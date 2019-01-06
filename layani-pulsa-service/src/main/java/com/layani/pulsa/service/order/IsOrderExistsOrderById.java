package com.layani.pulsa.service.order;

import com.layani.pulsa.entity.Notification;
import com.layani.pulsa.entity.Order;
import com.layani.pulsa.repository.NotificationRepository;
import com.layani.pulsa.repository.OrderRepository;
import com.layani.pulsa.service.constant.ErrorConstant;
import org.slerp.core.CoreException;
import org.slerp.core.Domain;
import org.slerp.core.business.DefaultBusinessFunction;
import org.slerp.core.validation.KeyValidation;
import org.slerp.core.validation.NotBlankValidation;
import org.slerp.core.validation.NumberValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@KeyValidation("id")
@NumberValidation({"id"})
@NotBlankValidation({})
public class IsOrderExistsOrderById extends DefaultBusinessFunction {

	@Autowired
	private OrderRepository orderRepository;

	@Override
	public Domain handle(Domain notificationDomain) {
		Long id = notificationDomain.getLong("id");
		Optional<Order> optional = orderRepository.findById(id);
		if(optional.isPresent()){
			return new Domain().put("exists", true).put("order", optional.get());
		}else{
			return new Domain().put("exists", false);
		}
	}
}