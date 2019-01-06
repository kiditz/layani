package com.layani.pulsa.service.notification;

import org.slerp.core.business.DefaultBusinessFunction;
import com.layani.pulsa.entity.Notification;
import org.springframework.stereotype.Service;
import org.slerp.core.validation.KeyValidation;
import org.slerp.core.validation.NumberValidation;
import org.slerp.core.validation.NotBlankValidation;
import org.slerp.core.Domain;
import com.layani.pulsa.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@KeyValidation("id")
@NumberValidation({})
@NotBlankValidation({})
public class FindNotification extends DefaultBusinessFunction {

	@Autowired
	NotificationRepository notificationRepository;

	@Override
	public Domain handle(Domain notificationDomain) {
		Notification notification = notificationRepository
				.findNotification(notificationDomain.getLong("id"));
		return new Domain().put("notification", notification);
	}
}