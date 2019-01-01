package com.layani.pulsa.service.notification;

import com.layani.pulsa.entity.Notification;
import com.layani.pulsa.repository.NotificationRepository;
import org.slerp.core.CoreException;
import org.slerp.core.Domain;
import org.slerp.core.business.DefaultBusinessFunction;
import org.slerp.core.validation.KeyValidation;
import org.slerp.core.validation.NotBlankValidation;
import org.slerp.core.validation.NumberValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@KeyValidation("id")
@NumberValidation({"id"})
@NotBlankValidation({})
public class FindNotification extends DefaultBusinessFunction {

	@Autowired
	private NotificationRepository notificationRepository;

	@Override
	public Domain handle(Domain notificationDomain) {
		Long id = notificationDomain.getLong("id");
		Optional<Notification> optional = notificationRepository.findById(id);
		if(optional.isPresent()){
			return new Domain().put("notification", optional.get());
		}else{
			throw new CoreException("notification.not.found");
		}
	}
}