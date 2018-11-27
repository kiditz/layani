package com.overflow.cash.net;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.overflow.libs.core.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MultipartUtils {
    public static RequestBody createValue(String value){
        return RequestBody.create(MultipartBody.FORM, value);
    }

    public static MultipartBody.Part createFile(String key, Intent data, Activity activity) throws IOException {
        Uri uri = data.getData();
        assert uri != null;

        String filename = StreamUtils.getFileNameFromIntentData(activity, uri);
        InputStream in = activity.getContentResolver().openInputStream(uri);
        RequestBody reqFile = RequestBody.create(
                MediaType.parse(Objects.requireNonNull(activity.getContentResolver().getType(uri))),
                StreamUtils.copyStreamToBytes(in)
        );
        return  MultipartBody.Part.createFormData(key, filename, reqFile);
    }

    /**
     * Upload with html file for add_document in the slerp server
     * @param key is the key required from server
     * @param data is the task html file
     * */
    public static MultipartBody.Part createFileHtml(String key, String data) {
        String filename = UUID.randomUUID().toString().substring(0, 8) + ".html";
        RequestBody reqFile = RequestBody.create(MediaType.parse("text/html"), data);
        return MultipartBody.Part.createFormData(key, filename, reqFile);
    }
}
