from flask import Blueprint, request
from slerp.logger import logging
from slerp.app import cache
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
@cache.cached(timeout=30, query_string=True)
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
@cache.cached(timeout=30, query_string=True)
def get_category_layani_by_name():
    """
    {
    }
    """
    return product_layani_service.get_category_layani()


@api.route('/providers', methods=['GET'])
@cache.cached(timeout=30, query_string=True)
def get_provider():
    domain = request.args.to_dict()
    return product_layani_service.get_provider(domain)


@api.route('/products_by_provider', methods=['GET'])
@cache.cached(timeout=30, query_string=True)
def get_product_by_provider():
    """
    {
        "page": "Long",
        "size": "Long",
        "phone_number": "String"
    }
    """
    domain = request.args.to_dict()
    return product_layani_service.get_product_by_provider(domain)