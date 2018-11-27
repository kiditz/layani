package com.overflow.cash.service.oauth2.test;

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
import java.util.Date;
import java.util.UUID;

import org.assertj.core.api.Assertions;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
@TestExecutionListeners(listeners = {DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class, DependencyInjectionTestExecutionListener.class}, inheritListeners = false)
@Rollback
public class AddDeviceTest
		extends
			AbstractTransactionalJUnit4SpringContextTests {

	static private Logger log = LoggerFactory.getLogger(AddDeviceTest.class);
	@Autowired
	private BusinessTransaction addDevice;

	@Before
	public void prepare() {
		executeSqlScript(
				"classpath:com/slerpio/teachme/service/oauth2/test/AddDeviceTest.sql",
				false);
	}

	@Test
	public void testSuccess() {
		String manufactured = "Vivo";
		String product = "Vivi Y21";

        String deviceId = UUID.randomUUID().toString();
		Date updateAt = new Date();
		Date createdAt = new Date();
		String clientId = "free-learn";
		String model = "Y21";
		Domain deviceDomain = new Domain();
		deviceDomain.put("deviceId", deviceId);
		deviceDomain.put("manufactured", manufactured);
		deviceDomain.put("product", product);
		deviceDomain.put("updateAt", updateAt);
		deviceDomain.put("createdAt", createdAt);
		deviceDomain.put("clientId", clientId);
		deviceDomain.put("model", model);
		Domain outputDevice = addDevice.handle(new Domain(deviceDomain));
		log.info("Result Test {}", outputDevice);
		Assertions.assertThat(outputDevice.get("manufactured")).isEqualTo(manufactured);
		Assertions.assertThat(outputDevice.getString("product")).isEqualTo(product);
		Assertions.assertThat(outputDevice.get("model")).isEqualTo(model);
        outputDevice = addDevice.handle(new Domain(deviceDomain));
        log.info("Result Test {}", outputDevice);
	}
}