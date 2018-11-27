from flask import Blueprint, request
from slerp.logger import logging

from service.customer_service import CustomerService

log = logging.getLogger(__name__)

customer_api_blue_print = Blueprint('customer_api_blue_print', __name__, url_prefix='/customer')
api = customer_api_blue_print
customer_service = CustomerService()


@api.route('/list', methods=['GET'])
def get_customer_list():
    """
    {
        "merchant_id": "Long",
        "name": "String",
    }
    """
    domain = request.args.to_dict()
    return customer_service.get_customer_list(domain)


@api.route('/edit', methods=['PUT'])
def edit_customer_by_id():

    """
    {
    "id": "Long",
    "name": "String",
    "phone_number": "String",
    "email": "String",
    "merchant_id": "Long"
    }
    """
    domain = request.get_json()
    return customer_service.edit_customer_by_id(domain)