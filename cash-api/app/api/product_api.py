from flask import Blueprint, request
from slerp.logger import logging

from service.product_service import ProductService

log = logging.getLogger(__name__)

product_api_blue_print = Blueprint('product_api_blue_print', __name__,url_prefix='/product')
api = product_api_blue_print
product_service = ProductService()


@api.route('/add', methods=['POST'])
def add_product():
    """
    {
    "category_id": "Long",
    "name": "String",
    "code": "String",
    "product_type": "String",
    "document_id": "Long",
    "sell_price": "Double",
    "qty": "Long"
    }
    """
    domain = request.get_json()
    return product_service.add_product(domain)


@api.route('/list', methods=['GET'])
def get_product_list():
    """
    {
        "category_id": "Long",
        "name": "String",
        "page": 1,
        "size": 10,
    }
    """
    domain = request.args.to_dict()
    return product_service.get_product_list(domain)


@api.route('/edit', methods=['PUT'])
def edit_product_by_id():

    """
    {
    "id": "Long",
    "category_id": "Long",
    "name": "String",
    "code": "String",
    "product_type": "String",
    "document_id": "Long",
    "use_stock": "Boolean",
    "merchant_id": "Long"
    }
    """
    domain = request.get_json()
    return product_service.edit_product_by_id(domain)


@api.route('/find_by_code', methods=['GET'])
def find_product_by_code():

    """
    {
        "code": "String"
    }
    """
    domain = request.args.to_dict()
    return product_service.find_product_by_code(domain)