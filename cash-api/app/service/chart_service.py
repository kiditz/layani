from entity.models import OrderItem, Product
from slerp.logger import logging
from slerp.validator import Blank, Number
from sqlalchemy import cast
from datetime import timedelta
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
		end_value = Order.query.with_entities(func.coalesce(func.sum(Order.total_amount), 0.0).label('income')) \
			.filter(Order.outlet_id == outlet_id) \
			.filter(Order.status == OrderStatus.SUCCESS)\
			.filter(cast(Order.order_at, DATE) == today) \
			.first()
		yesterday = today - timedelta(days=1)
		log.info("Yesterday : {}".format(yesterday))
		starting_value = Order.query.with_entities(func.coalesce(func.sum(Order.total_amount), 0.0).label('income')) \
			.filter(Order.outlet_id == outlet_id) \
			.filter(Order.status == OrderStatus.SUCCESS) \
			.filter(cast(Order.order_at, DATE) == yesterday) \
			.first()
		calculate_sales_increase = end_value.income - starting_value.income
		log.info("Starting Value : {}".format(starting_value))
		log.info("End Value : {}".format(end_value))
		log.info("Calculate Sales Increase : {}".format(calculate_sales_increase))
		percentage = calculate_sales_increase / starting_value.income if starting_value.income > 0.0 else 0.0
		result = {
			'income': end_value.income,
			'sales_increase_percentage': percentage
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
			.filter(Order.status == OrderStatus.SUCCESS)\
			.group_by(Product.id).order_by("quantity desc")\
			.paginate(page, size, error_out=False)
		product_list = list(map(lambda x: x._asdict(), item_q.items))
		return {'payload': product_list, 'total': item_q.total, 'total_pages': item_q.pages}