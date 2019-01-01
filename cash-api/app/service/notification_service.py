from entity.models import NotificationToken, Notification
from slerp.logger import logging
from slerp.validator import Key
from slerp.sender import send_message
log = logging.getLogger(__name__)


class NotificationService(object):
	def __init__(self):
		super(NotificationService, self).__init__()
	
	@Key(['client_id', 'user_id', 'token'])
	def add_notification_token(self, domain):
		notification_token = NotificationToken.query.filter(NotificationToken.user_id == domain['user_id']).first()
		if notification_token is None:
			notification_token = NotificationToken(domain)
			notification_token.save()
		else:
			notification_token.update(domain)
		return {'payload': notification_token.to_dict()}
	
	@Key(['user_id', 'title', 'body', 'data'])
	def add_notification(self, domain):
		notification = Notification(domain)
		notification.save()
		notification_dict = {
			'id': notification.id
		}
		send_message('notification', notification_dict)
		return {'payload': notification_dict['id']}