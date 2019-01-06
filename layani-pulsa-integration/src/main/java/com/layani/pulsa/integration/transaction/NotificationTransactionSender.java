package com.layani.pulsa.integration.transaction;

import com.layani.pulsa.service.notification.AddNotification;
import org.slerp.core.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class NotificationTransactionSender implements ActivatorMessageDomain{
    @Autowired
    private AddNotification addNotification;
    @Autowired
    private KafkaTemplate<String, String> template;
    @Value("${kafka.notification}")
    private String topic;
    private Logger log = LoggerFactory.getLogger(getClass());
    @Override
    public Message<Domain> execute(Message<Domain> message) {
        Domain payload = message.getPayload();
        log.debug("Input : {}", payload);
        Domain notification = payload.getDomain("notification");
        if(!notification.containsKey("data")){
            notification.put("data", payload.toString());
        }
        notification = addNotification.handle(notification);
        log.debug("Notification : {}", notification);
        template.send(topic, "", notification.toString());
        return MessageBuilder.withPayload(payload).build();
    }
}
