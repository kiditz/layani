from decimal import Decimal

from entity.models import OrderItem, ProductSellPrice, ProductPurchasePrice, StockHistory, Stock, Cashbox, \
	AccountReceiveable, CashboxHistory, Customer, Product
from slerp.logger import logging
from slerp.validator import Number, Blank, Key, ValidationException
from sqlalchemy import between

from utils.api_constant import StockRef, ErrorCode, PaymentMethod, CashboxType, CashDrawer
from .chart_query import *

log = logging.getLogger(__name__)


class OrderService(object):
	def __init__(self):
		super(OrderService, self).__init__()
	
	@Key(['outlet_id', 'customer_id', 'items.use_stock', 'items.discount_amount', 'items.discount_type'])
	@Number(['user_id'])
	def add_order(self, domain):
		outlet_id = domain['outlet_id']
		order_id = domain['order_id'] if 'order_id' in domain else -1
		order = Order.query.filter_by(id=order_id).first()
		if order is None:
			order = Order(domain)
			order.save()
			order.order_code = str(order.id).zfill(10)
		
		if 'total_amount' in domain and 'total_payment' in domain:
			total_amount = domain['total_amount']
			total_payment = domain['total_payment']
			if total_payment < total_amount:
				raise ValidationException(ErrorCode.INVALID_TOTAL_AMOUNT)
			cashbox = Cashbox.query.filter(
				and_(Cashbox.outlet_id == outlet_id, Cashbox.name == CashDrawer.CASH_DRAWER)).first()
			order.status = OrderStatus.SUCCESS
			cashbox.total_amount = cashbox.total_amount + Decimal(domain['total_amount'])
			order.cashback = order.total_payment - order.total_amount
			cashbox.outlet_id = domain['outlet_id']
			cashbox.save()
			# Cashbox History
			cashbox_history = CashboxHistory()
			cashbox_history.cash_box_id = cashbox.id
			cashbox_history.amount = Decimal(domain['total_amount'])
			cashbox_history.payment_method = CashboxType.DEBIT
			cashbox_history.remark = 'order.cash #' + order.order_code
			cashbox_history.save()
		
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
			
		order.save()
		order_dict = order.to_dict()
		if domain['customer_id'] is not None:
			customer = Customer.query.get(domain['customer_id'])
			if customer is not None:
				order_dict['customer_name'] = customer.name			
		
		order_dict['order_items'] = domain['items']
		return {'payload': order_dict}

	@Number(['order_id'])
	def refund_order(self, domain):
		order_id = domain['order_id']
		order = Order.query.get(order_id)
		if order.status != OrderStatus.SUCCESS:
			raise ValidationException(ErrorCode.REFUND_FAILED)
		order.status = OrderStatus.VOID
		cashbox = Cashbox.query.filter(Cashbox.id == order.cash_box_id).first()
		cashbox.total_amount = cashbox.total_amount - order.total_amount
		cashbox.save()
		order.total_amount = order.total_amount * -1
		order.save()
		order_cpy = Order(order.to_dict())
		order_cpy.order_at = datetime.now()
		order_cpy.id = None
		order_cpy.total_amount = order.total_amount * -1
		order_cpy.save()
		cashbox_history = CashboxHistory()
		cashbox_history.cash_box_id = cashbox.id
		cashbox_history.amount = cashbox.total_amount
		cashbox_history.payment_method = PaymentMethod.CREDIT
		cashbox_history.remark = 'order.refund #' + order.order_code
		cashbox_history.save()
		return {'payload': order.to_dict()}

	@Blank(['period', 'outlet_id'])
	def get_order_chart_data(self, domain):
		chart_label, lines_chart_void = get_order_chart_data_by_period(period=domain['period'], status=OrderStatus.VOID, outlet_id=domain['outlet_id'])
		_, lines_chart_debt = get_order_chart_data_by_period(period=domain['period'],  status=OrderStatus.PENDING, outlet_id=domain['outlet_id'])
		_, lines_chart_success = get_order_chart_data_by_period(period=domain['period'], status=OrderStatus.SUCCESS, outlet_id=domain['outlet_id'])
		lines_data = [
			lines_chart_void,
			lines_chart_debt,
			lines_chart_success
		]
		return {'payload': {'chart_label': chart_label, 'lines_data': lines_data}}
		
	@Number(["outlet_id"])
	def get_dashboard_header(self, domain):
		outlet_id = domain['outlet_id']
		cashbox = Cashbox.query.with_entities(func.coalesce(func.sum(Cashbox.total_amount), 0).label("total_amount"))\
			.filter(Cashbox.outlet_id == outlet_id)\
			.first()._asdict()
		total_profit = Order.query.with_entities(func.coalesce(func.sum(Order.profit), 0).label("total_profit"))\
			.filter(Order.outlet_id == outlet_id) \
			.filter(and_(Order.status != OrderStatus.VOID, Order.status != OrderStatus.CREATED)) \
			.first()._asdict()
		total_income = Order.query.with_entities(func.coalesce(func.sum(Order.total_amount), 0).label("total_income"))\
			.filter(Order.outlet_id == outlet_id) \
			.filter(Order.status == OrderStatus.SUCCESS) \
			.first()._asdict()
		total_receiveable = AccountReceiveable.query.with_entities(func.coalesce(func.sum(AccountReceiveable.total_credit), 0).label("total_credit")).filter(AccountReceiveable.outlet_id == outlet_id).first()._asdict()
		result = {
			'cashbox_amount': cashbox['total_amount'],
			'total_receiveable': total_receiveable["total_credit"],
			'total_profit': total_profit["total_profit"],
			'total_income': total_income["total_income"]
		}
		return {'payload': result}
	
	@Blank(['period'])
	@Number(['outlet_id'])
	def get_profit_chart_data(self, domain):
		chart_label, lines_chart = get_profit_chart_data_by_period(period=domain['period'], status=OrderStatus.VOID, outlet_id=domain['outlet_id'])
		_, lines_chart_debt = get_profit_chart_data_by_period(period=domain['period'], status=OrderStatus.PENDING, outlet_id=domain['outlet_id'])
		_, lines_chart_success = get_profit_chart_data_by_period(period=domain['period'], status=OrderStatus.SUCCESS, outlet_id=domain['outlet_id'])
		lines_data = [
			lines_chart,
			lines_chart_debt,
			lines_chart_success
		]
		return {'payload': {'chart_label': chart_label, 'lines_data': lines_data}}
	
	@Blank(['period', 'outlet_id'])
	def get_income_chart_data(self, domain):
		chart_label, lines_chart = get_income_chart_data_by_period(period=domain['period'], outlet_id=domain['outlet_id'])
		lines_data = [
			lines_chart,
			# lines_chart_debt,
			# lines_chart_success
		]
		return {'payload': {'chart_label': chart_label, 'lines_data': lines_data}}

	@Number(['order_code'])
	def get_order_items(self, domain):
		order_code = domain['order_code']
		entities = (			
			OrderItem.sub_total,
			OrderItem.qty,
			Product.name.label('product_name'),
		)
		order_items = OrderItem.query.with_entities(*entities)\
			.join(Product, Product.id == OrderItem.product_id) \
			.join(Order, Order.id == OrderItem.order_id) \
			.filter(Order.order_code == order_code)\
			.order_by("product_name asc")
		order_item_list = list(map(lambda x: x._asdict(), order_items.all()))
		return {'payload': order_item_list}
	
	@Number(['outlet_id', 'page', 'size'])
	def get_top_product(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		entities = (
			func.sum(OrderItem.qty).label('quantity'),
			Product.name
		)
		item_q = OrderItem.query.with_entities(*entities)\
			.join(Product, Product.id == OrderItem.product_id) \
			.join(Order, Order.id == OrderItem.order_id) \
			.filter(Product.outlet_id == domain['outlet_id'])\
			.filter(and_(Order.status != OrderStatus.VOID, Order.status != OrderStatus.CREATED))\
			.group_by(Product.id).order_by("quantity desc")\
			.paginate(page, size, error_out=False)
		product_list = list(map(lambda x: x._asdict(), item_q.items))
		return {'payload': product_list, 'total': item_q.total, 'total_pages': item_q.pages}

	@Number(['outlet_id', 'page', 'size'])
	@Key(['query'])
	def get_order_list(self, domain):
		outlet_id = domain['outlet_id']
		status = domain['status'] if 'status' in domain else None
		page = int(domain['page'])
		size = int(domain['size'])
		entities = (
			Order.id,
			Order.total_amount,
			Order.total_payment,
			Order.order_code,
			Order.status,
			Order.cash_box_id,
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
		if status:
			order_q = order_q.filter(Order.status == domain['status'])
		order_q = order_q.order_by(Order.order_code.desc(), Order.order_at.desc()).paginate(page, size, error_out=False)
		order_list = list(map(lambda x: x._asdict(), order_q.items))
		return {'payload': order_list, 'total': order_q.total, 'total_pages': order_q.pages}
