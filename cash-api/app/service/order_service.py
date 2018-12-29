from entity.models import OrderItem, StockHistory, Stock, AccountReceiveable, Customer, Product, \
	ProductSellPrice, ProductPurchasePrice, \
	CashboxSummary, User, CashboxHistory
from slerp.logger import logging
from slerp.validator import Number, Key, ValidationException
from sqlalchemy import between

from utils import str2bool
from utils.api_constant import StockRef, ErrorCode, CashboxStatus, PaymentMethod
from .chart_query import *

log = logging.getLogger(__name__)


class OrderService(object):
	def __init__(self):
		super(OrderService, self).__init__()
	
	@Key(['outlet_id', 'customer_id'])
	@Number(['user_id'])
	def add_order(self, domain):
		outlet_id = domain['outlet_id']
		order_id = domain['order_id'] if 'order_id' in domain else -1
		order = Order.query.filter_by(id=order_id).first()
		if order is None:
			order = Order(domain)
			order.save()
			order.order_at = datetime.now()
			order.order_code = str(order.id).zfill(10)
		else:
			order.update(domain)
		if 'total_amount' in domain and 'total_payment' in domain:
			total_amount = domain['total_amount']
			total_payment = domain['total_payment']
			order.total_payment = total_payment
			order.total_amount = total_amount
			if total_payment < total_amount:
				raise ValidationException(ErrorCode.INVALID_TOTAL_AMOUNT)
			order.status = OrderStatus.SUCCESS
			order.cashback = order.total_payment - order.total_amount
			# Cashbox History
			
			datetime_now = datetime.now()
			date_now = datetime_now.date()
			cashbox_summary = CashboxSummary.query \
				.filter(CashboxSummary.outlet_id == outlet_id) \
				.filter(cast(CashboxSummary.start_at, DATE) == date_now) \
				.filter(CashboxSummary.status == CashboxStatus.OPEN).first()
			if cashbox_summary is None:
				cashbox_summary = CashboxSummary()
				cashbox_summary.transaction = 0
				cashbox_summary.start_at = datetime_now
				cashbox_summary.end_at = datetime_now
				cashbox_summary.user_id = domain['user_id']
				cashbox_summary.outlet_id = outlet_id
				cashbox_summary.status = 'O'
				cashbox_summary.save()
			pass
		
		order_dict = order.to_dict()
		if 'items' in domain:
			order_items = domain['items']
			for item in order_items:
				item['order_id'] = order.id
				if item['use_stock']:
					stock = Stock.query.filter_by(product_id=item['product_id']).first()
					if stock.quantity - item['qty'] < 0:
						raise ValidationException(ErrorCode.NOT_ENOUGH_STOCK)
					stock.quantity -= item['qty']
					stock.save()
					stock_history = StockHistory()
					stock_history.quantity = -item['qty']
					stock_history.ref_id = StockRef.TRANSACTION
					stock_history.remark = 'cut.stock #{}'.format(order.order_code)
					stock_history.stock_id = stock.id
					stock_history.save()
				pass
			db.session.bulk_insert_mappings(OrderItem, order_items)
			order_dict['order_items'] = domain['items']
		else:
			order_code = {'order_code': order.order_code}
			order_dict["order_items"] = self.get_order_items(order_code)['payload']
		if domain['customer_id'] is not None:
			customer = Customer.query.get(domain['customer_id'])
			if customer is not None:
				order_dict['customer_name'] = customer.name
		
		return {'payload': order_dict}
	
	@Number(['order_id'])
	def refund_order(self, domain):
		order_id = domain['order_id']
		order = Order.query.get(order_id)
		datetime_now = datetime.now()
		date_now = datetime_now.date()
		if order.status != OrderStatus.SUCCESS:
			raise ValidationException(ErrorCode.REFUND_FAILED)
		order.status = OrderStatus.VOID
		if 'description' in domain:
			order.description = domain['description']
		order.save()
		order_cpy = Order(order.to_dict())
		order_cpy.order_at = datetime_now
		order_cpy.id = None
		order_cpy.total_amount = order.total_amount * -1
		order_cpy.save()
		cashbox_summary = CashboxSummary.query \
			.filter(CashboxSummary.outlet_id == order.outlet_id) \
			.filter(cast(CashboxSummary.start_at, DATE) == date_now) \
			.filter(CashboxSummary.status == CashboxStatus.OPEN).first()
		if cashbox_summary is None:
			cashbox_summary = CashboxSummary()
			cashbox_summary.transaction = 0
			cashbox_summary.start_at = datetime_now
			cashbox_summary.end_at = datetime_now
			cashbox_summary.user_id = domain['user_id']
			cashbox_summary.outlet_id = order.outlet_id
			cashbox_summary.status = 'O'
			cashbox_summary.save()				
		return {'payload': order.to_dict()}
	
	@Number(['order_code'])
	def get_order_items(self, domain):
		order_code = domain['order_code']
		
		entities = (
			OrderItem.sub_total,
			OrderItem.qty,
			Product.name.label('product_name'),
			OrderItem.discount_amount,
			OrderItem.discount_name,
			Product.unit,
			Product.use_stock,
			Product.id.label('product_id'),
			Product.document_id,
			ProductSellPrice.sell_price,
			ProductSellPrice.id.label('sell_pricet_id'),
		
		)
		now = datetime.now()
		order_items = OrderItem.query.with_entities(*entities) \
			.join(Product, Product.id == OrderItem.product_id)\
			.join(ProductSellPrice, and_(Product.id == ProductSellPrice.product_id, ProductSellPrice.name == 'STANDARD')) \
			.join(ProductPurchasePrice, and_(ProductPurchasePrice.product_id == Product.id, between(now, ProductPurchasePrice.start_at, ProductPurchasePrice.end_at))) \
			.join(Order, and_(Order.id == OrderItem.order_id, Order.status == OrderStatus.SUCCESS)) \
			.filter(Order.order_code == order_code) \
			.order_by("product_name asc")
		order_item_list = list(map(lambda x: x._asdict(), order_items.all()))
		return {'payload': order_item_list}
	
	@Number(['outlet_id', 'page', 'size'])
	@Key(['query'])
	def get_order_list(self, domain):
		outlet_id = domain['outlet_id']
		status = domain['status'] if 'status' in domain else None
		exclude_status = str2bool(domain['exclude']) if 'exclude' in domain else True
		page = int(domain['page'])
		size = int(domain['size'])
		entities = (
			Order.id,
			Order.total_amount,
			Order.total_payment,
			Order.order_code,
			Order.status,
			Order.outlet_id,
			Order.order_at,
			Order.payment_method,
			Order.cashback,
			AccountReceiveable.receiveable_date,
			AccountReceiveable.total_credit,
			Customer.name.label("customer_name")
		)
		order_q = Order.query.with_entities(*entities).filter_by(outlet_id=outlet_id) \
			.outerjoin(Customer, Customer.id == Order.customer_id) \
			.outerjoin(AccountReceiveable, AccountReceiveable.order_id == Order.id) \
			.filter(Order.order_code.ilike('%' + domain['query'] + '%'))
		log.info('exclude_status : %s', exclude_status)
		if exclude_status:
			log.info('exclude_status run: %s', exclude_status)
			order_q = order_q.filter(Order.status != OrderStatus.CREATED)
			pass
		if status:
			order_q = order_q.filter(Order.status == domain['status'])
		order_q = order_q.order_by(Order.order_at.desc()).paginate(page, size, error_out=False)
		order_list = list(map(lambda x: x._asdict(), order_q.items))
		return {'payload': order_list, 'total': order_q.total, 'total_pages': order_q.pages}
	
	@Key(['id'])
	def delete_order_by_id(self, domain):
		order = Order.query.filter_by(id=domain['id']).first()
		if order is None:
			raise ValidationException(ErrorCode.ORDER_NOT_FOUND)
		if order.status != OrderStatus.CREATED:
			raise ValidationException(ErrorCode.ORDER_CANNOT_BE_DELETED)
		OrderItem.query.filter_by(order_id=domain['id']).delete()
		order.delete()
		return {'payload': {'success': 'Y'}}
	
	@Key(['outlet_id'])
	def count_saved_order_by_id(self, domain):
		count_order_saved = Order.query \
			.with_entities(func.count(Order.id).label('count_saved_order')) \
			.filter(and_(Order.outlet_id == domain['outlet_id'], Order.status == OrderStatus.CREATED)) \
			.scalar()
		return {'payload': {'count': count_order_saved}}
	
	@Key(['summary_id', 'date'])
	def get_order_amount_summary(self, domain):
		date_now = domain['date']
		cashbox_summary = CashboxSummary.query.filter(CashboxSummary.id == domain['summary_id']).first()
		outlet_id = cashbox_summary.outlet_id
		
		order_success_q = Order.query.with_entities(
			func.coalesce(func.sum(Order.total_amount), 0.0).label('order_summary')) \
			.filter(and_(between(Order.order_at, cashbox_summary.start_at.strftime('%Y-%m-%d %H:%M:%S'), date_now), Order.outlet_id == outlet_id)) \
			.filter(Order.status == OrderStatus.SUCCESS) \
			.first()
		
		order_in_progress_q = Order.query.with_entities(
			func.coalesce(func.sum(Order.total_amount), 0.0).label('order_summary')) \
			.filter(and_(between(Order.order_at, cashbox_summary.start_at.strftime('%Y-%m-%d %H:%M:%S'), date_now), Order.outlet_id == outlet_id)) \
			.filter(Order.status == OrderStatus.PENDING) \
			.first()
		
		order_void_q = Order.query.with_entities(
			func.coalesce(func.sum(Order.total_amount), 0.0).label('order_summary')) \
			.filter(and_(between(Order.order_at, cashbox_summary.start_at.strftime('%Y-%m-%d %H:%M:%S'), date_now), Order.outlet_id == outlet_id)) \
			.filter(Order.status == OrderStatus.VOID) \
			.first()
		
		order_created_q = Order.query.with_entities(func.count(Order.id).label('order_summary')) \
			.filter(and_(between(Order.order_at, cashbox_summary.start_at.strftime('%Y-%m-%d %H:%M:%S'), date_now), Order.outlet_id == outlet_id, Order.status == OrderStatus.CREATED, Order.total_amount > 0.0)) \
			.first()
		
		order_card_q = Order.query.with_entities(
			func.coalesce(func.sum(Order.total_amount), 0.0).label('order_summary')) \
			.filter(and_(between(Order.order_at, cashbox_summary.start_at.strftime('%Y-%m-%d %H:%M:%S'), date_now), Order.outlet_id == outlet_id, Order.payment_method == PaymentMethod.CARD, Order.total_amount > 0.0)) \
			.first()
		
		order_cash_q = Order.query.with_entities(
			func.coalesce(func.sum(Order.total_amount), 0.0).label('order_summary')) \
			.filter(and_(between(Order.order_at, cashbox_summary.start_at.strftime('%Y-%m-%d %H:%M:%S'), date_now), Order.outlet_id == outlet_id, Order.payment_method == PaymentMethod.CASH, Order.total_amount > 0.0)) \
			.first()
		
		cash_in = CashboxHistory.query.with_entities(func.coalesce(func.sum(CashboxHistory.amount), 0).label('amount')) \
			.filter(CashboxHistory.ref_id == 1) \
			.filter(CashboxHistory.cash_box_summary_id == domain['summary_id']) \
			.first().amount
		
		cash_out = CashboxHistory.query.with_entities(func.coalesce(func.sum(CashboxHistory.amount), 0).label('amount')) \
			.filter(CashboxHistory.ref_id == 2) \
			.filter(CashboxHistory.cash_box_summary_id == domain['summary_id']) \
			.first().amount
		
		order_dict = {
			'success': order_success_q.order_summary,
			'void': order_void_q.order_summary,
			'created': order_created_q.order_summary,
			'card': order_card_q.order_summary,
			'cash': order_cash_q.order_summary,
			'in_progress': order_in_progress_q.order_summary,
			'cash_in': cash_in,
			'cash_out': cash_out
		}
		return {'payload': order_dict}
	
	@Key(['id'])
	def find_order_by_id(self, domain):
		entities = (
			Order.id,
			Order.total_amount,
			Order.total_payment,
			Order.payment_method,
			Order.order_code,
			Order.order_at,
			Order.status,
			User.fullname,
			Outlet.name.label('outlet_name'),
			Outlet.address.label('outlet_address'),
			Outlet.phone_number.label('outlet_phone_number'),
			Order.cashback,
			Order.discount_amount,
			Order.discount_name
		)
		order = Order.query.with_entities(*entities) \
			.join(User, User.id == Order.user_id) \
			.join(Outlet, Outlet.id == Order.outlet_id) \
			.filter(Order.id == domain['id']) \
			.first()
		price_before_disc = OrderItem.query.with_entities(
			func.coalesce(func.sum(OrderItem.sub_total), 0).label('amount')).filter(
			OrderItem.order_id == order.id).first()
		entities = (
			OrderItem.qty,
			OrderItem.sub_total,
			Product.name,
			ProductSellPrice.sell_price,
			OrderItem.discount_name,
			OrderItem.discount_amount
		)
		order_item_list = OrderItem.query.with_entities(*entities) \
			.join(Product, OrderItem.product_id == Product.id)\
			.join(ProductSellPrice, OrderItem.sell_price_id == ProductSellPrice.id)\
			.filter(OrderItem.order_id == order.id).all()
		item_list = list(map(lambda x: x._asdict(), order_item_list))
		order_dict = order._asdict()
		order_dict['price_before_disc'] = price_before_disc.amount
		order_dict['item_list'] = item_list
		return {'payload': order_dict}
	
	@Key(['user_id', 'start_at', 'end_at'])
	def find_order_items_by_user_id(self, domain):
		user_id = domain['user_id']
		start_at = domain['start_at']
		end_at = domain['end_at']
		
		entities = (
			func.sum(OrderItem.qty).label('quantity'),
			func.sum(OrderItem.sub_total).label('sub_total'),
			Product.name.label('product_name'),
			func.sum(OrderItem.discount_amount).label('discount_amount'),
			OrderItem.discount_name,
			func.coalesce(func.count(OrderItem.id), 0.0).label('count_order'),
		)
		item_q = OrderItem.query.with_entities(*entities)\
			.join(Product, OrderItem.product_id == Product.id)\
			.join(Order, OrderItem.order_id == Order.id) \
			.join(ProductSellPrice, OrderItem.sell_price_id == ProductSellPrice.id) \
			.filter(between(Order.order_at, start_at, end_at)) \
			.filter(Order.user_id == user_id) \
			.group_by(Product.id, OrderItem.discount_name)\
			.order_by('sub_total').all()
		item_list = list(map(lambda x: x._asdict(), item_q))
		return {'payload': item_list}
	
	@Key(['user_id', 'start_at', 'end_at'])
	def find_order_by_user_id(self, domain):
		user_id = domain['user_id']
		start_at = domain['start_at']
		end_at = domain['end_at']
		entities = (
			func.sum(Order.discount_amount).label('discount_amount'),
			func.sum(Order.total_amount).label('total_amount'),
			func.coalesce(func.sum(Order.total_amount) + func.sum(Order.discount_amount), 0.0).label('price_before_disc')
		)
		
		order_q = Order.query.with_entities(*entities)\
			.filter(between(Order.order_at, start_at, end_at))\
			.filter(and_(Order.user_id == user_id, Order.status == OrderStatus.SUCCESS)).first()
		return {'payload': order_q._asdict()}
