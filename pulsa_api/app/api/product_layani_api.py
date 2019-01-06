from flask import Blueprint, request
from slerp.logger import logging

from service.product_layani_service import ProductLayaniService

log = logging.getLogger(__name__)

product_layani_api_blue_print = Blueprint('product_layani_api_blue_print', __name__, url_prefix='product_layani')
api = product_layani_api_blue_print
product_layani_service = ProductLayaniService()


@api.route('/add', methods=['POST'])
def add_product_layani():
    """
    {
    "name": "String",
    "code": "String",
    "nominal": "Double",
    "provider_id": "Long"
    }
    """
    domain = request.get_json()
    return product_layani_service.add_product_layani(domain)


@api.route('/get', methods=['GET'])
def get_product_layani_by_code():

    """
    {
        "page": "Long",
        "size": "Long",
        "code": "String"
    }
    """
    domain = request.args.to_dict()
    return product_layani_service.get_product_layani_by_code(domain)