from entity.models import OrderItem, StockHistory, Stock, AccountReceiveable, Customer, Product, \
	Discount, ProductSellPrice, ProductPurchasePrice, \
	CashboxSummary
from slerp.logger import logging
from slerp.validator import Number, Key, ValidationException
from sqlalchemy import between
from sqlalchemy.orm import aliased

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
		if order.status != OrderStatus.SUCCESS:
			raise ValidationException(ErrorCode.REFUND_FAILED)
		order.status = OrderStatus.VOID
		if 'description' in domain:
			order.description = domain['description']
		order.save()
		order_cpy = Order(order.to_dict())
		order_cpy.order_at = datetime.now()
		order_cpy.id = None
		order_cpy.total_amount = order.total_amount * -1
		order_cpy.save()
		return {'payload': order.to_dict()}

	@Number(['order_code'])
	def get_order_items(self, domain):
		order_code = domain['order_code']
		product_discount = aliased(Product, name='product_discount')
		product = aliased(Product, name='product')
		entities = (			
			OrderItem.sub_total,
			OrderItem.qty,
			product.name.label('product_name'),
			Discount.name.label('discount_name'),
			Discount.free_product_id,
			Discount.discount_type,
			Discount.method,
			Discount.amount.label('discount_amount'),
			product_discount.name.label('free_product'),
			product.unit,
			product.use_stock,
			product.id.label('product_id'),
			product.document_id,
			ProductSellPrice.sell_price,
			ProductSellPrice.id.label('sell_pricet_id'),
			
		)
		now = datetime.now()
		order_items = OrderItem.query.with_entities(*entities)\
			.join(product, product.id == OrderItem.product_id) \
			.outerjoin(Discount, Discount.id == OrderItem.discount_id) \
			.outerjoin(product_discount, Discount.free_product_id == product_discount.id)\
			.join(ProductSellPrice, and_(product.id == ProductSellPrice.product_id, ProductSellPrice.name == 'STANDARD')) \
			.join(ProductPurchasePrice, and_(ProductPurchasePrice.product_id == product.id, between(now, ProductPurchasePrice.start_at, ProductPurchasePrice.end_at))) \
			.join(Order, Order.id == OrderItem.order_id) \
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
		log.info('typeof %s', type(exclude_status))
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
		order_q = Order.query.with_entities(*entities).filter_by(outlet_id=outlet_id)\
			.outerjoin(Customer, Customer.id == Order.customer_id)\
			.outerjoin(AccountReceiveable, AccountReceiveable.order_id == Order.id)\
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
		count_order_saved = Order.query\
			.with_entities(func.count(Order.id).label('count_saved_order'))\
			.filter(and_(Order.outlet_id == domain['outlet_id'], Order.status == OrderStatus.CREATED))\
			.scalar()
		return {'payload': {'count': count_order_saved}}
	
	@Key(['outlet_id', 'date'])
	def get_order_amount_summary(self, domain):
		date_now = domain['date']
		outlet_id = domain['outlet_id']
		order_success_q = Order.query.with_entities(func.sum(Order.total_amount).label('order_summary'))\
			.filter(and_(cast(Order.order_at, DATE) == date_now, Order.outlet_id == outlet_id, Order.status == OrderStatus.SUCCESS))\
			.first()
		order_all_q = Order.query.with_entities(func.sum(Order.total_amount).label('order_summary')) \
			.filter(and_(cast(Order.order_at, DATE) == date_now, Order.outlet_id == outlet_id)) \
			.first()
		order_void_q = Order.query.with_entities(func.sum(Order.total_amount).label('order_summary'))\
			.filter(and_(cast(Order.order_at, DATE) == date_now, Order.outlet_id == outlet_id, Order.status == OrderStatus.VOID, Order.total_amount > 0.0))\
			.first()

		order_created_q = Order.query.with_entities(func.count(Order.id).label('order_summary'))\
			.filter(and_(cast(Order.order_at, DATE) == date_now, Order.outlet_id == outlet_id, Order.status == OrderStatus.CREATED, Order.total_amount > 0.0))\
			.first()
		
		order_card_q = Order.query.with_entities(func.count(Order.id).label('order_summary')) \
			.filter(and_(cast(Order.order_at, DATE) == date_now, Order.outlet_id == outlet_id, Order.payment_method == PaymentMethod.CARD, Order.total_amount > 0.0)) \
			.first()
		
		order_cash_q = Order.query.with_entities(func.count(Order.id).label('order_summary')) \
			.filter(and_(cast(Order.order_at, DATE) == date_now, Order.outlet_id == outlet_id, Order.payment_method == PaymentMethod.CASH, Order.total_amount > 0.0)) \
			.first()
		order_dict = {
			'success': order_success_q.order_summary,
			'void': order_void_q.order_summary,
			'pending': order_created_q.order_summary,
			'card': order_card_q.order_summary,
			'cash': order_cash_q.order_summary,
			'all': order_all_q.order_summary,
		}
		return {'payload': order_dict}
