package com.layani.pulsa.integration.controller;

import org.slerp.core.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckerController {

	private Logger log = LoggerFactory.getLogger(getClass());


	@GetMapping("/health")
	@ResponseBody
	public Domain health(){
		return new Domain().put("start", "success");
	}
}