from flask import Blueprint, request
from slerp.logger import logging

from service.account_receiveable_service import AccountReceiveableService

log = logging.getLogger(__name__)

account_receiveable_api_blue_print = Blueprint('account_receiveable_api_blue_print', __name__, url_prefix='/account_receiveable')
api = account_receiveable_api_blue_print
account_receiveable_service = AccountReceiveableService()


@api.route('/list', methods=['GET'])
def get_account_receiveable_by_order_id():

    """
    {
        "page": "Long",
        "size": "Long",
        "merchant_id": "Long"
        "name":"String"
    }
    """
    domain = request.args.to_dict()
    return account_receiveable_service.get_account_receiveable_list(domain)


@api.route('/detail/list', methods=['GET'])
def get_account_receiveable_detail():

    """
    {
        "page": "Long",
        "size": "Long",
        "customer_id": "Long"
    }
    """
    domain = request.args.to_dict()
    return account_receiveable_service.get_account_receiveable_detail(domain)

@api.route('/in_age', methods=['GET'])
def get_account_receiveable_age():
    domain = request.args.to_dict()
    return account_receiveable_service.get_account_receiveable_age(domain)

@api.route('/out_of_age', methods=['GET'])
def get_account_receiveable_out_of_age():
    domain = request.args.to_dict()
    return account_receiveable_service.get_account_receiveable_out_of_age(domain)    


@api.route('/pay', methods=['PUT'])
def edit_account_receiveable():

    """
    {
    "order_id": "Long",
    "cashbox_id": "Double",
    "payment_amount": "Double"    
    }
    """
    domain = request.get_json()
    return account_receiveable_service.edit_account_receiveable(domain)