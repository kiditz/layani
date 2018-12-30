from entity.models import AccountReceiveable, OrderItem, Product
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
		order = Order.query.with_entities(func.sum(Order.total_amount))
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
			.filter(and_(Order.status != OrderStatus.VOID, Order.status != OrderStatus.CREATED))\
			.group_by(Product.id).order_by("quantity desc")\
			.paginate(page, size, error_out=False)
		product_list = list(map(lambda x: x._asdict(), item_q.items))
		return {'payload': product_list, 'total': item_q.total, 'total_pages': item_q.pages}