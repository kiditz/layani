package com.layani.pulsa.integration.transaction;

import com.layani.pulsa.integration.utils.Constant;
import com.layani.pulsa.integration.utils.Localization;
import com.layani.pulsa.integration.utils.TemplateHandler;
import com.layani.pulsa.service.order.EditOrder;
import org.slerp.core.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class TransactionCheckPostPaidActivator implements ActivatorMessageDomain {
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private EditOrder editOrder;
    @Autowired
    private TemplateHandler template;
    @Autowired
    private Localization localization;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public Message<Domain> execute(Message<Domain> message) {
        log.debug("Input : {}", message.getPayload());
        Domain payload = message.getPayload();

        Domain order = editOrder.handle(payload);
        //Put Request Id
        payload.put("reqid", order.getString("reqid"));
        payload.put("remark", localization.getMessage(payload.getString("remark")));
        //Change Format Date for message
        try {
            String formatDatetime = format.format(new Date(payload.getLong("createdAt")));
            payload.put("createdAt", formatDatetime);
        }catch (Exception e){
            payload.put("createdAt", format.format(new Date()));
        }
        String messageNotification  = template.build("transaction_check_post_paid", payload);
        log.debug("Mesage Nontification : {}", messageNotification);
        Domain notification = new Domain();
        notification.put("title", localization.getMessage(Constant.NotificationValue.TRX_CHECK_POST_PAID));
        notification.put("body", messageNotification);
        notification.put("userId", order.getLong("userId"));
        payload.put("notification", notification);
        Domain postPaid = payload.getDomain("postPaid");
        payload.put("data", postPaid);
        return MessageBuilder.withPayload(payload).build();
    }
}
