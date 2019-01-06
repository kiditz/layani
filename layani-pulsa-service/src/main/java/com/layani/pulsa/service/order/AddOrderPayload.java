package com.layani.pulsa.service.order;

import com.layani.pulsa.entity.Order;
import com.layani.pulsa.repository.OrderRepository;
import com.layani.pulsa.service.constant.ErrorConstant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.layani.pulsa.repository.OrderPayloadRepository;
import org.slerp.core.Domain;
import com.layani.pulsa.entity.OrderPayload;
import org.slerp.core.CoreException;
import org.slerp.core.validation.KeyValidation;
import org.slerp.core.validation.NumberValidation;
import org.slerp.core.business.DefaultBusinessTransaction;

import java.util.Optional;

@Service
@Transactional
@KeyValidation({"payload", "orderId"})
@NumberValidation("orderId")
public class AddOrderPayload extends DefaultBusinessTransaction {

	@Autowired
	private OrderPayloadRepository orderPayloadRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Override
	public void prepare(Domain orderPayloadDomain) {

	}

	@Override
	public Domain handle(Domain orderPayloadDomain) {
		super.handle(orderPayloadDomain);
        Long orderId = orderPayloadDomain.getLong("orderId");
        Optional<Order> order = orderRepository.findById(orderId);
        orderPayloadDomain.remove("orderId");
        if(order.isPresent()){
            OrderPayload orderPayload = orderPayloadDomain.convertTo(OrderPayload.class);
            orderPayload.setOrderId(order.get());
            orderPayload = orderPayloadRepository.save(orderPayload);
            return new Domain(orderPayload);
        }else{
            throw new CoreException(ErrorConstant.ORDER_NOT_FOUND);
        }
	}
}