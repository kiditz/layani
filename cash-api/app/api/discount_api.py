from flask import Blueprint, request
from slerp.logger import logging

from service.discount_service import DiscountService

log = logging.getLogger(__name__)

discount_api_blue_print = Blueprint('discount_api_blue_print', __name__, url_prefix='/discount')
api = discount_api_blue_print
discount_service = DiscountService()


@api.route('/add', methods=['POST'])
def add_discount():
    """
    {
    "product_id": "Long",
    "discount": "Long",
    "discount_when": "Long"
    }
    """
    domain = request.get_json()
    return discount_service.add_discount(domain)


@api.route('/by_quantity', methods=['GET'])
def find_by_quantity():
    """
    {
        "quantity": "Long",
        "outlet_id": "Long"
    }
    """
    domain = request.args.to_dict()
    return discount_service.find_discount_by_quantity(domain)


@api.route('/by_bill_amount', methods=['GET'])
def find_by_bill_amount():
    """
    {
        "bill_amount": "Double",
        "outlet_id": "Long"
    }
    """
    domain = request.args.to_dict()
    return discount_service.find_discount_by_bill_amount(domain)
