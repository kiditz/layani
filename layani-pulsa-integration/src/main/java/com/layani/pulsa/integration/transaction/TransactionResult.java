package com.layani.pulsa.integration.transaction;

import com.layani.pulsa.integration.utils.Constant;
import org.apache.commons.lang.StringUtils;
import org.slerp.core.Domain;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransactionResult {
    public static Message<Domain> fail(Domain payload, String remark, String message){
        payload.put("remark", remark);
        payload.put("status", Constant.TransactionStatus.FAIL);
        if (StringUtils.isNotEmpty(message)){
            payload.put("send_mail", true);
            payload.put("mail_message", message);
        }
        return MessageBuilder.withPayload(payload).build();
    }

    public static Message<Domain> success(Domain payload){
        payload.put("status", Constant.TransactionStatus.SUCCESS);
        payload.put("remark", Constant.NotificationValue.TRX_SUCCESS);
        return MessageBuilder.withPayload(payload).build();
    }

    public static Message<Domain> progress(Domain payload){
        payload.put("status", Constant.TransactionStatus.IN_PROGRESS);
        payload.put("remark", Constant.NotificationValue.TRX_IN_PROGRESS);
        return MessageBuilder.withPayload(payload).build();
    }

    public static Message<Domain> checkPostPaid(Domain payload){
        payload.put("status", Constant.TransactionStatus.CHECK_POST_PAID);
        payload.put("remark", Constant.NotificationValue.TRX_CHECK_POST_PAID);
        return MessageBuilder.withPayload(payload).build();
    }

    public static String getSerialNumber(String note){
        Pattern pattern = Pattern.compile("(SN):(.*)");
        Matcher matcher = pattern.matcher(note);
        if(matcher.find()){
            return matcher.group(2);
        }
        return note;
    }
}
