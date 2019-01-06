package com.layani.pulsa.service.order;

import org.slerp.core.business.DefaultBusinessFunction;
import com.layani.pulsa.entity.PartnerProductPurchasePrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.slerp.core.validation.KeyValidation;
import org.slerp.core.validation.NumberValidation;
import org.slerp.core.validation.NotBlankValidation;
import org.slerp.core.Domain;
import com.layani.pulsa.repository.PartnerProductPurchasePriceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

@Service
@KeyValidation("productId")
@NumberValidation({"productId"})
@NotBlankValidation({})
public class IsProductPurchasePriceExistByProductId extends DefaultBusinessFunction {

	@Autowired
	private PartnerProductPurchasePriceRepository partnerProductPurchasePriceRepository;

	@Override
	public Domain handle(Domain partnerProductPurchasePriceDomain) {
		Page<PartnerProductPurchasePrice> partnerProductPurchasePrice = partnerProductPurchasePriceRepository.findProductPurchasePrice(partnerProductPurchasePriceDomain.getLong("productId"), new Date(),PageRequest.of(0, 1, Sort.Direction.ASC, "purchasePrice"));
		Domain result = new Domain();
		if(partnerProductPurchasePrice.getTotalElements() > 0){
			result.put("exists", true);
			result.put("partnerProductPurchasePrice", partnerProductPurchasePrice.getContent().get(0));
		}else{
			result.put("exists", false);
		}

		return result;
	}
}