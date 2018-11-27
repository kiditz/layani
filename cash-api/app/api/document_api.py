import os
from flask import Blueprint, request
from slerp.logger import logging
from slerp.app import app
from slerp.exception import ValidationException, CoreException
from werkzeug.utils import secure_filename
from utils.api_constant import allowed_file, ErrorCode
from service.document_service import DocumentService
from datetime import datetime

document_api_blue_print = Blueprint('document_api_blue_print', __name__, url_prefix='/document')
api = document_api_blue_print
document_service = DocumentService()

log = logging.getLogger(__name__)


@api.route('/add', methods=['POST'])
def add_document():
	if 'file' not in request.files:
		raise ValidationException(ErrorCode.FILE_NOT_FOUND)
	file = request.files['file']
	if file.filename == '':
		raise ValidationException(ErrorCode.FILE_CANNOT_BE_EMPTY)
	
	upload_folder = app.config['UPLOAD_FOLDER']
	data = request.form.to_dict()
	user_dir = '' if 'directory' not in data else data['directory']
	
	directory = os.path.join(os.path.dirname(upload_folder), user_dir)
	if not os.path.exists(directory):
		os.makedirs(directory)
	
	if file and allowed_file(file.filename):
		# setting file upload
		mimetype = str(file.content_type)
		file_time = datetime.now().strftime('%Y_%m_%d_%H_%M_%S')
		original_filename = secure_filename(file.filename)
		filename = file_time + '_' + original_filename
		domain = {
			'filename': filename,
			'folder': directory,
			'mimetype': mimetype,
			'original_filename': original_filename
		}
		# Check if data has secure key
		if 'secure' in data:
			domain['secure'] = data['secure']
		final_file_path = os.path.join(directory, filename)
		file.save(final_file_path)
		return document_service.add_document(domain)
	raise CoreException(ErrorCode.UPLOAD_FAIL)


@api.route('/find', methods=['GET'])
def find_document():
	domain = request.args.to_dict()
	return document_service.find_document(domain)
