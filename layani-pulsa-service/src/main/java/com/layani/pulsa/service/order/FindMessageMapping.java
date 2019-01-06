package com.layani.pulsa.service.order;

import com.layani.pulsa.service.constant.ErrorConstant;
import org.slerp.core.CoreException;
import org.slerp.core.business.DefaultBusinessFunction;
import com.layani.pulsa.entity.OrderMessageMapping;
import org.springframework.stereotype.Service;
import org.slerp.core.validation.KeyValidation;
import org.slerp.core.validation.NumberValidation;
import org.slerp.core.validation.NotBlankValidation;
import org.slerp.core.Domain;
import com.layani.pulsa.repository.OrderMessageMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@KeyValidation({"partnerId", "message"})
@NumberValidation({})
@NotBlankValidation({})
public class FindMessageMapping extends DefaultBusinessFunction {

	@Autowired
	private OrderMessageMappingRepository orderMessageMappingRepository;

	@Override
	public Domain handle(Domain orderMessageMappingDomain) {
		String message = orderMessageMappingDomain.getString("message");
		Long partnerId = orderMessageMappingDomain.getLong("partnerId");
		OrderMessageMapping orderMessageMapping = orderMessageMappingRepository.findMessageMapping(partnerId, message);
		if(orderMessageMapping == null){
			throw new CoreException(ErrorConstant.PRODUCT_NOT_EXISTS);
		}
		return new Domain().put("messageMapping", orderMessageMapping);
	}
}