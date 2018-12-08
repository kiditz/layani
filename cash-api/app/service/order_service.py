from datetime import datetime, date
from decimal import Decimal

from entity.models import Order, OrderItem, ProductSellPrice, ProductPurchasePrice, StockHistory, Stock, Cashbox, \
	AccountReceiveable, CashboxHistory, Customer, Merchant, Product
from slerp.app import db
from slerp.logger import logging
from slerp.validator import Number, Blank, Key, ValidationException
from sqlalchemy import between, and_, cast, func, Interval
from sqlalchemy.dialects.mssql import DATE
from utils.api_constant import StockRef, ErrorCode, PaymentMethod, OrderStatus
from utils.date_utils import get_day_of_year, get_day_of_week

log = logging.getLogger(__name__)


class OrderService(object):
	def __init__(self):
		super(OrderService, self).__init__()
	
	@Key(['merchant_id', 'customer_id', 'cash_box_id', 'total_amount', 'total_payment', 'items.use_stock'])
	def add_order(self, domain):
		order = Order(domain)
		order.merchant_id = domain['merchant_id']
		order.order_code = '-'
		order.save()
		order.order_code = str(order.id).zfill(10)
		now = datetime.now()
		profit_list = []
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
				stock_history.remark = 'Potong stok untuk penjualan #{}'.format(order.order_code)
				stock_history.stock_id = stock.id
				stock_history.save()
			
			purchase_price = ProductPurchasePrice.query.filter(
				and_(ProductPurchasePrice.product_id == item['product_id'], between(now, ProductPurchasePrice.start_at, ProductPurchasePrice.end_at))).first()
			sell_price = ProductSellPrice.query.filter(
				and_(ProductSellPrice.product_id == item['product_id'], ProductSellPrice.name == 'STANDARD')).first()
			profit_list.append(Decimal(sell_price.sell_price - purchase_price.purchase_price) * Decimal(item['qty']))
			pass
		cashbox = Cashbox.query.get(domain['cash_box_id'])
		account_receiveable = None
		if order.payment_method == PaymentMethod.CASH:
			order.status = OrderStatus.SUCCESS
			cashbox.total_amount = cashbox.total_amount + Decimal(domain['total_amount'])
			order.cashback = order.total_payment - order.total_amount			
		else:			
			cashbox.total_amount = cashbox.total_amount + Decimal(domain['total_payment'])
			order.status = OrderStatus.PENDING
			order.cashback = 0.0
			account_receiveable = AccountReceiveable()
			account_receiveable.total_credit = order.total_amount - order.total_payment
			account_receiveable.merchant_id = domain['merchant_id']
			account_receiveable.order_id = order.id
			account_receiveable.receiveable_date = datetime.now()
			account_receiveable.save()
			due_date = datetime.strptime(domain['due_date'], '%Y-%m-%d %H:%M:%S')
			account_receiveable.receiveable_date = due_date
		cashbox.merchant_id = domain['merchant_id']
		cashbox.save()
		# Cashbox History
		cashbox_history = CashboxHistory()
		cashbox_history.cash_box_id = cashbox.id		
		
		if order.payment_method == PaymentMethod.CASH:
			cashbox_history.payment_amount = Decimal(domain['total_amount'])
			cashbox_history.payment_method = PaymentMethod.DEBIT
			cashbox_history.remark = 'order.cash #' + order.order_code
		else:
			cashbox_history.payment_amount = Decimal(domain['total_payment'])
			cashbox_history.payment_method = PaymentMethod.DEBIT			
			cashbox_history.remark = 'order.credit #' + order.order_code		

		cashbox_history.save()
		order.profit = sum(profit_list)
		order.save()
		
		db.session.bulk_insert_mappings(OrderItem, order_items)
		order_dict = order.to_dict()
		if domain['customer_id'] is not None:
			customer = Customer.query.get(domain['customer_id'])
			if customer is not None:
				order_dict['customer_name'] = customer.name
		
		if account_receiveable is not None:
			order_dict['total_credit'] = account_receiveable.total_credit
			order_dict["receiveable_date"] = account_receiveable.receiveable_date
		order_dict['order_items'] = order_items
		return {'payload': order_dict}

	@Number(['order_id'])
	def refund_order(self, domain):
		order_id = domain['order_id']
		order = Order.query.get(order_id)
		order.status = OrderStatus.VOID
		order_cpy = Order(order.to_dict())
		order_cpy.id = None
		order_cpy.total_amount = order.total_amount * -1
		order_cpy.status = OrderStatus.VOID
		order.profit = 0
		order_cpy.save()
		order.save()
		cashbox = Cashbox.query.filter(Cashbox.id == order.cash_box_id).first()
		cashbox.total_amount = cashbox.total_amount - order.total_amount
		cashbox.save()
		cashbox_history = CashboxHistory()
		cashbox_history.cash_box_id = cashbox.id
		cashbox_history.payment_amount = cashbox.total_amount
		cashbox_history.payment_method = PaymentMethod.CREDIT
		cashbox_history.remark = 'order.refund #' + order.order_code
		cashbox_history.save()
		return {'payload': order.to_dict()}

	@Blank(['period', 'merchant_id'])
	def get_order_chart_data(self, domain):
		chart_label, lines_chart = get_order_chart_data_by_period(period=domain['period'], merchant_id=domain['merchant_id'])
		_, lines_chart_debt = get_order_chart_data_by_period(period = domain['period'],  status='I', merchant_id=domain['merchant_id'])
		_, lines_chart_success = get_order_chart_data_by_period(period=domain['period'], status='S', merchant_id=domain['merchant_id'])
		lines_data = [
			lines_chart,
			lines_chart_debt,
			lines_chart_success
		]
		return {'payload': {'chart_label': chart_label, 'lines_data': lines_data}}
		
	@Number(["merchant_id"])
	def get_dashboard_header(self, domain):
		merchant_id = domain['merchant_id']
		cashbox = Cashbox.query.with_entities(func.coalesce(func.sum(Cashbox.total_amount), 0).label("total_amount")).filter(Cashbox.merchant_id == merchant_id).first()._asdict()
		total_profit = Order.query.with_entities(func.coalesce(func.sum(Order.profit), 0).label("total_profit")).filter(Order.merchant_id == merchant_id).first()._asdict()
		total_income = Order.query.with_entities(func.coalesce(func.sum(Order.total_amount), 0).label("total_income")).filter(Order.merchant_id == merchant_id).first()._asdict()
		total_receiveable = AccountReceiveable.query.with_entities(func.coalesce(func.sum(AccountReceiveable.total_credit), 0).label("total_credit")).filter(AccountReceiveable.merchant_id == merchant_id).first()._asdict()
		result = {
			'cashbox_amount': cashbox['total_amount'],
			'total_receiveable': total_receiveable["total_credit"],
			'total_profit': total_profit["total_profit"],
			'total_income': total_income["total_income"]
		}
		return {'payload': result}
	
	@Blank(['period'])
	@Number(['merchant_id'])
	def get_profit_chart_data(self, domain):
		chart_label, lines_chart = get_profit_chart_data_by_period(period=domain['period'], merchant_id=domain['merchant_id'])
		_, lines_chart_debt = get_profit_chart_data_by_period(period=domain['period'], status='I', merchant_id=domain['merchant_id'])
		_, lines_chart_success = get_profit_chart_data_by_period(period=domain['period'], status='S', merchant_id=domain['merchant_id'])
		lines_data = [
			lines_chart,
			lines_chart_debt,
			lines_chart_success
		]
		return {'payload': {'chart_label': chart_label, 'lines_data': lines_data}}
	
	@Blank(['period', 'merchant_id'])
	def get_income_chart_data(self, domain):
		chart_label, lines_chart = get_income_chart_data_by_period(period=domain['period'], merchant_id=domain['merchant_id'])
		lines_data = [
			lines_chart,
			# lines_chart_debt,
			# lines_chart_success
		]
		return {'payload': {'chart_label': chart_label, 'lines_data': lines_data}}

	@Number(['order_id'])
	def get_order_items(self, domain):
		order_id = int(domain['order_id'])		
		entities = (			
			OrderItem.sub_total.label('sub_total'),
			OrderItem.qty,
			Product.name.label('product_name'),
		)
		order_items = OrderItem.query.with_entities(*entities)\
			.join(Product, Product.id == OrderItem.product_id)\
			.filter(OrderItem.order_id == order_id)\
			.order_by("product_name asc")
		order_item_list = list(map(lambda x: x._asdict(), order_items.all()))
		return {'payload': order_item_list}
	
	@Number(['merchant_id', 'page', 'size'])
	def get_top_product(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		entities = (
			func.sum(OrderItem.qty).label('quantity'),
			Product.name
		)
		item_q = OrderItem.query.with_entities(*entities)\
			.join(Product, Product.id == OrderItem.product_id)\
			.filter(Product.merchant_id == domain['merchant_id'])\
			.group_by(Product.id).order_by("quantity desc")\
			.paginate(page, size, error_out=False)
		product_list = list(map(lambda x: x._asdict(), item_q.items))
		return {'payload': product_list, 'total': item_q.total, 'total_pages': item_q.pages}

	@Number(['merchant_id', 'page', 'size'])
	def get_order_list(self, domain):
		merchant_id = domain['merchant_id']
		page = int(domain['page'])
		size = int(domain['size'])
		order_q = Order.query.filter_by(merchant_id=merchant_id).filter(cast(Order.order_at, DATE) == datetime.now().date())
		order_q = order_q.filter(Order.order_code.ilike('%' + domain['query'] + '%'))
		if 'status' in domain:
			order_q = order_q.filter(Order.status == domain['status'])
		order_q = order_q.order_by(Order.order_at.desc()).paginate(page, size, error_out=False)
		order_list = list(map(lambda x: x.to_dict(), order_q.items))
		return {'payload': order_list, 'total': order_q.total, 'total_pages': order_q.pages}


def get_order_chart_data_by_period(period=None, status=None, merchant_id=-1):
	if period == 'week':
		# Weekly
		start_date, end_date = get_day_of_week()
		entities, join = get_order_chart_count_query(start_date, end_date, status, 'YYYY-MM-DD', merchant_id=merchant_id)
	elif period == 'month':
		# Monthly
		start_date, end_date = get_day_of_year()
		entities, join = get_order_chart_count_query(start_date, end_date, status, 'YYYY-MM', merchant_id=merchant_id)
	else:
		# YEAR from 2015
		merchant = Merchant.query.get(merchant_id)
		# Mengambil data dua year sebelum merchant di buat
		start_date = date(merchant.created_at.year - 2, 1, 1)
		end_date = datetime.today().replace(month=12, day=31)
		entities, join = get_order_chart_count_query(start_date, end_date, status, 'YYYY', merchant_id=merchant_id)
		
	order_q = Order.query.with_entities(*entities).select_from(join).group_by('datetime').order_by('datetime')
	
	chart_label = list(map(lambda x: handle_chart_label(x._asdict()['datetime'], period), order_q))
	lines_data = list(map(lambda x: x._asdict()['total'], order_q))
	return chart_label, lines_data


def get_income_chart_data_by_period(period=None, status=None, merchant_id=-1):
	if period == 'week':
		# Weekly
		start_date, end_date = get_day_of_week()
		entities, join = get_order_income_query(start_date, end_date, status, 'YYYY-MM-DD', merchant_id=merchant_id)
	elif period == 'month':
		# Monthly
		start_date, end_date = get_day_of_year()
		entities, join = get_order_income_query(start_date, end_date, status, 'YYYY-MM', merchant_id=merchant_id)
	else:
		# YEAR from 2015
		merchant = Merchant.query.get(merchant_id)
		# Mengambil data dua year sebelum merchant di buat
		start_date = date(merchant.created_at.year - 2, 1, 1)
		end_date = datetime.today().replace(month=12, day=31)
		entities, join = get_order_income_query(start_date, end_date, status, 'YYYY', merchant_id=merchant_id)
	
	order_q = Order.query.with_entities(*entities).select_from(join).group_by('datetime').order_by('datetime')
	
	chart_label = list(map(lambda x: handle_chart_label(x._asdict()['datetime'], period), order_q))
	lines_data = list(map(lambda x: x._asdict()['total'], order_q))
	return chart_label, lines_data


def get_profit_chart_data_by_period(period=None, status=None, merchant_id=-1):
	if period == 'week':
		# Weekly
		start_date, end_date = get_day_of_week()
		entities, join = get_order_profit_query(start_date, end_date, status, 'YYYY-MM-DD', merchant_id=merchant_id)
	elif period == 'month':
		# Monthly
		start_date, end_date = get_day_of_year()
		entities, join = get_order_profit_query(start_date, end_date, status, 'YYYY-MM', merchant_id=merchant_id)
	else:
		# YEAR from 2015
		merchant = Merchant.query.get(merchant_id)
		# Mengambil data dua year sebelum merchant di buat
		start_date = date(merchant.created_at.year - 2, 1, 1)
		end_date = datetime.today().replace(month=12, day=31)
		entities, join = get_order_profit_query(start_date, end_date, status, 'YYYY', merchant_id=merchant_id)
	
	order_q = Order.query.with_entities(*entities).select_from(join).group_by('datetime').order_by('datetime')
	
	chart_label = list(map(lambda x: handle_chart_label(x._asdict()['datetime'], period), order_q))
	lines_data = list(map(lambda x: x._asdict()['total'], order_q))
	return chart_label, lines_data


def get_order_chart_count_query(start_date, end_date, status, fmt, merchant_id):
	stmt = db.session.query(
		func.generate_series(start_date, end_date, cast('1 day', Interval())).label('day')).subquery()
	entities = (
		func.count(Order.id).label('total'),
		func.to_char(stmt.c.day, fmt).label('datetime')
	)
	if status:
		join = stmt.outerjoin(Order, and_(cast(Order.order_at, DATE) == stmt.c.day, Order.status == status, Order.merchant_id == merchant_id))
	else:
		join = stmt.outerjoin(Order, and_(cast(Order.order_at, DATE) == stmt.c.day, Order.status != OrderStatus.VOID, Order.merchant_id == merchant_id))
	return entities, join


def get_order_profit_query(start_date, end_date, status, fmt, merchant_id):
	stmt = db.session.query(
		func.generate_series(start_date, end_date, cast('1 day', Interval())).label('day')).subquery()
	entities = (
		func.coalesce(func.sum(Order.profit), 0).label('total'),
		func.to_char(stmt.c.day, fmt).label('datetime')
	)
	if status:
		join = stmt.outerjoin(Order, and_(cast(Order.order_at, DATE) == stmt.c.day, Order.status == status, Order.merchant_id == merchant_id))
	else:
		join = stmt.outerjoin(Order, and_(cast(Order.order_at, DATE) == stmt.c.day, Order.status != OrderStatus.VOID, Order.merchant_id == merchant_id))
	return entities, join


def get_order_income_query(start_date, end_date, status, fmt, merchant_id):
	stmt = db.session.query(
		func.generate_series(start_date, end_date, cast('1 day', Interval())).label('day')).subquery()
	entities = (
		func.coalesce(func.sum(Order.total_amount), 0).label('total'),
		func.to_char(stmt.c.day, fmt).label('datetime')
	)
	if status:
		join = stmt.outerjoin(Order, and_(cast(Order.order_at, DATE) == stmt.c.day, Order.status == status, Order.merchant_id == merchant_id))
	else:
		join = stmt.outerjoin(Order, and_(cast(Order.order_at, DATE) == stmt.c.day, Order.status != OrderStatus.VOID, Order.merchant_id == merchant_id))
	return entities, join


weekly = ["Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"]


def handle_chart_label(order_at, period):
	if period == 'week':
		return weekly[datetime.strptime(order_at, '%Y-%m-%d').weekday()]
	elif period == 'month':
		return datetime.strptime(order_at, '%Y-%m').strftime('%b')
	return datetime.strptime(order_at, '%Y').strftime('%Y')
