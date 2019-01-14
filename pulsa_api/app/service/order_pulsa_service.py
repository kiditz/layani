import time

from entity.models import OrderPulsa, ProductLayani
from slerp.logger import logging
from slerp.sender import send_message
from slerp.validator import Key, ValidationException

log = logging.getLogger(__name__)


class OrderPulsaService(object):
	def __init__(self):
		super(OrderPulsaService, self).__init__()

	@Key(['msisdn', 'outlet_id', 'sales_type', 'code'])
	def add_order_pulsa(self, domain):
		product = ProductLayani.query.filter(ProductLayani.code == domain['code']).first()
		if product is None:
			raise ValidationException('product.not.found')
		domain['product_id'] = product.id
		if msisdn != '-':
			order_pulsa = OrderPulsa.query.filter_by(msisdn=domain['msisdn']).filter_by(status='I').first()		
			if order_pulsa is not None:
				raise ValidationException('order.still.in.progress')
		order_pulsa = OrderPulsa(domain)
		order_pulsa.save()
		millis = int(time.mktime(order_pulsa.order_at.timetuple()) * 1000)
		order_pulsa_dict = {
			'order_pulsa_id': order_pulsa.id,
			'product_id': order_pulsa.product_id,
			'outlet_id': order_pulsa.outlet_id,
			'customer_id': order_pulsa.customer_id,
			'sales_type': order_pulsa.sales_type,
			'msisdn': order_pulsa.msisdn,
			'created_at': millis,
			'user_id': order_pulsa.user_id
		}
		send_message('order_pulsa', order_pulsa_dict)
		return {'payload': order_pulsa.to_dict()}