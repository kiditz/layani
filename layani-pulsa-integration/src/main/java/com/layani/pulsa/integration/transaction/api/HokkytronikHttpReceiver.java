package com.layani.pulsa.integration.transaction.api;

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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Objects;

@Component
public class HokkytronikHttpReceiver {
    @Autowired
    private FindOrderPayloadByReqid findOrderPayloadByReqid;
    @Autowired
    private IsOrderExistsById isOrderExistsById;
    @Autowired
    private MessageMapping messageMapping;
    private Logger log = LoggerFactory.getLogger(getClass());

    public Message<Domain> execute(Message<LinkedMultiValueMap<String, String>> message) {
        log.info("Type : {}", message.getPayload().getClass());
        log.info("Message : {}", message.getPayload());
        MultiValueMap<String, String> map = message.getPayload();
        try {
            Domain payload = new Domain(map.getFirst("content"));
            log.info("Payload : {}", payload);
            Domain inputPayload = new Domain();
            String reqId = payload.getString("idtrx");
            inputPayload.put("reqid", reqId);
            Domain orderPayload;
            try {
                orderPayload = new Domain(findOrderPayloadByReqid.handle(inputPayload).getString("payload"));
            } catch (NullPointerException e) {
                log.info("Payload tidak di temukan :{}", inputPayload.toString());
                return TransactionResult.progress(new Domain());
            }
            orderPayload.put("reqid", reqId);
            orderPayload.put("request", "Callback");
            orderPayload.put("response", payload.toString());
            Domain partnerProduct = orderPayload.getDomain("partnerProduct");
            Domain partner = partnerProduct.getDomain("partner");
            Domain orderExists = isOrderExistsById.handle(orderPayload);
            if (!orderExists.getBoolean("exists")) {
                throw new CoreException(ErrorConstant.ORDER_NOT_FOUND);
            }
            Domain order = orderExists.getDomain("order");
            //Handle Order Is Not In Progress
            if (order.getString("status").equalsIgnoreCase(Constant.TransactionStatus.SUCCESS)) {
                throw new CoreException(ErrorConstant.ORDER_IS_NOT_IN_PROGRESS);
            }
            //PPOB Check

            if (Objects.requireNonNull(payload.getString("status")).equalsIgnoreCase("sukses")) {
                if (payload.getString("kode_produk").startsWith("HCEK")) {
                    return buildMessageForCheck(orderPayload, payload);
                }
                if (payload.containsKey("catatan")) {
                    String sn = messageMapping.getSerialNumber(payload.getString("catatan"));
                    log.info("SN:{}", sn);

                    orderPayload.put("sn", sn);
                    return TransactionResult.success(orderPayload);
                } else {
                    return TransactionResult.progress(orderPayload);
                }
            } else if (Objects.requireNonNull(payload.getString("status")).equalsIgnoreCase("gagal")) {
                if (payload.containsKey("catatan")) {
                    String note = payload.getString("catatan");
                    log.info("Catatan : {}", note);
                    String remark = messageMapping.getMessage(note, partner.getLong("id"));
                    return TransactionResult.fail(orderPayload, remark, StringUtils.EMPTY);
                }
            }
            return TransactionResult.progress(new Domain());
        } catch (CoreException e) {
            log.error("CoreException : {}", e.getMessage());
            return TransactionResult.progress(new Domain());
        } catch (Exception e) {
            log.error("Exception : {}", e);
            return TransactionResult.progress(new Domain());
        }
    }

    private Message<Domain> buildMessageForCheck(Domain orderPayload, Domain payload) {
        String note = payload.getString("catatan");
        note = note.substring(0, note.indexOf("."));
        Double postPaidAmount = payload.getDouble("tagihan");
        String[] splitNote = note.split("/");
        String customerName = splitNote[0];
        Long numOfTrx = Long.valueOf(splitNote[4].replaceAll("[^0-9]", ""));
        String postPaidMonth = splitNote[5];
        Double admCost = Double.parseDouble(splitNote[splitNote.length - 1].replaceAll("[^0-9]", ""));
        Domain inputPostPaid = new Domain();
        inputPostPaid.put("customerName", customerName);
        inputPostPaid.put("admCost", admCost * numOfTrx);
        inputPostPaid.put("numOfTrx", numOfTrx);
        inputPostPaid.put("postPaidAmount", postPaidAmount);
        inputPostPaid.put("postPaidMonth", postPaidMonth);
        inputPostPaid.put("billAmount", postPaidAmount - (admCost * numOfTrx));
        inputPostPaid.put("id", orderPayload.getLong("id"));
        orderPayload.put("postPaid", inputPostPaid);
        return TransactionResult.checkPostPaid(orderPayload);
    }
}
