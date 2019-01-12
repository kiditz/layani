package com.layani.pulsa.integration.controller;

import org.slerp.core.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
public class NotificationController {

	@Autowired
	private KafkaMessageListenerContainer<String, String> notificationContainer;

	private Logger log = LoggerFactory.getLogger(getClass());


	@GetMapping("/stop")
	@ResponseBody
	public Domain notificationConsumer(@RequestParam("query") String query){
		if(query.equalsIgnoreCase("start")){
            if(!notificationContainer.isRunning()){
                notificationContainer.start();
            }
        }else{
            if(notificationContainer.isRunning()){
                notificationContainer.stop(()->{
                    log.info("Notification Stoped");
                });
            }
        }
		return new Domain().put("status", notificationContainer.isContainerPaused());
	}
}