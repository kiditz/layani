package com.slerpio.cash.test;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Before;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
public class MockTest {
	MockRestServiceServer server;

	@Before
	public void setUp() {
		RestTemplate template = new RestTemplate();
		
		server = MockRestServiceServer.createServer(template);		
	}
	public void test() {
		server.expect(requestTo("http://google.com")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess("resultSuccess", MediaType.TEXT_PLAIN));
		
	}
}
