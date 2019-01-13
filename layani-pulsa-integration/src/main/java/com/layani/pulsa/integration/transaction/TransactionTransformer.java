package com.layani.pulsa.integration.transaction;

import com.layani.pulsa.integration.utils.Constant;
import com.layani.pulsa.integration.utils.DomainUtils;
import com.layani.pulsa.service.constant.ErrorConstant;
import com.layani.pulsa.service.order.IsProductPurchasePriceExistByProductId;
import org.apache.commons.lang.StringUtils;
import org.slerp.core.Domain;
import org.slerp.core.business.BusinessFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class TransactionTransformer implements ActivatorMessageString {
    @Autowired
    private IsProductPurchasePriceExistByProductId isProductPurchasePriceExistByProductId;
    @Autowired
    private BusinessFunction isProductSellPriceExistsByProductId;
    
    private Logger log = LoggerFactory.getLogger(getClass());
    @Override
    public Message<Domain> execute(Message<String> message) {
        Domain input = DomainUtils.convertKeyToCamelCase(new Domain(message.getPayload()));
        log.debug("Input : {}", input);
        Domain productPurchasePrice = isProductPurchasePriceExistByProductId.handle(input);
        Domain productSellPrice = isProductSellPriceExistsByProductId.handle(input);
        input.put("id", input.getLong("orderPulsaId"));
        if(!productPurchasePrice.getBoolean("exists")){
            log.info("Purchase not exists");
            return TransactionResult.fail(input, ErrorConstant.PRODUCT_NOT_EXISTS, StringUtils.EMPTY);
        }

        if(!productSellPrice.getBoolean("exists")){
            log.info("Sellprice not exists");
            return TransactionResult.fail(input, ErrorConstant.PRODUCT_NOT_EXISTS, StringUtils.EMPTY);
        }
        //Manage Sell Price Domain after knowing it is exists
        productSellPrice = productSellPrice.getDomain("productSellPrice");
        productPurchasePrice = productPurchasePrice.getDomain("partnerProductPurchasePrice");
        log.debug("Product Sell Price : {}", productSellPrice);
        log.debug("Product Purchase Price : {}", productPurchasePrice);
        Double sellPrice = productSellPrice.getDouble("sellPrice");
        Double purchasePrice = productPurchasePrice.getDouble("purchasePrice");
        if(sellPrice < purchasePrice){
            return TransactionResult.fail(input, ErrorConstant.PRODUCT_NOT_EXISTS, StringUtils.EMPTY);
        }
        input.put("product", productSellPrice.getDomain("productId"));
        input.put("sellPrice", productSellPrice.getBigDecimal("sellPrice"));
        input.put("purchasePrice", productPurchasePrice.getBigDecimal("purchasePrice"));

        input.put("partnerProduct", productPurchasePrice.getDomain("partnerProductId"));
        input.put("partnerProductId", productPurchasePrice.getDomain("partnerProductId").getLong("id"));
        input.put("status", Constant.TransactionStatus.IN_PROGRESS);
        log.debug("TransactionResult : {}", input);
        return MessageBuilder.withPayload(input).build();
    }
}
