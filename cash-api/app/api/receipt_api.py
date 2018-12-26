from flask import Blueprint, request, render_template
from slerp.logger import logging
from flask import g
from utils.api_constant import PAYLOAD
from .order_api import order_service
from .cashbox_api import cashbox_service
log = logging.getLogger(__name__)

receipt_api_blue_print = Blueprint('receipt_api_blue_print', __name__, url_prefix='/receipt', template_folder='templates')
api = receipt_api_blue_print


@api.route('/order', methods=['GET'])
def get_receipt_for_order():
	domain = request.args.to_dict()
	g.lang_code = domain['lang_code']
	order = order_service.find_order_by_id(domain)[PAYLOAD]
	return render_template('receipt_order.html', order=order)


@api.route('/cash/view', methods=['GET'])
def get_receipt_for_recap_cash():
	domain = request.args.to_dict()
	g.lang_code = domain['lang_code']
	summary = cashbox_service.find_cashbox_summary(domain)[PAYLOAD]
	return render_template('cash_recapitulation_view.html', summary=summary)


@api.route('/cash/print', methods=['GET'])
def get_receipt_for_print_cash():
	domain = request.args.to_dict()
	g.lang_code = domain['lang_code']
	summary = cashbox_service.find_cashbox_summary(domain)[PAYLOAD]
	order_input = domain
	order_input['start_at'] = summary['start_at'].strftime('%Y-%m-%d %H:%M:%S')
	order_input['end_at'] = summary['end_at'].strftime('%Y-%m-%d %H:%M:%S')
	order_items = order_service.find_order_items_by_user_id(order_input)[PAYLOAD]
	return render_template('cash_recapitulation_print.html', summary=summary, order_items=order_items)

