from flask import Blueprint, request
from slerp.logger import logging

from service.cashbox_service import CashboxService

log = logging.getLogger(__name__)

cashbox_api_blue_print = Blueprint('cashbox_api_blue_print', __name__, url_prefix='/cashbox')
api = cashbox_api_blue_print
cashbox_service = CashboxService()


@api.route('/list', methods=['GET'])
def get_cashbox_by_merchant_id():

    """
    {
        "page": "Long",
        "size": "Long",
        "merchant_id": "Long"
    }
    """
    domain = request.args.to_dict()
    return cashbox_service.get_cashbox_by_merchant_id(domain)