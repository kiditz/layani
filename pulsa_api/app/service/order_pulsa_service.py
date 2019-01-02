from slerp.validator import Key, Number, Blank
from slerp.logger import logging
from slerp.app import db
from slerp.sender import send_message
from entity.models import OrderPulsa


log = logging.getLogger(__name__)


class OrderPulsaService(object):
	def __init__(self):
		super(OrderPulsaService, self).__init__()

	@Key(['msisdn', 'outlet_id', 'sales_type', 'product_id'])
	def add_order_pulsa(self, domain):
		order_pulsa = OrderPulsa(domain)
		order_pulsa.save()
		order_pulsa_dict = {
			'order_pulsa_id': order_pulsa.id
		}
		return {'payload': order_pulsa.to_dict()}