from entity.models import NotificationToken
from slerp.logger import logging
from slerp.validator import Key

log = logging.getLogger(__name__)


class NotificationTokenService(object):
	def __init__(self):
		super(NotificationTokenService, self).__init__()
	
	@Key(['client_id', 'user_id', 'token'])
	def add_notification_token(self, domain):
		notification_token = NotificationToken.query.filter(NotificationToken.user_id == domain['user_id']).first()
		if notification_token is None:
			notification_token = NotificationToken(domain)
			notification_token.save()
		else:
			notification_token.update(domain)
		return {'payload': notification_token.to_dict()}
