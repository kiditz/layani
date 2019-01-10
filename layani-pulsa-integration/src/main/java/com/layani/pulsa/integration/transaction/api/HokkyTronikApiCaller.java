package com.layani.pulsa.integration.transaction.api;

import com.layani.pulsa.integration.RetrofitClient;
import com.layani.pulsa.integration.model.HokkyTronik;
import com.layani.pulsa.integration.transaction.TransactionResult;
import com.layani.pulsa.integration.utils.Constant;
import com.layani.pulsa.integration.utils.MessageMapping;
import com.layani.pulsa.service.constant.ServiceConstant;
import org.apache.commons.lang.StringUtils;
import org.slerp.core.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import retrofit2.Response;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

@Component("API_HOKKYTRONIK")
public class HokkyTronikApiCaller implements ApiCaller {

    @Value("${hokkytronik.apiKey}")
    private String apiKey;

    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private MessageMapping messageMapping;

    @Override
    public Message<Domain> execute(Domain payload) {
        String requestId = ServiceConstant.getReqid(payload.getLong("id"));
        Domain partnerProduct = payload.getDomain("partnerProduct");
        Domain partner = partnerProduct.getDomain("partner");
        String url = partner.getString("url");
        HttpHeaders headers = new HttpHeaders();
        headers.add("api-key", apiKey);
        // Create Http Entity
        Map<String, String> input = new HashMap<>();
        input.put("ref_idtrx", requestId);
        input.put("kode", partnerProduct.getString("code"));
        input.put("tujuan", payload.getString("msisdn"));
        HokkyTronik hokkyTronik = RetrofitClient.retrofit(url, payload).create(HokkyTronik.class);
        try {
            Response<Domain> response = hokkyTronik.postOrder(apiKey, input).execute();
            log.debug("Reponse : {}", response);
            if(response.isSuccessful()){
                Domain body = response.body();
                assert body != null;
                if(body.containsKey("response") && body.getString("response").equalsIgnoreCase("gagal")){
                    String message = body.getString("message");
                    String remark = messageMapping.getMessage(message, partner.getLong("id"));
                    return TransactionResult.fail(payload, remark, StringUtils.EMPTY);
                }
            }else{
                assert response.errorBody() != null;
                Domain errorBody = new Domain(response.errorBody().string());
                if(errorBody.containsKey("response") && errorBody.getString("response").equalsIgnoreCase("gagal")){
                    String message = errorBody.getString("message");
                    String remark = messageMapping.getMessage(message, partner.getLong("id"));
                    return TransactionResult.fail(payload, remark, StringUtils.EMPTY);
                }
            }
        } catch (IOException e) {
            log.error("Exception Call", e);
            if (e.getClass().isAssignableFrom(ConnectException.class)){
                return TransactionResult.fail(payload, Constant.NotificationValue.TRX_ERROR, "Tidak dapat terhubung dengan hokky tronik");
            }
            return TransactionResult.progress(payload);
        }
        return TransactionResult.progress(payload);
    }
}
