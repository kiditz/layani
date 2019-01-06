package com.layani.pulsa.integration.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class Localization {
    @Autowired
    private MessageSource messageSource;
    public String getMessage(String value){
        try {
            return messageSource.getMessage(value, new Object[]{}, Locale.US);
        }catch (NoSuchMessageException e){
            return value;
        }
    }
}
