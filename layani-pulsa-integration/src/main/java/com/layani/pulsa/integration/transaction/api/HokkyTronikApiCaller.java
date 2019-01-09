package com.layani.pulsa.integration.transaction.api;

import com.layani.pulsa.integration.transaction.TransactionResult;
import com.layani.pulsa.integration.utils.MessageMapping;
import com.layani.pulsa.service.constant.ErrorConstant;
import com.layani.pulsa.service.constant.ServiceConstant;
import org.apache.commons.lang.StringUtils;
import org.slerp.core.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component("API_HOKKYTRONIK")
public class HokkyTronikApiCaller implements ApiCaller {

    @Value("${hokkytronik.apiKey}")
    private String apiKey;
    @Autowired
    private RestTemplate template;
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private MessageMapping messageMapping;
    @Override
    public Message<Domain> execute(Domain payload) {
        Long requestId = payload.getLong("id");
        Domain partnerProduct = payload.getDomain("partnerProduct");
        Domain partner = partnerProduct.getDomain("partner");
        String url = partner.getString("url");
        HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.APPLICATION_JSON);
        //headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add("api-key", apiKey);
        // Create Http Entity
        Domain input = new Domain();
        input.put("ref_idtrx", requestId);
        input.put("kode", partnerProduct.getString("code"));
        input.put("tujuan", payload.getString("msisdn"));

        HttpEntity<String> request = new HttpEntity<>(input.toString(), headers);
        try {
            log.info("<<Request>> :{}", request.toString());
            payload.put("request", request.toString());
            ResponseEntity<String> resp = template.exchange(url, HttpMethod.POST, request, String.class);
            log.info("BODY : {}", resp.getBody());
            Domain body = new Domain(resp.getBody());
            if(resp.getStatusCode() == HttpStatus.OK || resp.getStatusCode() == HttpStatus.CREATED){
                return TransactionResult.progress(payload);
            }else{
                if(body.containsKey("message")){
                    String message = body.getString("mess   age");
                    String remark = messageMapping.getMessage(message, partner.getLong("id"));
                    return TransactionResult.fail(payload, remark, StringUtils.EMPTY);
                }
                return TransactionResult.fail(payload, ErrorConstant.PRODUCT_NOT_EXISTS, StringUtils.EMPTY);
            }
        }catch (HttpClientErrorException e){
            log.error("Exception : {}", e.getResponseBodyAsString());
            Domain resp = new Domain(e.getResponseBodyAsString());
            if(resp.getString("response").equalsIgnoreCase("gagal")){
                return TransactionResult.fail(payload, ErrorConstant.PRODUCT_NOT_EXISTS, StringUtils.EMPTY);
            }
            return TransactionResult.progress(payload);
        }catch (Exception e){
            log.error("Exception : {}", e);
            return TransactionResult.progress(payload);
        }
    }
}
