from flask import Blueprint, request
from slerp.logger import logging

from service.merchant_service import MerchantService

log = logging.getLogger(__name__)

merchant_api_blue_print = Blueprint('merchant_api_blue_print', __name__)
api = merchant_api_blue_print
merchant_service = MerchantService()


@api.route('/merchant/add', methods=['POST'])
def add_merchant():
    """
    {
    "name": "String",
    "phone_number": "String",
    "address": "String",
    "email": "String",
    "user": "Long"
    }
    """
    domain = request.get_json()
    return merchant_service.add_merchant(domain)


@api.route('/merchant/find', methods=['GET'])
def get_merchant():

    """
    {
        "phone_number": "String"
    }
    """
    domain = request.args.to_dict()
    return merchant_service.find_merchant(domain)