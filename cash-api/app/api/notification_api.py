from flask import Blueprint, request
from slerp.logger import logging

from service.notification_service import NotificationService

log = logging.getLogger(__name__)

notification_api_blue_print = Blueprint('notification_api_blue_print', __name__, url_prefix='/notification')
api = notification_api_blue_print
notification_service = NotificationService()


@api.route('/token/add', methods=['POST'])
def add_notification_token():
    """
    {
    "client_id": "String",
    "user_id": "Long",
    "token": "String"
    }
    """
    domain = request.get_json()
    return notification_service.add_notification_token(domain)


@api.route('/add', methods=['POST'])
def add_notification():
    """
    {
    "client_id": "String",
    "user_id": "Long",
    "token": "String"
    }
    """
    domain = request.get_json()
    return notification_service.add_notification(domain)