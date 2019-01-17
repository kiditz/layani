from flask import Blueprint, request
from slerp.logger import logging

from service.provider_image_service import ProviderImageService

log = logging.getLogger(__name__)

provider_image_api_blue_print = Blueprint('provider_image_api_blue_print', __name__)
api = provider_image_api_blue_print
provider_image_service = ProviderImageService()


@api.route('/add_provider_image', methods=['POST'])
def add_provider_image():
    """
    {
    "filename": "String",
    "original_filename": "String",
    "thumbnails": "String",
    "mimetype": "String",
    "folder": "String",
    "secure": "Boolean",
    "provider_id": "Long"
    }
    """
    domain = request.get_json()
    return provider_image_service.add_provider_image(domain)