package com.layani.pulsa.service.order;

import com.layani.pulsa.entity.Order;
import com.layani.pulsa.entity.OrderApi;
import com.layani.pulsa.repository.OrderApiRepository;
import com.layani.pulsa.repository.OrderRepository;
import com.layani.pulsa.service.constant.ErrorConstant;
import com.layani.pulsa.service.constant.ServiceConstant;
import org.slerp.core.CoreException;
import org.slerp.core.Domain;
import org.slerp.core.business.DefaultBusinessTransaction;
import org.slerp.core.validation.KeyValidation;
import org.slerp.core.validation.NumberValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional
@KeyValidation({"id", "msisdn", "salesType", "sellPrice", "purchasePrice", "status", "remark"})
@NumberValidation({"id", "outletId"})
public class EditOrder extends DefaultBusinessTransaction {

	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderApiRepository orderApiRepository;
	@Autowired
	private JdbcTemplate template;
	@Override
	public void prepare(Domain orderDomain) throws Exception {
		Optional<Order> optional = orderRepository.findById(orderDomain.getLong("id"));
		// Validate order is present
		if (!optional.isPresent()) {
			throw new CoreException(ErrorConstant.ORDER_NOT_FOUND);
		}
		Order order = optional.get();
		// Validate order still in progress
		if(!order.getStatus().equalsIgnoreCase(ServiceConstant.IN_PROGRESS)){
			throw new CoreException(ErrorConstant.ORDER_IS_NOT_IN_PROGRESS);
		}
		orderDomain.put("updateAt", new Date());
		orderDomain.put("reqid", ServiceConstant.getReqid(orderDomain.getLong("id")));
	}

	@Override
	public Domain handle(Domain orderDomain) {
		super.handle(orderDomain);
		try {
			Order order = orderDomain.convertTo(Order.class);
			order = orderRepository.save(order);
			updatePartnerBalance(order);
			if(orderDomain.containsKey("request") || orderDomain.containsKey("response")){
				addOrderApi(order, orderDomain);
			}
			return new Domain(order);
		} catch (Exception e) {
			throw new CoreException(e);
		}
	}

	private void addOrderApi(Order order, Domain orderDomain){
		OrderApi orderApi = new OrderApi();
		orderApi.setOrderId(order);
		orderApi.setCreatedAt(new Date());
		orderApi.setRequest(orderDomain.getString("request"));
		orderApi.setResponse(orderDomain.getString("response"));
		orderApiRepository.save(orderApi);
	}

	private void updatePartnerBalance(Order order){
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("$1", order.getId());
		SimpleJdbcCall call = new SimpleJdbcCall(template).withFunctionName("f_update_partner_balance");
		call.executeFunction(Void.class, map);
	}
}