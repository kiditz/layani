from flask import Blueprint, request
from slerp.logger import logging
from slerp.app import cache
from service.chart_service import ChartService

log = logging.getLogger(__name__)

chart_api_blue_print = Blueprint('chart_api_blue_print', __name__, url_prefix='/chart')
api = chart_api_blue_print
chart_service = ChartService()


@api.route('/num_of_order_chart', methods=['GET'])
@cache.cached(timeout=10, query_string=True)
def get_order_data():
    domain = request.args.to_dict()
    return chart_service.get_order_chart_data(domain)


@api.route('/profit_chart', methods=['GET'])
@cache.cached(timeout=10, query_string=True)
def get_profit_data():
    domain = request.args.to_dict()
    return chart_service.get_profit_chart_data(domain)


@api.route('/income_chart', methods=['GET'])
@cache.cached(timeout=10, query_string=True)
def get_income_data():
    domain = request.args.to_dict()
    return chart_service.get_income_chart_data(domain)


@api.route('/dashboard', methods=['GET'])
@cache.cached(timeout=10, query_string=True)
def get_dashboard():
    domain = request.args.to_dict()
    return chart_service.get_dashboard_header(domain)


@api.route('/top_product', methods=['GET'])
@cache.cached(timeout=10, query_string=True)
def get_top_product():
    domain = request.args.to_dict()
    return chart_service.get_top_product(domain)