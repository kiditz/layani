
from datetime import datetime, date

from entity.models import Order, Outlet
from slerp.app import db
from sqlalchemy import and_, cast, func, Interval
from sqlalchemy.dialects.mssql import DATE

from utils.api_constant import OrderStatus
from utils.date_utils import get_day_of_year, get_day_of_week


def get_order_chart_data_by_period(period=None, status=None, outlet_id=-1):
	if period == 'week':
		# Weekly
		start_date, end_date = get_day_of_week()
		entities, join = get_order_chart_count_query(start_date, end_date, status, 'YYYY-MM-DD', outlet_id=outlet_id)
	elif period == 'month':
		# Monthly
		start_date, end_date = get_day_of_year()
		entities, join = get_order_chart_count_query(start_date, end_date, status, 'YYYY-MM', outlet_id=outlet_id)
	else:
		# YEAR from 2015
		outlet = Outlet.query.get(outlet_id)
		# Mengambil data dua year sebelum outlet di buat
		start_date = date(outlet.created_at.year - 2, 1, 1)
		end_date = datetime.today().replace(month=12, day=31)
		entities, join = get_order_chart_count_query(start_date, end_date, status, 'YYYY', outlet_id=outlet_id)
	
	order_q = Order.query.with_entities(*entities).select_from(join).group_by('datetime').order_by('datetime')
	
	chart_label = list(map(lambda x: handle_chart_label(x._asdict()['datetime'], period), order_q))
	lines_data = list(map(lambda x: x._asdict()['total'], order_q))
	return chart_label, lines_data


def get_income_chart_data_by_period(period=None, status=None, outlet_id=-1):
	if period == 'week':
		# Weekly
		start_date, end_date = get_day_of_week()
		entities, join = get_order_income_query(start_date, end_date, status, 'YYYY-MM-DD', outlet_id=outlet_id)
	elif period == 'month':
		# Monthly
		start_date, end_date = get_day_of_year()
		entities, join = get_order_income_query(start_date, end_date, status, 'YYYY-MM', outlet_id=outlet_id)
	else:
		# YEAR from 2015
		outlet = Outlet.query.get(outlet_id)
		# Mengambil data dua year sebelum outlet di buat
		start_date = date(outlet.created_at.year - 2, 1, 1)
		end_date = datetime.today().replace(month=12, day=31)
		entities, join = get_order_income_query(start_date, end_date, status, 'YYYY', outlet_id=outlet_id)
	
	order_q = Order.query.with_entities(*entities).select_from(join).group_by('datetime').order_by('datetime')
	
	chart_label = list(map(lambda x: handle_chart_label(x._asdict()['datetime'], period), order_q))
	lines_data = list(map(lambda x: x._asdict()['total'], order_q))
	return chart_label, lines_data


def get_profit_chart_data_by_period(period=None, status=None, outlet_id=-1):
	if period == 'week':
		# Weekly
		start_date, end_date = get_day_of_week()
		entities, join = get_order_profit_query(start_date, end_date, status, 'YYYY-MM-DD', outlet_id=outlet_id)
	elif period == 'month':
		# Monthly
		start_date, end_date = get_day_of_year()
		entities, join = get_order_profit_query(start_date, end_date, status, 'YYYY-MM', outlet_id=outlet_id)
	else:
		# YEAR from 2015
		outlet = Outlet.query.get(outlet_id)
		# Mengambil data dua year sebelum outlet di buat
		start_date = date(outlet.created_at.year - 2, 1, 1)
		end_date = datetime.today().replace(month=12, day=31)
		entities, join = get_order_profit_query(start_date, end_date, status, 'YYYY', outlet_id=outlet_id)
	
	order_q = Order.query.with_entities(*entities).select_from(join).group_by('datetime').order_by('datetime')
	
	chart_label = list(map(lambda x: handle_chart_label(x._asdict()['datetime'], period), order_q))
	lines_data = list(map(lambda x: x._asdict()['total'], order_q))
	return chart_label, lines_data


def get_order_chart_count_query(start_date, end_date, status, fmt, outlet_id):
	stmt = db.session.query(
		func.generate_series(start_date, end_date, cast('1 day', Interval())).label('day')).subquery()
	entities = (
		func.count(Order.id).label('total'),
		func.to_char(stmt.c.day, fmt).label('datetime')
	)
	if status:
		join = stmt.outerjoin(Order, and_(cast(Order.order_at, DATE) == stmt.c.day, Order.status == status,
		                                  Order.outlet_id == outlet_id))
	else:
		join = stmt.outerjoin(Order, and_(cast(Order.order_at, DATE) == stmt.c.day, Order.status != OrderStatus.VOID,
		                                  Order.outlet_id == outlet_id))
	return entities, join


def get_order_profit_query(start_date, end_date, status, fmt, outlet_id):
	stmt = db.session.query(
		func.generate_series(start_date, end_date, cast('1 day', Interval())).label('day')).subquery()
	entities = (
		func.coalesce(func.sum(Order.profit), 0).label('total'),
		func.to_char(stmt.c.day, fmt).label('datetime')
	)
	if status:
		join = stmt.outerjoin(Order, and_(cast(Order.order_at, DATE) == stmt.c.day, Order.status == status,
		                                  Order.outlet_id == outlet_id))
	else:
		join = stmt.outerjoin(Order, and_(cast(Order.order_at, DATE) == stmt.c.day, Order.status != OrderStatus.VOID,
		                                  Order.outlet_id == outlet_id))
	return entities, join


def get_order_income_query(start_date, end_date, status, fmt, outlet_id):
	stmt = db.session.query(
		func.generate_series(start_date, end_date, cast('1 day', Interval())).label('day')).subquery()
	entities = (
		func.coalesce(func.sum(Order.total_amount), 0).label('total'),
		func.to_char(stmt.c.day, fmt).label('datetime')
	)
	if status:
		join = stmt.outerjoin(Order, and_(cast(Order.order_at, DATE) == stmt.c.day, Order.status == status,
		                                  Order.outlet_id == outlet_id))
	else:
		join = stmt.outerjoin(Order, and_(cast(Order.order_at, DATE) == stmt.c.day, Order.status != OrderStatus.VOID,
		                                  Order.outlet_id == outlet_id))
	return entities, join


weekly = ["Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"]


def handle_chart_label(order_at, period):
	if period == 'week':
		return weekly[datetime.strptime(order_at, '%Y-%m-%d').weekday()]
	elif period == 'month':
		return datetime.strptime(order_at, '%Y-%m').strftime('%b')
	return datetime.strptime(order_at, '%Y').strftime('%Y')
