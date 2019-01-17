from slerp.validator import Key, Number, Blank
from slerp.logger import logging
from slerp.app import db

from entity.models import ProviderImage


log = logging.getLogger(__name__)


class ProviderImageService(object):
	def __init__(self):
		super(ProviderImageService, self).__init__()

	@Key(['filename', 'original_filename', 'thumbnails', 'mimetype', 'folder', 'secure', 'provider_id'])
	def add_provider_image(self, domain):
		provider_image = ProviderImage(domain)
		provider_image.save()
		return {'payload': provider_image.to_dict()}