package com.layani.pulsa.integration.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slerp.core.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

@Component
public class TemplateHandler {
    @Autowired
    private Configuration freemarkerConfig;
    private Logger log = LoggerFactory.getLogger(getClass());

    public String build(String template, Domain payload ){
        try {
            Template t = freemarkerConfig.getTemplate(template.concat(".ftl"));
            return FreeMarkerTemplateUtils.processTemplateIntoString(t, payload);
        }catch (Exception e){
            log.error("Template Error : {}", e);
            return " ";
        }
    }
}
