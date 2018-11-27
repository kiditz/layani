package com.overflow.libs.core;

public class SnakeCase {
    public static String convertToCamel(String data){
        String[] split = data.split("_");
        if(split.length != 0){
            StringBuilder builder = new StringBuilder();
            builder.append(split[0]);
            for (int i = 1; i < split.length; i++) {
                String str = split[i];
                String firstString = str.substring(0, 1).toUpperCase();
                String bodyString = str.substring(1, str.length());
                builder.append(firstString + bodyString);
            }
            return builder.toString();
        }
        return data;
    }
}
