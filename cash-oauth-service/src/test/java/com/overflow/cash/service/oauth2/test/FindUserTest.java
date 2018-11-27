package com.overflow.cash.service.oauth2.test;

import com.overflow.cash.service.oauth2.FindUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slerp.core.Domain;
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
public class FindUserTest
		extends
			AbstractTransactionalJUnit4SpringContextTests {

	static private Logger log = LoggerFactory
			.getLogger(FindUserTest.class);
	@Autowired
    FindUser findUserByPhoneNumber;

	@Before
	public void prepare() {
		executeSqlScript(
				"classpath:com/slerpio/teachme/service/oauth2/test/FindUserTest.sql",
				false);
	}

	@Test
	public void testFindWithPhoneNumber() {
		String query = "087788044374";
		Domain userDomain = new Domain();
		userDomain.put("query", query);
		Domain outputUser = findUserByPhoneNumber.handle(userDomain);
		log.info("Result Test {}", outputUser);
		Assertions.assertThat(outputUser.getDomain("user").get("phoneNumber")).isEqualTo(query);
	}

	@Test
	public void testFindWithUsername() {
		String query = "kiditz";
		Domain userDomain = new Domain();
		userDomain.put("query", query);
		Domain outputUser = findUserByPhoneNumber.handle(userDomain);
		log.info("Result Test {}", outputUser);
		Assertions.assertThat(outputUser.getDomain("user").get("username")).isEqualTo(query);
	}
}