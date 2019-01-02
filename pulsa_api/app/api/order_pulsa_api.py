from flask import Blueprint, request
from slerp.logger import logging

from service.order_pulsa_service import OrderPulsaService

log = logging.getLogger(__name__)

order_pulsa_api_blue_print = Blueprint('order_pulsa_api_blue_print', __name__, url_prefix='/order_pulsa')
api = order_pulsa_api_blue_print
order_pulsa_service = OrderPulsaService()


@api.route('/add', methods=['POST'])
def add_order_pulsa():
    """
    'msisdn', 'customer_id', 'outlet_id', 'sales_type', 'product_id'
    {
    "msisdn": "String",    
    "outlet_id": "Long",
    "sales_type": "String",
    "product_id": "Long"    
    }
    """
    domain = request.get_json()
    return order_pulsa_service.add_order_pulsa(domain)