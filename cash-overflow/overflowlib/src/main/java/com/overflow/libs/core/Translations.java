package com.overflow.libs.core;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Created by kiditz on 22/10/17.
 */

public class Translations {
    private Data data;

    public Translations(Context context) {
        this(context, getCurrentLocale(context).getLanguage());
    }

    public Translations(Context context, String language) {
        if(language == null){
            language = "";
        }
        String filename ="translations_"+ language +".json";
        try {
            InputStream in = context.getAssets().open(filename);
            String data = StreamUtils.copyStreamToString(in);
            this.data =new Data(data);
        } catch (Exception e) {
            try {
                InputStream in = context.getAssets().open("translations.json");
                String data = StreamUtils.copyStreamToString(in);
                this.data =new Data(data);
            } catch (IOException e1) {
                e1.printStackTrace();
                throw new RuntimeException("Please set "+filename+" in assets");
            }
        }
    }

    public String get(String key){
        return this.data.containsKey(key) ? data.get(key).toString() : key;
    }
    public boolean has(String key){
        return this.data.containsKey(key);
    }

    public Data getData() {
        return data;
    }

    public static Locale getCurrentLocale(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return context.getResources().getConfiguration().getLocales().get(0);
        } else{
            return context.getResources().getConfiguration().locale;
        }
    }
}
