package com.layani.pulsa.integration.transaction;

import com.layani.pulsa.integration.utils.Constant;
import com.layani.pulsa.integration.utils.DomainUtils;
import com.layani.pulsa.service.constant.ErrorConstant;
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
public class TransactionTransformerPostPaid implements ActivatorMessageString {
    @Autowired
    private BusinessFunction isProductPurchasePriceExistByProductId;
    @Autowired
    private BusinessFunction isProductSellPriceExistsByProductId;
    @Autowired
    private BusinessFunction isOrderPostPaidExistsById;

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
        Domain postPaid = isOrderPostPaidExistsById.handle(input);
        if(!postPaid.getBoolean("exists")){
            return TransactionResult.fail(input, ErrorConstant.ORDER_NOT_FOUND, StringUtils.EMPTY);
        }
        postPaid = postPaid.getDomain("postPaid");
        Double admCost = postPaid.getBigDecimal("admCost").doubleValue();
        Long numOfTrx = postPaid.getLong("numOfTrx");
        Double billAmount = postPaid.getBigDecimal("billAmount").doubleValue();
        Double postPaidAmount = postPaid.getBigDecimal("postPaidAmount").doubleValue();

        Double purchasePriceValue = productPurchasePrice.getBigDecimal("purchasePrice").doubleValue();
        Double sellPriceValue = productSellPrice.getBigDecimal("sellPrice").doubleValue();

        log.debug("Post Purchase Price = {} + {} ", postPaidAmount, purchasePriceValue);
        Double calculatePurchasePrice =  postPaidAmount + purchasePriceValue;
        Double calculateSellPrice =  billAmount + (admCost * numOfTrx) + purchasePriceValue + sellPriceValue;
        log.debug("Sell Price = {} + ({} * {}) + {} + {}", billAmount, admCost, numOfTrx, purchasePriceValue, sellPriceValue);
        Double cashBack = (admCost * numOfTrx) - sellPrice;
        input.put("product", productSellPrice.getDomain("productId"));
        input.put("sellPrice", calculateSellPrice);
        input.put("purchasePrice", calculatePurchasePrice);
        input.put("partnerProduct", productPurchasePrice.getDomain("partnerProductId"));
        input.put("partnerProductId", productPurchasePrice.getDomain("partnerProductId").getLong("id"));
        input.put("status", Constant.TransactionStatus.IN_PROGRESS);
        input.put("cashBack", cashBack);
        log.debug("TransactionResult : {}", input);
        return MessageBuilder.withPayload(input).build();
    }
}
