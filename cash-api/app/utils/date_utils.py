from datetime import datetime, date, timedelta
import calendar


def get_day_of_month():
	today = datetime.today()
	_, end_day = calendar.monthrange(today.year, today.month)
	start_date = date(today.year, today.month, 1)
	end_date = date(today.year, today.month, end_day)
	return start_date.strftime('%Y-%m-%d'), end_date.strftime('%Y-%m-%d')


def get_day_of_year():
	today = datetime.today()
	start_date = date(today.year, 1, 1)
	end_date = date(today.year, 12, 31)
	return start_date.strftime('%Y-%m-%d'), end_date.strftime('%Y-%m-%d')


def get_day_of_week():
	today = datetime.today()
	start_date = today - timedelta(days=today.weekday())
	end_day = start_date + timedelta(days=6)
	return start_date.strftime('%Y-%m-%d'), end_day.strftime('%Y-%m-%d')