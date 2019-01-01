package com.layani.pulsa.integration.controller;

import org.springframework.web.bind.annotation.*;
import org.slerp.core.business.BusinessFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.slerp.core.Domain;

@RestController
@RequestMapping("/notification")
public class NotificationController {

	@Autowired
	BusinessFunction findNotification;

	@GetMapping("/find")
	@ResponseBody
	public Domain findNotification(@RequestParam("id") Long id) {
		Domain notificationDomain = new Domain();
		notificationDomain.put("id", id);
		return findNotification.handle(notificationDomain);
	}
}