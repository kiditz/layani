import time
from datetime import datetime
from entity.models import OrderPulsa, ProductLayani
from slerp.logger import logging
from slerp.sender import send_message
from slerp.validator import Key, ValidationException
from slerp.app import app
from utils.api_constant import ErrorCode
log = logging.getLogger(__name__)


class OrderPulsaService(object):
	def __init__(self):
		super(OrderPulsaService, self).__init__()
	
	@Key(['msisdn', 'outlet_id', 'code'])
	def add_order_pulsa(self, domain):
		product = ProductLayani.query.filter(ProductLayani.code == domain['code']).first()
		if product is None:
			raise ValidationException(ErrorCode.PRODUCT_NOT_FOUND)
		domain['product_id'] = product.id
		if domain['msisdn'] != '-':
			order_pulsa = OrderPulsa.query.filter_by(msisdn=domain['msisdn']).filter_by(status='I').first()
			if order_pulsa is not None:
				raise ValidationException(ErrorCode.ORDER_STILL_IN_PROGRESS)
		if product.code.startswith('LCEK'):
			order_pulsa = OrderPulsa(domain)
			order_pulsa.sales_type = app.config['post_paid_check']
			order_pulsa.save()
		elif product.code.startswith('LBYR'):
			if 'id' not in domain:
				raise ValidationException('must.check.payment.anyway')
			order_pulsa = OrderPulsa.query.filter_by(id=domain['id']).first()
			order_pulsa.order_at = datetime.now()
			order_pulsa.sales_type = app.config['post_paid_pay']
			order_pulsa.update(domain)
		else:
			order_pulsa = OrderPulsa(domain)
			order_pulsa.sales_type = app.config['order_pulsa']
			order_pulsa.save()
		millis = int(time.mktime(order_pulsa.order_at.timetuple()) * 1000)
		order_pulsa_dict = {
			'order_pulsa_id': order_pulsa.id,
			'product_id': order_pulsa.product_id,
			'outlet_id': order_pulsa.outlet_id,
			'customer_id': order_pulsa.customer_id,
			'sales_type': order_pulsa.sales_type,
			'msisdn': order_pulsa.msisdn,
			'order_at': millis,
			'user_id': order_pulsa.user_id,
			'created_at': order_pulsa.created_at
		}
		if product.code.startswith('LCEK'):
			send_message(app.config['post_paid_check'], order_pulsa_dict)
		elif product.code.startswith('LBYR'):
			send_message(app.config['post_paid_pay'], order_pulsa_dict)
		else:
			send_message(app.config['order_pulsa'], order_pulsa_dict)
		return {'payload': order_pulsa.to_dict()}
