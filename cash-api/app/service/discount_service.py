from entity.models import Discount
from slerp.logger import logging
from slerp.validator import Key, Blank,ValidationException
from decimal import Decimal
from utils.api_constant import ErrorCode
from sqlalchemy import cast, between
from sqlalchemy.dialects.mssql import DATE
from slerp.app import app
log = logging.getLogger(__name__)


class DiscountService(object):
	def __init__(self):
		super(DiscountService, self).__init__()

	@Key(['product_id', 'discount', 'quantity'])
	def add_discount(self, domain):
		discount = Discount(domain)
		discount.save()
		return {'payload': discount.to_dict()}
	
	@Key(['outlet_id', 'quantity'])
	@Blank(['date'])
	def find_discount_by_quantity(self, domain):
		qty = int(domain['quantity'])
		outlet_id = domain['outlet_id']
		date = domain['date']
		discount = Discount.query\
			.filter(Discount.outlet_id == outlet_id) \
			.filter(between(cast(Discount.start_at, DATE), cast(Discount.end_at, DATE), date)) \
			.filter(Discount.quantity <= qty).first()
		if discount is None:
			raise ValidationException(ErrorCode.DISCOUNT_NOT_FOUND)
		return {'payload': discount.to_dict()}
	
	@Key(['outlet_id', 'bill_amount'])
	@Blank(['date'])
	def find_discount_by_bill_amount(self, domain):
		bill_amount = Decimal(domain['bill_amount'])
		outlet_id = domain['outlet_id']
		date = domain['date']
		log.info("Date %s", date)
		discount = Discount.query \
			.filter(Discount.outlet_id == outlet_id) \
			.filter(between(date, cast(Discount.start_at, DATE), cast(Discount.end_at, DATE))) \
			.filter(Discount.bill_amount <= bill_amount)\
			.order_by(Discount.bill_amount.desc())\
			.first()
		if discount is None:
			raise ValidationException(ErrorCode.DISCOUNT_NOT_FOUND)
		return {'payload': discount.to_dict()}