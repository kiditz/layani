package com.layani.pulsa.integration.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.web.bind.annotation.*;
import org.slerp.core.business.BusinessFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.slerp.core.Domain;

@RestController
@RequestMapping("/notification")
public class NotificationController {

	@Autowired
	private KafkaMessageListenerContainer<String, String> notificationContainer;

	private Logger log = LoggerFactory.getLogger(getClass());


	@GetMapping("/stop")
	@ResponseBody
	public Domain stopConsumer(){
		notificationContainer.stop(() -> {
			log.info("Stoping Kafka Consumer");
		});
		return new Domain().put("stop", notificationContainer.isContainerPaused());
	}
}