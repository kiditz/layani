import os

from entity.models import ProviderImage
from flask import send_file
from slerp.logger import logging
from slerp.validator import Key, ValidationException

from utils.api_constant import ErrorCode

log = logging.getLogger(__name__)


class ProviderImageService(object):
	def __init__(self):
		super(ProviderImageService, self).__init__()

	@Key(['filename', 'original_filename', 'mimetype', 'provider_id'])
	def add_provider_image(self, domain):
		provider_image = ProviderImage(domain)
		provider_image.save()
		return {'payload': provider_image.to_dict()}
	
	@Key(['id'])
	def find_provider_image(self, domain):
		document = ProviderImage.query.filter(ProviderImage.provider_id == domain['id']).first()
		if document is not None:
			filename = os.path.join(document.folder, document.filename if 'thumbnails' not in domain else document.thumbnails)
			return send_file(filename, mimetype=document.mimetype if 'thumbnails' not in domain else 'image/png')
		else:
			raise ValidationException(ErrorCode.FILE_NOT_FOUND)
