package com.layani.pulsa.service.notification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.layani.pulsa.repository.NotificationRepository;
import org.slerp.core.Domain;
import com.layani.pulsa.entity.Notification;
import org.slerp.core.CoreException;
import org.slerp.core.validation.KeyValidation;
import org.slerp.core.validation.NumberValidation;
import org.slerp.core.business.DefaultBusinessTransaction;

@Service
@Transactional
@KeyValidation({"userId", "title", "body"})
@NumberValidation("userId")
public class AddNotification extends DefaultBusinessTransaction {

	@Autowired
	NotificationRepository notificationRepository;

	@Override
	public void prepare(Domain notificationDomain) throws Exception {
	}

	@Override
	public Domain handle(Domain notificationDomain) {
		super.handle(notificationDomain);
		try {
			Notification notification = notificationDomain
					.convertTo(Notification.class);
			notification = notificationRepository.save(notification);
			return new Domain(notification);
		} catch (Exception e) {
			throw new CoreException(e);
		}
	}
}