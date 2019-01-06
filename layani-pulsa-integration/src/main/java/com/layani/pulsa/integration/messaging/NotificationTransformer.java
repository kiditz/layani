package com.layani.pulsa.integration.messaging;

import com.layani.pulsa.integration.transaction.ActivatorMessageString;
import com.layani.pulsa.service.notification.FindNotification;
import com.layani.pulsa.service.notification.FindNotificationToken;
import org.apache.commons.lang.StringUtils;
import org.slerp.core.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
/**
 * @author kiditz
 * @apiNote Class is used to transform data from layani database into firebase message before send it to the user
 * */
@Component
public class NotificationTransformer implements ActivatorMessageString {
    @Value("${firebase.token}")
    private String firebaseToken;
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private FindNotification findNotification;
    @Autowired
    private FindNotificationToken findNotificationToken;

    @Override
    public Message<Domain> execute(Message<String> message){
        Domain payload = new Domain(message.getPayload());
        log.debug("Input : {}", payload);
        Domain notificationDomain = findNotification.handle(payload).getDomain("notification");
        Domain tokenDomain = findNotificationToken.handle(notificationDomain).getDomain("notificationToken");
        Domain transform = new Domain();
        transform.put("to", tokenDomain.getString("token"));
        transform.put("notification", notificationDomain);
        if(notificationDomain.containsKey("data") && StringUtils.isNotEmpty("data")){
            transform.put("data", new Domain(notificationDomain.getString("data")));
        }
        return MessageBuilder.withPayload(transform).setHeader(HttpHeaders.AUTHORIZATION, firebaseToken).build();
    }
}
