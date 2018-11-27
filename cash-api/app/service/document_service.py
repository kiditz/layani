import os

from flask import send_file
from slerp.logger import logging
from slerp.validator import Number, Blank, Key, ValidationException
from utils.api_constant import ErrorCode
from entity.models import Document

log = logging.getLogger(__name__)


class DocumentService(object):
	def __init__(self):
		super(DocumentService, self).__init__()
	
	@Blank(['filename', 'mimetype', 'original_filename'])
	def add_document(self, domain):
		if 'secure' in domain and domain['secure'] == 'N':
			domain['secure'] = False
		else:
			domain['secure'] = True
		document = Document(domain)
		document.save()
		return {'payload': document.to_dict()}
	
	@Key(['id'])
	def find_document(self, domain):
		document = Document.query.filter_by(id=domain['id']).first()
		if document is not None:
			filename = os.path.join(document.folder, document.filename if 'thumbnails' not in domain else document.thumbnails)
			return send_file(filename, mimetype=document.mimetype if 'thumbnails' not in domain else 'image/png')
		else:
			raise ValidationException(ErrorCode.FILE_NOT_FOUND)