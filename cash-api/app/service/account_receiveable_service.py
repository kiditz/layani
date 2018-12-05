from decimal import Decimal
from entity.models import AccountReceiveable, Customer, Order, Cashbox, CashboxHistory
from slerp.logger import logging
from slerp.validator import Key, Number, ValidationException
from sqlalchemy import and_, func, between
from utils.api_constant import PaymentMethod, ErrorCode
from datetime import datetime
log = logging.getLogger(__name__)


class AccountReceiveableService(object):
	def __init__(self):
		super(AccountReceiveableService, self).__init__()
	
	@Key(['name'])
	@Number(['merchant_id', 'page', 'size'])
	def get_account_receiveable_list(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		entities = (
			func.sum(AccountReceiveable.total_credit).label("total_credit"),
			func.count(Order.id).label("total_order"),
			Customer.name,
			Customer.id.label("customer_id")
		)
		
		account_receiveable_q = AccountReceiveable.query.with_entities(*entities)\
			.join(Order, and_(Order.id == AccountReceiveable.order_id, Order.status == 'I')) \
			.join(Customer, Customer.id == Order.customer_id) \
			.filter(AccountReceiveable.merchant_id == domain['merchant_id'])\
			.filter(and_(Customer.name.ilike('%' + domain['name'] + '%'), Order.payment_method == PaymentMethod.CREDIT)) \
			.order_by(Customer.name)\
			.group_by(Customer.id)\
			.paginate(page, size, error_out=False)
		account_receiveable_list = list(map(lambda x: x._asdict(), account_receiveable_q.items))
		return {'payload': account_receiveable_list, 'total': account_receiveable_q.total, 'total_pages': account_receiveable_q.pages}
		
	@Number(['customer_id', 'page', 'size'])
	def get_account_receiveable_detail(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		entities = (
			AccountReceiveable.total_credit,
			AccountReceiveable.payment_amount,
			Order.created_at.label("order_at"),
			AccountReceiveable.receiveable_date,
			Order.order_code,
			Order.id.label('order_id'),
			Order.cashback,
			Order.total_amount,
			Order.total_payment		
		)
		
		account_receiveable_q = AccountReceiveable.query.with_entities(*entities) \
			.join(Order, Order.id == AccountReceiveable.order_id)\
			.join(Customer, Customer.id == Order.customer_id)\
			.filter(and_(Customer.id == domain['customer_id'], Order.payment_method == PaymentMethod.CREDIT, Order.status == 'I')) \
			.order_by(AccountReceiveable.receiveable_date) \
			.paginate(page, size, error_out=False)
		account_receiveable_list = list(map(lambda x: x._asdict(), account_receiveable_q.items))
		return {'payload': account_receiveable_list, 'total': account_receiveable_q.total, 'total_pages': account_receiveable_q.pages}
	
	@Number(['merchant_id'])
	def get_account_receiveable_age(self, domain):
		in_1_30 = AccountReceiveable.query.with_entities(func.coalesce(func.sum(AccountReceiveable.total_credit), 0).label('total_credit'))\
		.join(Order, Order.id == AccountReceiveable.order_id)\
		.filter(and_(Order.status == 'I', Order.payment_method == PaymentMethod.CREDIT))\
		.filter(between(func.date_part('days', datetime.now() - AccountReceiveable.receiveable_date), -30, -0))\
		.first()._asdict()
		in_30_60 = AccountReceiveable.query.with_entities(func.coalesce(func.sum(AccountReceiveable.total_credit), 0).label('total_credit'))\
		.join(Order, Order.id == AccountReceiveable.order_id)\
		.filter(and_(Order.status == 'I', Order.payment_method == PaymentMethod.CREDIT))\
		.filter(between(func.date_part('days', datetime.now() - AccountReceiveable.receiveable_date), -60, -30))\
		.first()._asdict()
		in_60_90 = AccountReceiveable.query.with_entities(func.coalesce(func.sum(AccountReceiveable.total_credit), 0).label('total_credit'))\
		.join(Order, Order.id == AccountReceiveable.order_id)\
		.filter(and_(Order.status == 'I', Order.payment_method == PaymentMethod.CREDIT))\
		.filter(between(func.date_part('days', datetime.now() - AccountReceiveable.receiveable_date), -90, -60))\
		.first()._asdict()

		in_gt_90 = AccountReceiveable.query.with_entities(func.coalesce(func.sum(AccountReceiveable.total_credit), 0).label('total_credit'))\
		.join(Order, Order.id == AccountReceiveable.order_id)\
		.filter(and_(Order.status == 'I', Order.payment_method == PaymentMethod.CREDIT))\
		.filter(func.date_part('days', datetime.now() - AccountReceiveable.receiveable_date) < -90)\
		.first()._asdict()
		return {'payload': {'1-30 Hari': in_1_30['total_credit'], '30-60 Hari': in_30_60['total_credit'], '60-90 Hari': in_60_90['total_credit'], 'Lebih Dari 90 Hari': in_gt_90['total_credit']}}

	@Number(['merchant_id'])
	def get_account_receiveable_out_of_age(self, domain):
		in_0_30 = AccountReceiveable.query.with_entities(func.coalesce(func.sum(AccountReceiveable.total_credit), 0).label('total_credit'))\
		.join(Order, Order.id == AccountReceiveable.order_id)\
		.filter(and_(Order.status == 'I', Order.payment_method == PaymentMethod.CREDIT))\
		.filter(between(func.date_part('days', datetime.now() - AccountReceiveable.receiveable_date), 1, 30))\
		.first()._asdict()
		in_30_60 = AccountReceiveable.query.with_entities(func.coalesce(func.sum(AccountReceiveable.total_credit), 0).label('total_credit'))\
		.join(Order, Order.id == AccountReceiveable.order_id)\
		.filter(and_(Order.status == 'I', Order.payment_method == PaymentMethod.CREDIT))\
		.filter(between(func.date_part('days', datetime.now() - AccountReceiveable.receiveable_date), 30,  60))\
		.first()._asdict()
		in_60_90 = AccountReceiveable.query.with_entities(func.coalesce(func.sum(AccountReceiveable.total_credit), 0).label('total_credit'))\
		.join(Order, Order.id == AccountReceiveable.order_id)\
		.filter(and_(Order.status == 'I', Order.payment_method == PaymentMethod.CREDIT))\
		.filter(between(func.date_part('days', datetime.now() - AccountReceiveable.receiveable_date), 60, 90))\
		.first()._asdict()

		in_gt_90 = AccountReceiveable.query.with_entities(func.coalesce(func.sum(AccountReceiveable.total_credit), 0).label('total_credit'))\
		.join(Order, Order.id == AccountReceiveable.order_id)\
		.filter(and_(Order.status == 'I', Order.payment_method == PaymentMethod.CREDIT))\
		.filter(func.date_part('days', datetime.now() - AccountReceiveable.receiveable_date) > 90)\
		.first()._asdict()
		return {'payload': {'0-30 Hari': in_0_30['total_credit'], '30-60 Hari': in_30_60['total_credit'], '60-90 Hari': in_60_90['total_credit'], 'Lebih Dari 90 Hari': in_gt_90['total_credit']}}
	
	@Key(['order_id', 'cash_box_id', 'payment_amount'])
	def edit_account_receiveable(self, domain):		
		order = Order.query.get(domain['order_id'])
		payment_amount = Decimal(domain['payment_amount'])

		if order is None:
			raise ValidationException(ErrorCode.ORDER_NOT_FOUND)
		account_receiveable = AccountReceiveable.query.get(domain['order_id'])								
		subtract_amount = payment_amount - account_receiveable.total_credit
		if subtract_amount < 0:											
			account_receiveable.total_credit = subtract_amount * Decimal(-1.0)
			order.total_payment = order.total_payment + payment_amount
			account_receiveable.payment_amount = order.total_payment
		else:	
			account_receiveable.payment_amount = account_receiveable.total_credit			
			account_receiveable.total_credit = 0
			order.total_payment = order.total_payment + payment_amount
			order.cashback = order.total_payment - order.total_amount			
			order.status = 'S'		


		order.save()
		account_receiveable.save()
		cashbox = Cashbox.query.get(domain['cash_box_id'])
		cashbox.total_amount = cashbox.total_amount + payment_amount
		cashbox_history = CashboxHistory()
		cashbox_history.cash_box_id = cashbox.id
		cashbox_history.payment_amount = Decimal(account_receiveable.payment_amount)
		cashbox_history.payment_method = PaymentMethod.DEBIT
		cashbox_history.remark = 'payment.paid #' + order.order_code
		cashbox_history.save()
		
		account_receiveable_dict = account_receiveable.to_dict()		
		account_receiveable_dict['status'] = order.status
		account_receiveable_dict['total_payment'] = order.total_payment
		return {'payload': account_receiveable_dict}