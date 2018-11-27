from flask import Blueprint, request
from slerp.logger import logging

from service.stock_service import StockService

log = logging.getLogger(__name__)

stock_api_blue_print = Blueprint('stock_api_blue_print', __name__, url_prefix='/stock')
api = stock_api_blue_print
stock_service = StockService()


@api.route('/add', methods=['POST'])
def add_stock():
    """
    {
    "product_id": "Long",
    "purchase_price": "Long",
    "quantity": "Long",
    "start_date": "Date"
    }
    """
    domain = request.get_json()
    return stock_service.add_stock(domain)