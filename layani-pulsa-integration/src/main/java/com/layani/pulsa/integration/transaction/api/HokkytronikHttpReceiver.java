package com.layani.pulsa.integration.transaction.api;

import com.layani.pulsa.integration.transaction.ActivatorMessageString;
import com.layani.pulsa.integration.transaction.TransactionResult;
import com.layani.pulsa.integration.utils.Constant;
import com.layani.pulsa.integration.utils.MessageMapping;
import com.layani.pulsa.service.constant.ErrorConstant;
import com.layani.pulsa.service.order.FindOrderPayloadByReqid;
import com.layani.pulsa.service.order.IsOrderExistsById;
import org.apache.commons.lang.StringUtils;
import org.slerp.core.CoreException;
import org.slerp.core.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class HokkytronikHttpReceiver implements ActivatorMessageString {
    @Autowired
    private FindOrderPayloadByReqid findOrderPayloadByReqid;
    @Autowired
    private IsOrderExistsById isOrderExistsById;
    @Autowired
    private MessageMapping messageMapping;

    private Logger log = LoggerFactory.getLogger(getClass());
    @Override
    public Message<Domain> execute(Message<String> message) {
        log.info("Message : {}", message.getPayload());
        try {
            Domain payload = new Domain(message.getPayload());
            log.info("Payload : {}", payload);
            Domain inputPayload = new Domain();
            inputPayload.put("reqid", payload.getString("ref_idtrx"));
            Domain orderPayload = new Domain(findOrderPayloadByReqid.handle(inputPayload).getString("payload"));
            Domain partnerProduct = orderPayload.getDomain("partnerProduct");
            Domain partner = partnerProduct.getDomain("partner");
            Domain orderExists = isOrderExistsById.handle(orderPayload);
            if(!orderExists.getBoolean("exists")){
                throw new CoreException(ErrorConstant.ORDER_NOT_FOUND);
            }
            Domain order = orderExists.getDomain("order");
            //Handle Order Is Not In Progress
            if(order.getString("status").equalsIgnoreCase(Constant.TransactionStatus.SUCCESS)){
                throw new CoreException(ErrorConstant.ORDER_IS_NOT_IN_PROGRESS);
            }
            if(payload.getString("status").equalsIgnoreCase("sukses")){
                if(payload.containsKey("catatan")){
                    orderPayload.put("sn", TransactionResult.getSerialNumber(payload.getString("catatan")));
                    return TransactionResult.success(orderPayload);
                }
            }else if (payload.getString("status").equalsIgnoreCase("gagal")){
                if(payload.containsKey("catatan")){
                    String note = payload.getString("catatan");
                    String remark = messageMapping.getMessage(note, partner.getLong("id"));
                    return TransactionResult.fail(payload, remark, StringUtils.EMPTY);
                }
            }
            return TransactionResult.progress(new Domain());
        }catch (CoreException e){
            log.error("CoreException : {}", e.getMessage());
            return TransactionResult.progress(new Domain());
        }catch (Exception e){
            log.error("Exception : {}", e);
            return TransactionResult.progress(new Domain());
        }
    }
}
