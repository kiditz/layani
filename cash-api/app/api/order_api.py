from flask import Blueprint, request
from slerp.app import cache
from slerp.logger import logging

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


@api.route('/items', methods=['GET'])
@cache.cached(timeout=60, query_string=True)
def get_order_items():
    domain = request.args.to_dict()
    return order_service.get_order_items(domain)


@api.route('/list', methods=['GET'])
@cache.cached(timeout=30, query_string=True)
def get_order_list():
    domain = request.args.to_dict()
    return order_service.get_order_list(domain)


@api.route('/delete_by_id', methods=['DELETE'])
def delete_order_by_id():
    domain = request.args.to_dict()
    return order_service.delete_order_by_id(domain)


@api.route('/count_saved_by_id', methods=['GET'])
def count_saved_order_by_id():
    domain = request.args.to_dict()
    return order_service.count_saved_order_by_id(domain)


@api.route('/summary', methods=['GET'])
@cache.cached(timeout=30, query_string=True)
def get_order_amount_summary():
    domain = request.args.to_dict()
    return order_service.get_order_amount_summary(domain)




@api.route('/find_order_by_id', methods=['GET'])
def find_order_by_id():

    """
    {
        "id": "Long"
    }
    """
    domain = request.args.to_dict()
    return order_service.find_order_by_id(domain)