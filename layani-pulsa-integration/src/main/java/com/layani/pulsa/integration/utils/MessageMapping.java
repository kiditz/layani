package com.layani.pulsa.integration.utils;

import org.slerp.core.CoreException;
import org.slerp.core.Domain;
import org.slerp.core.business.BusinessFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageMapping {
    @Autowired
    private BusinessFunction findMessageMapping;
    public String getMessage(String message, Long partnerId){
        Domain messageMapping = new Domain();
        messageMapping.put("message", message);
        messageMapping.put("partnerId", partnerId);
        try {
            Domain result = findMessageMapping.handle(messageMapping);
            return result.getDomain("messageMapping").getString("layaniMessage");
        }catch (CoreException e){
            return e.getMessage();
        }
    }
}
