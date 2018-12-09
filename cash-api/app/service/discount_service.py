from entity.models import Discount
from slerp.logger import logging
from slerp.validator import Key, ValidationException

from utils.api_constant import ErrorCode

log = logging.getLogger(__name__)


class DiscountService(object):
	def __init__(self):
		super(DiscountService, self).__init__()

	@Key(['product_id', 'discount', 'discount_when'])
	def add_discount(self, domain):
		discount = Discount(domain)
		discount.save()
		return {'payload': discount.to_dict()}
	
	@Key(['product_id', 'discount_when'])
	def find_discount_by_product_id_and_discount(self, domain):
		discount_when = int(domain['discount_when'])
		discount = Discount.query\
			.filter(Discount.product_id == domain['product_id']) \
			.filter(Discount.discount_when >= discount_when) \
			.order_by(Discount.discount.asc()).first()
		if discount is None:
			raise ValidationException(ErrorCode.DISCOUNT_NOT_FOUND)
		return {'payload': discount.to_dict()}