package com.layani.pulsa.service.notification;

import com.layani.pulsa.entity.NotificationToken;
import com.layani.pulsa.repository.NotificationTokenRepository;
import org.slerp.core.Domain;
import org.slerp.core.business.DefaultBusinessFunction;
import org.slerp.core.validation.KeyValidation;
import org.slerp.core.validation.NotBlankValidation;
import org.slerp.core.validation.NumberValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@KeyValidation({"userId"})
@NumberValidation({"userId"})
@NotBlankValidation({})
public class FindNotificationToken extends DefaultBusinessFunction {

	@Autowired
	NotificationTokenRepository notificationTokenRepository;

	@Override
	public Domain handle(Domain notificationTokenDomain) {
		NotificationToken notificationTokenPage = notificationTokenRepository.findNotificationToken(notificationTokenDomain.getLong("userId"));
		return new Domain().put("notificationToken", notificationTokenPage);
	}
}