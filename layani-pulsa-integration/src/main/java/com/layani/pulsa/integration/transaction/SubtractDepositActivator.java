package com.layani.pulsa.integration.transaction;

import com.layani.pulsa.integration.utils.Constant;
import com.layani.pulsa.service.deposit.EditDeposit;
import org.slerp.core.CoreException;
import org.slerp.core.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class SubtractDepositActivator implements ActivatorMessageDomain {
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private EditDeposit editDeposit;
    @Override
    public Message<Domain> execute(Message<Domain> message) {
        log.debug("Input : {}", message);
        Domain payload = message.getPayload();
        Domain inputDeposit = new Domain();
        //Make sure to use minus value in transaction
        inputDeposit.put("amount", -payload.getDouble("sellPrice"));
        inputDeposit.put("outletId", payload.getLong("outletId"));
        try {
            Domain deposit = editDeposit.handle(inputDeposit);
            payload.put("deposit", deposit);
            payload.put("status", Constant.TransactionStatus.IN_PROGRESS);
        }catch (CoreException e){
            payload.put("status", Constant.TransactionStatus.FAIL);
            payload.put("remark", e.getMessage());
        }
        return MessageBuilder.withPayload(payload).build();
    }
}
