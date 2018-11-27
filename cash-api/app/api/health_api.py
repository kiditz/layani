from flask import Blueprint

health_api_blue_print = Blueprint('health_api_blue_print', __name__)
api = health_api_blue_print


@api.route('/health', methods=['GET'])
def health():
	return {'payload': None}