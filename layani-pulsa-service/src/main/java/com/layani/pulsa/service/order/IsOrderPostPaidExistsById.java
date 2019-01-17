package com.layani.pulsa.service.order;

import com.layani.pulsa.entity.Notification;
import com.layani.pulsa.entity.OrderPostPaid;
import com.layani.pulsa.repository.NotificationRepository;
import com.layani.pulsa.repository.OrderPostPaidRepository;
import org.slerp.core.CoreException;
import org.slerp.core.Domain;
import org.slerp.core.business.DefaultBusinessFunction;
import org.slerp.core.validation.KeyValidation;
import org.slerp.core.validation.NotBlankValidation;
import org.slerp.core.validation.NumberValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@KeyValidation("id")
@NumberValidation({})
@NotBlankValidation({})
public class IsOrderPostPaidExistsById extends DefaultBusinessFunction {

	@Autowired
	private OrderPostPaidRepository orderPostPaidRepository;

	@Override
	public Domain handle(Domain orderDomain) {
		Optional<OrderPostPaid> orderPostPaidOptional = orderPostPaidRepository.findById(orderDomain.getLong("id"));
		if(orderPostPaidOptional.isPresent()){
			return Objects.requireNonNull(new Domain().put("exists", true)).put("postPaid", orderPostPaidOptional.get());
		}else{
			return new Domain().put("exists", false);
		}
	}
}