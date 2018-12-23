from flask import Blueprint, request
from slerp.app import cache
from slerp.logger import logging

from service.cashbox_service import CashboxService

log = logging.getLogger(__name__)

cashbox_api_blue_print = Blueprint('cashbox_api_blue_print', __name__, url_prefix='/cashbox')
api = cashbox_api_blue_print
cashbox_service = CashboxService()


@api.route('/summary/list', methods=['GET'])
@cache.cached(timeout=30, query_string=True)
def get_cashbox_summary():

    """
    {
        "page": "Long",
        "size": "Long",
        "outlet_id": "Long"
    }
    """
    domain = request.args.to_dict()
    return cashbox_service.get_cashbox_summary(domain)


@api.route('/history/list', methods=['GET'])
@cache.cached(timeout=30, query_string=True)
def get_cashbox_history():

    """
    {
        "page": "Long",
        "size": "Long",
        "cash_box_summary_id": "Long"
    }
    """
    domain = request.args.to_dict()
    return cashbox_service.get_cashbox_history(domain)


@api.route('/history/edit', methods=['PUT'])
def edit_cash_history():

    """
    {
        "outlet_id": "Long"
        "total_amount": "Double"
        "remark": "String"
    }
    """
    domain = request.get_json()
    return cashbox_service.edit_cashbox_history(domain)
