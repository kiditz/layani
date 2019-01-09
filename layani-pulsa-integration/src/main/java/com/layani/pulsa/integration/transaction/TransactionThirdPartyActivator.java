package com.layani.pulsa.integration.transaction;

import com.layani.pulsa.integration.transaction.api.ApiCaller;
import com.layani.pulsa.integration.utils.Constant;
import com.layani.pulsa.service.constant.ErrorConstant;
import com.layani.pulsa.service.order.EditOrder;
import org.apache.commons.lang.StringUtils;
import org.slerp.core.Domain;
import org.slerp.core.business.BusinessFunction;
import org.slerp.core.business.BusinessTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class TransactionThirdPartyActivator implements ActivatorMessageDomain {
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private BusinessFunction isPartnerDepositExistsById;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private BusinessTransaction addOrderPayload;
    @Autowired
    private EditOrder editOrder;
    @Override
    public Message<Domain> execute(Message<Domain> message) {
        Domain payload = message.getPayload();
        log.debug("Payload : {}", payload);
        Domain inputOrderPayload = new Domain();
        inputOrderPayload.put("payload", payload.toString());
        inputOrderPayload.put("orderId", payload.getLong("id"));
        log.debug("Write Payload");
        this.addOrderPayload.handle(inputOrderPayload);
        Domain partner = payload.getDomain("partnerProduct").getDomain("partner");
        Domain partnerDepositExists = isPartnerDepositExistsById.handle(partner);
        if(!partnerDepositExists.getBoolean("exists")){
            return TransactionResult.fail(payload, ErrorConstant.PRODUCT_NOT_EXISTS, StringUtils.EMPTY);
        }
        Domain partnerDeposit = partnerDepositExists.getDomain("partnerDeposit");
        Double balanceAmount = partnerDeposit.getDouble("balanceAmount");
        if(balanceAmount < 0){
            return TransactionResult.fail(payload, ErrorConstant.PRODUCT_NOT_EXISTS, StringUtils.EMPTY);
        }

        ApiCaller apiCaller = (ApiCaller) context.getBean("API_"+ partner.getString("code"));
        Message<Domain> caller = apiCaller.execute(payload);
        //Edit Reqid untuk keperluan callback
        if(payload.getString("status").equalsIgnoreCase(Constant.TransactionStatus.IN_PROGRESS)){
            editOrder.handle(payload);
        }
        return caller;
    }


}
