package com.layani.pulsa.integration.messaging;

import org.slerp.core.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * This class is used to send message from {@link NotificationTransformer}
 * into firebase service by using {@link org.springframework.web.client.RestTemplate}
 *
 * @author kiditz
 */
@Component
public class NotificationSenderActivator {
    @Autowired
    private RestTemplate template;
    @Value("${firebase.url}")
    private String url;
    private Logger log = LoggerFactory.getLogger(getClass());

    public Message<Domain> execute(Message<Domain> message) {
        Domain payload = message.getPayload();
        log.info("Payload : {}", payload);
        Map<String, Object> messageHeaders = message.getHeaders();
        // Prepare the header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        messageHeaders.forEach((k, v) -> headers.add(k, v.toString()));
        // Create Http Entity
        HttpEntity<Domain> request = new HttpEntity<>(payload, headers);
        // Send Message
        log.info("Request : {}", request.toString());
        ResponseEntity<Domain> response = template.exchange(url, HttpMethod.POST, request, Domain.class);
        log.info("Result Http : {}", response.toString());
        if(response.getStatusCode() == HttpStatus.OK){
            Domain body = response.getBody();
            assert body != null;
            return MessageBuilder.withPayload(body).build();
        }else{
            return MessageBuilder.withPayload(payload).build();
        }


    }
}
