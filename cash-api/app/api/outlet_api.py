from flask import Blueprint, request
from slerp.logger import logging

from service.outlet_service import OutletService

log = logging.getLogger(__name__)

outlet_api_blue_print = Blueprint('outlet_api_blue_print', __name__)
api = outlet_api_blue_print
outlet_service = OutletService()


@api.route('/outlet/add', methods=['POST'])
def add_outlet():
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
    return outlet_service.add_outlet(domain)


@api.route('/outlet/find', methods=['GET'])
def get_outlet():

    """
    {
        "phone_number": "String"
    }
    """
    domain = request.args.to_dict()
    return outlet_service.find_outlet(domain)