from decimal import Decimal

from entity.models import Discount, Product
from slerp.logger import logging
from slerp.validator import Key, Blank, ValidationException
from sqlalchemy import cast, between, or_
from sqlalchemy.dialects.mssql import DATE
from datetime import datetime
from utils.api_constant import ErrorCode, DiscountMethod

log = logging.getLogger(__name__)


class DiscountService(object):
	def __init__(self):
		super(DiscountService, self).__init__()

	@Key(['product_id', 'discount', 'quantity'])
	def add_discount(self, domain):
		discount = Discount(domain)
		discount.save()
		return {'payload': discount.to_dict()}
	
	@Key(['outlet_id', 'quantity', 'product_id'])
	@Blank(['date'])
	def find_discount_by_quantity(self, domain):
		qty = int(domain['quantity'])
		outlet_id = domain['outlet_id']
		date = domain['date']
		entities = (
			Discount.day_of_week,
			Discount.method,
			Discount.quantity,
			Discount.bill_amount,
			Discount.start_at,
			Discount.name,
			Discount.amount,
			Product.name.label('free_product_name')
		)
		discount = Discount.query.with_entities(*entities)\
			.outerjoin(Product, Discount.free_product_id == Product.id)\
			.filter(Discount.outlet_id == outlet_id)\
			.filter(Discount.quantity <= qty)\
			.filter(or_(Discount.method == DiscountMethod.DISCOUNT_AMOUNT_PRODUCT, Discount.method == DiscountMethod.BY_N_GET_ONE))\
			.filter(between(date, cast(Discount.start_at, DATE), cast(Discount.end_at, DATE)))\
			.order_by(Discount.quantity.desc()).first()
	
		# Validate if discount not found
		if discount is None:
			raise ValidationException(ErrorCode.DISCOUNT_NOT_FOUND)
		
		# Validate discount for date
		day_of_weeks = [int(x) for x in discount.day_of_week.split(',')]
		today = datetime.strptime(date, '%Y-%m-%d').weekday()
		if today not in day_of_weeks:
			raise ValidationException(ErrorCode.DISCOUNT_NOT_FOR_TODAY)
		
		return {'payload': discount._asdict()}
	
	@Key(['outlet_id', 'bill_amount'])
	@Blank(['date'])
	def find_discount_by_bill_amount(self, domain):
		bill_amount = Decimal(domain['bill_amount'])
		outlet_id = domain['outlet_id']
		date = domain['date']
		
		discount = Discount.query\
			.filter(Discount.outlet_id == outlet_id) \
			.filter(between(date, cast(Discount.start_at, DATE), cast(Discount.end_at, DATE))) \
			.filter(Discount.bill_amount <= bill_amount) \
			.filter(Discount.method == DiscountMethod.DISCOUNT_AMOUNT_TRANSACTION) \
			.order_by(Discount.bill_amount.desc())\
			.first()
		# Validate if discount not found
		if discount is None:
			raise ValidationException(ErrorCode.DISCOUNT_NOT_FOUND)
		# Validate discount for date
		day_of_weeks = [int(x) for x in discount.day_of_week.split(',')]
		today = datetime.strptime(date, '%Y-%m-%d').weekday()
		if today not in day_of_weeks:
			raise ValidationException(ErrorCode.DISCOUNT_NOT_FOR_TODAY)
		return {'payload': discount.to_dict()}