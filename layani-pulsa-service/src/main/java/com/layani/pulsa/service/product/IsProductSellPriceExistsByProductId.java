package com.layani.pulsa.service.product;

import org.slerp.core.business.DefaultBusinessFunction;
import com.layani.pulsa.entity.ProductSellPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.slerp.core.validation.KeyValidation;
import org.slerp.core.validation.NumberValidation;
import org.slerp.core.validation.NotBlankValidation;
import org.slerp.core.Domain;
import com.layani.pulsa.repository.ProductSellPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
/**
 * @apiNote This class used to find product active product sell price even when we have so many product sell price
 * Who's active or inactive
 * @author kiditz
 * */
@Service
@KeyValidation("productId")
@NumberValidation({"productId"})
@NotBlankValidation({})
public class IsProductSellPriceExistsByProductId extends DefaultBusinessFunction {

	@Autowired
	private ProductSellPriceRepository productSellPriceRepository;

	@Override
	public Domain handle(Domain productSellPriceDomain) {
		Page<ProductSellPrice> productSellPrice = productSellPriceRepository.findProductSellPriceByProductId(productSellPriceDomain.getLong("productId"), new Date(), PageRequest.of(0, 1, Sort.Direction.ASC, "sellPrice"));
		Domain result = new Domain();
		if(productSellPrice.getTotalElements() > 0){
			result.put("exists", true);
			result.put("productSellPrice", productSellPrice.getContent().get(0));
		}else{
			result.put("exists", false);
		}

		return result;
	}
}