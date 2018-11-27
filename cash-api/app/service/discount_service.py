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
		qty = int(domain['discount_when'])
		discounts = Discount.query.filter(Discount.product_id == domain['product_id'])\
			.order_by(Discount.discount.asc()).all()
		for discount in discounts:
			if qty >= discount.discount_when:
				return {'payload': discount.to_dict()}
		raise ValidationException(ErrorCode.DISCOUNT_NOT_FOUND)