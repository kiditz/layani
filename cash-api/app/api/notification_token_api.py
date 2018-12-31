from flask import Blueprint, request
from slerp.logger import logging

from service.notification_token_service import NotificationTokenService

log = logging.getLogger(__name__)

notification_token_api_blue_print = Blueprint('notification_token_api_blue_print', __name__, url_prefix='/notification')
api = notification_token_api_blue_print
notification_token_service = NotificationTokenService()


@api.route('/add', methods=['POST'])
def add_notification_token():
    """
    {
    "client_id": "String",
    "user_id": "Long",
    "token": "String"
    }
    """
    domain = request.get_json()
    return notification_token_service.add_notification_token(domain)