package com.overflow.cash.service.oauth2.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slerp.core.Domain;
import org.slerp.core.business.BusinessFunction;
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
import org.assertj.core.api.Assertions;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
@TestExecutionListeners(listeners = {DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class}, inheritListeners = false)
@Rollback
public class FindClientByClientIdTest
		extends
			AbstractTransactionalJUnit4SpringContextTests {

	static private Logger log = LoggerFactory
			.getLogger(FindClientByClientIdTest.class);
	@Autowired
	private BusinessFunction findClientByClientId;

	@Before
	public void prepare() {
		executeSqlScript(
				"classpath:com/overflow/cash/service/oauth2/test/FindClientByClientIdTest.sql",
				false);
	}

	@Test
	public void testSuccess() {
		String clientId = "kiditz";
		Domain clientDomain = new Domain();
		clientDomain.put("clientId", clientId);
		Domain outputClient = findClientByClientId.handle(clientDomain);
		log.info("Result Test {}", outputClient.getDomain("client"));
		Assertions.assertThat(outputClient.getDomain("client").get("clientId")).isEqualTo(clientId);
	}
}