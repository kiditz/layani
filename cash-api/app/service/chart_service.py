from datetime import timedelta
from decimal import Decimal
from entity.models import OrderItem, Product
from slerp.logger import logging
from slerp.validator import Blank, Number

from .chart_query import *

log = logging.getLogger(__name__)


class ChartService(object):
	def __init__(self):
		super(ChartService, self).__init__()
	
	@Blank(['period', 'outlet_id'])
	def get_order_chart_data(self, domain):
		chart_label, lines_chart_void = get_order_chart_data_by_period(period=domain['period'], status= OrderStatus.VOID, outlet_id=domain['outlet_id'])
		_, lines_chart_debt = get_order_chart_data_by_period(period=domain['period'], status=OrderStatus.PENDING, outlet_id=domain['outlet_id'])
		_, lines_chart_success = get_order_chart_data_by_period(period=domain['period'], status=OrderStatus.SUCCESS, outlet_id=domain['outlet_id'])
		lines_data = [
			lines_chart_void,
			lines_chart_debt,
			lines_chart_success
		]
		return {'payload': {'chart_label': chart_label, 'lines_data': lines_data}}
	
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
	
	@Number(["outlet_id"])
	def get_dashboard_header(self, domain):
		outlet_id = domain['outlet_id']
		today = date.today()
		sales_end_value = Order.query.with_entities(func.coalesce(func.sum(Order.total_amount), 0.0).label('income')) \
			.filter(Order.outlet_id == outlet_id) \
			.filter(Order.status == OrderStatus.SUCCESS)\
			.filter(cast(Order.order_at, DATE) == today) \
			.first()
		yesterday = today - timedelta(days=1)		
		sales_starting_value = Order.query.with_entities(func.coalesce(func.sum(Order.total_amount), 0.0).label('income')) \
			.filter(Order.outlet_id == outlet_id) \
			.filter(Order.status == OrderStatus.SUCCESS) \
			.filter(cast(Order.order_at, DATE) == yesterday) \
			.first()
		calculate_sales_increase = sales_end_value.income - sales_starting_value.income
		
		sales_increase_percentage = calculate_sales_increase / sales_starting_value.income if sales_starting_value.income > 0.0 else 0.0
		
		# Transaction
		trx_end_value = Order.query.with_entities(func.coalesce(func.count(Order.id), 0.0).label('count')) \
			.filter(Order.outlet_id == outlet_id) \
			.filter(Order.status == OrderStatus.SUCCESS) \
			.filter(cast(Order.order_at, DATE) == today) \
			.first()
		trx_starting_value = Order.query.with_entities(func.coalesce(func.count(Order.id), 0.0).label('count')) \
			.filter(Order.outlet_id == outlet_id) \
			.filter(Order.status == OrderStatus.SUCCESS) \
			.filter(cast(Order.order_at, DATE) == yesterday) \
			.first()
		calculate_trx_increase = trx_end_value.count - trx_starting_value.count
		
		trx_increase_percentage = calculate_trx_increase / trx_starting_value.count if trx_starting_value.count > 0.0 else 0.0
		result = {
			'sales': sales_end_value.income,
			'sales_increase': calculate_sales_increase,
			'sales_increase_percentage': round(sales_increase_percentage * Decimal(100.0)),
			'trx': trx_end_value.count,
			'trx_increase': calculate_trx_increase,
			'trx_increase_percentage': round(trx_increase_percentage * Decimal(100.0))
		}
		
		return {'payload': result}
	
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
			.filter(Order.status == OrderStatus.SUCCESS) \
			.filter(cast(Order.order_at, DATE) == date.today()) \
			.group_by(Product.id).order_by("quantity desc")\
			.paginate(page, size, error_out=False)
		product_list = list(map(lambda x: x._asdict(), item_q.items))
		return {'payload': product_list, 'total': item_q.total, 'total_pages': item_q.pages}