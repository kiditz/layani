from flask import Blueprint, request
from slerp.logger import logging
from slerp.app import cache
from service.order_service import OrderService

log = logging.getLogger(__name__)

order_api_blue_print = Blueprint('order_api_blue_print', __name__, url_prefix='/order')
api = order_api_blue_print
order_service = OrderService()


@api.route('/add', methods=['POST'])
def add_order():
    """
    {
    "customer_id": "Long",
    "cash_box_id": "Long",
    "order_code": "String",
    "total_amount": "Double",
    "total_payment": "Double",
    "cashback": "Double",
    "order_at": "Date",
    "profit": "Double",
    "income": "Double"
    }
    """
    log.debug("REQUEST : %s", request.data)
    domain = request.get_json()
    return order_service.add_order(domain)


@api.route('/refund', methods=['POST'])
def refund_order():
    """
    {
    "order_id": "Long"
    }
    """
    log.debug("REQUEST : %s", request.data)
    domain = request.get_json()
    return order_service.refund_order(domain)


@api.route('/chart', methods=['GET'])
@cache.cached(timeout=10, query_string=True)
def get_order_data():
    domain = request.args.to_dict()
    return order_service.get_order_chart_data(domain)


@api.route('/profit_chart', methods=['GET'])
@cache.cached(timeout=10, query_string=True)
def get_profit_data():
    domain = request.args.to_dict()
    return order_service.get_profit_chart_data(domain)


@api.route('/income_chart', methods=['GET'])
@cache.cached(timeout=10, query_string=True)
def get_income_data():
    domain = request.args.to_dict()
    return order_service.get_income_chart_data(domain)


@api.route('/dashboard_header', methods=['GET'])
@cache.cached(timeout=10, query_string=True)
def get_dashboard():
    domain = request.args.to_dict()
    return order_service.get_dashboard_header(domain)


@api.route('/top_product', methods=['GET'])
@cache.cached(timeout=10, query_string=True)
def get_top_product():
    domain = request.args.to_dict()
    return order_service.get_top_product(domain)


@api.route('/items', methods=['GET'])
@cache.cached(timeout=60, query_string=True)
def get_order_items():
    domain = request.args.to_dict()
    return order_service.get_order_items(domain)


@api.route('/list', methods=['GET'])
def get_order_list():
    domain = request.args.to_dict()
    return order_service.get_order_list(domain)