from flask import Blueprint, request, render_template
from slerp.logger import logging
from flask import g
from utils.api_constant import PAYLOAD
from .order_api import order_service

log = logging.getLogger(__name__)

receipt_api_blue_print = Blueprint('receipt_api_blue_print', __name__, url_prefix='/receipt', template_folder='templates')
api = receipt_api_blue_print


@api.route('/cash', methods=['GET'])
def get_receipt_for_recap_cash():
	domain = request.args.to_dict()
	g.lang_code = domain['lang_code']
	order = order_service.find_order_by_id(domain)[PAYLOAD]
	print(order)
	return render_template('receipt_order.html', order=order)