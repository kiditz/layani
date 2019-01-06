package com.layani.pulsa.service.order;

import org.slerp.core.business.DefaultBusinessFunction;
import com.layani.pulsa.entity.Order;
import org.springframework.stereotype.Service;
import org.slerp.core.validation.KeyValidation;
import org.slerp.core.validation.NumberValidation;
import org.slerp.core.validation.NotBlankValidation;
import org.slerp.core.Domain;
import com.layani.pulsa.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Service
@KeyValidation("id")
@NumberValidation({})
@NotBlankValidation({})
public class IsOrderExistsById extends DefaultBusinessFunction {

	@Autowired
	private OrderRepository orderRepository;

	@Override
	public Domain handle(Domain orderDomain) {
		Long orderId = orderDomain.getLong("id");
		Optional<Order> orderOptional = orderRepository.findById(orderId);
		Domain result = new Domain();
		if(orderOptional.isPresent()){
			result.put("order", orderOptional.get());
			result.put("exists", true);
		}else{
			result.put("exists", false);
		}
		return result;
	}
}