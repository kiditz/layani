package com.layani.pulsa.integration.utils;

import org.slerp.core.Domain;

import java.util.Map;

public class DomainUtils {
    public static Domain convertKeyToCamelCase(Domain input){
        Domain result = new Domain();
        for (Map.Entry<Object, Object> entry: input.entrySet()) {
            result.put(SnakeCase.convertToCamel(entry.getKey().toString()), entry.getValue());
        }
        return result;
    }


}
