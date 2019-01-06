package com.layani.pulsa.service.order;

import com.layani.pulsa.entity.OrderPayload;
import com.layani.pulsa.repository.OrderPayloadRepository;
import org.slerp.core.Domain;
import org.slerp.core.business.DefaultBusinessFunction;
import org.slerp.core.validation.KeyValidation;
import org.slerp.core.validation.NotBlankValidation;
import org.slerp.core.validation.NumberValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@KeyValidation("reqid")
@NumberValidation({})
@NotBlankValidation({})
public class FindOrderPayloadByReqid extends DefaultBusinessFunction {
	@Autowired
	private OrderPayloadRepository orderPayloadRepository;
	@Override
	public Domain handle(Domain orderPayloadDomain) {
		OrderPayload orderPayload = orderPayloadRepository.findOrderPayloadByReqid(orderPayloadDomain.getString("reqid"));
		return new Domain(orderPayload);
	}
}