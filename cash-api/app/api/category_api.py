from flask import Blueprint, request
from slerp.logger import logging

from service.category_service import CategoryService

log = logging.getLogger(__name__)

category_api_blue_print = Blueprint('category_api_blue_print', __name__, url_prefix='/category')
api = category_api_blue_print
category_service = CategoryService()


@api.route('/add', methods=['POST'])
def add_category():
    """
    {
    "merchant_id": "Long",
    "name": "String"
    }
    """
    domain = request.get_json()
    return category_service.add_category(domain)


@api.route('/list', methods=['GET'])
def get_category_list():

    """
    {
        "page": "Long",
        "size": "Long",
        "merchant_id": "Long"
        "name": "String"
    }
    """
    domain = request.args.to_dict()
    return category_service.get_category_list(domain)


@api.route('/find', methods=['GET'])
def find_category_by_id():
    """
    {
        "id": "Long",
        "merchant_id": "Long",
    }
    """
    domain = request.args.to_dict()
    return category_service.find_category_by_id(domain)


@api.route('/edit', methods=['PUT'])
def edit_category_by_id():

    """
    {
    "id": "Long",
    "merchant_id": "Long",
    "name": "String"
    }
    """
    domain = request.get_json()
    return category_service.edit_category_by_id(domain)


@api.route('/delete', methods=['DELETE'])
def delete_category_by_id():
    domain = request.args.to_dict()
    return category_service.delete_category_by_id(domain)