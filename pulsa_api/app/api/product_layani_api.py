from flask import Blueprint, request
from slerp.logger import logging

from service.product_layani_service import ProductLayaniService

log = logging.getLogger(__name__)

product_layani_api_blue_print = Blueprint('product_layani_api_blue_print', __name__, url_prefix='/product_layani')
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


@api.route('/products', methods=['GET'])
def get_product_layani_by_prefix():
    """
    {
        "page": "Long",
        "size": "Long",
        "phone_number": "String"
    }
    """
    domain = request.args.to_dict()
    return product_layani_service.get_product_pulsa_prefix(domain)


@api.route('/categories', methods=['GET'])
def get_category_layani_by_name():

    """
    {
    }
    """
    return product_layani_service.get_category_layani()
