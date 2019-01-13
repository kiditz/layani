package com.layani.pulsa.service.order.test;

import org.junit.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slerp.core.Domain;
import org.slerp.core.business.BusinessTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.junit.Before;
import org.junit.Test;
import java.math.BigDecimal;
import org.assertj.core.api.Assertions;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
@TestExecutionListeners(listeners = {DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class}, inheritListeners = false)
@Rollback
public class EditOrderTest
		extends
			AbstractTransactionalJUnit4SpringContextTests {

	static private Logger log = LoggerFactory.getLogger(EditOrderTest.class);
	@Autowired
	private BusinessTransaction editOrder;

	@Before
	public void prepare() {
	}

	@Test

	public void testSuccess() {
		Long id = 40L;
		String msisdn = "0895414733018";
		BigDecimal purchasePrice = BigDecimal.valueOf(1215);
		String status = "S";
		BigDecimal sellPrice = BigDecimal.valueOf(1365);
		String remark = "trx.success";
		String salesType = "TRADITIONAL";
		Long outletId = 1L;
		Domain orderDomain = new Domain();
		orderDomain.put("id", id);
		orderDomain.put("outletId", outletId);
		orderDomain.put("msisdn", msisdn);
		orderDomain.put("purchasePrice", purchasePrice);
		orderDomain.put("status", status);
		orderDomain.put("sellPrice", sellPrice);
		orderDomain.put("remark", remark);
		orderDomain.put("salesType", salesType);
		Domain outputOrder = editOrder.handle(orderDomain);
		log.info("Result Test {}", outputOrder);
		Assertions.assertThat(orderDomain.get("id")).isEqualTo(id);
		Assertions.assertThat(orderDomain.get("msisdn")).isEqualTo(msisdn);
		Assertions.assertThat(orderDomain.get("purchasePrice")).isEqualTo(purchasePrice);
		Assertions.assertThat(orderDomain.get("status")).isEqualTo(status);
		Assertions.assertThat(orderDomain.get("sellPrice"))
				.isEqualTo(sellPrice);
		Assertions.assertThat(orderDomain.get("remark")).isEqualTo(remark);
		Assertions.assertThat(orderDomain.get("salesType")).isEqualTo(salesType);
	}
	@After
	public void after(){

	}
}