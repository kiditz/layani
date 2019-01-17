from flask import Blueprint, request
from slerp import ValidationException
from slerp.logger import logging
from slerp.app import app
from service.provider_image_service import ProviderImageService
from utils.api_constant import ErrorCode
from datetime import datetime
from werkzeug.utils import secure_filename
import os

log = logging.getLogger(__name__)

provider_image_api_blue_print = Blueprint('provider_image_api_blue_print', __name__, url_prefix='/image')
api = provider_image_api_blue_print
provider_image_service = ProviderImageService()


@api.route('/add', methods=['POST'])
def add_provider_image():
	"""
	{
	"file": "String",
	"provider_id": "Long"
	}
	"""
	file = request.files['file']
	if 'file' not in request.files:
		raise ValidationException(ErrorCode.FILE_NOT_FOUND)
	upload_folder = app.config['UPLOAD_FOLDER']
	data = request.form.to_dict()
	user_dir = 'provider'
	directory = os.path.join(os.path.dirname(upload_folder), user_dir)
	if not os.path.exists(directory):
		os.makedirs(directory)
		pass
	# setting file upload
	mimetype = str(file.content_type)
	file_time = datetime.now().strftime('%Y_%m_%d_%H_%M_%S')
	original_filename = secure_filename(file.filename)
	filename = file_time + '_' + original_filename
	domain = {
		'filename': filename,
		'folder': directory,
		'mimetype': mimetype,
		'original_filename': original_filename,
		'provider_id': data["provider_id"]
	}
	final_file_path = os.path.join(directory, filename)
	file.save(final_file_path)
	return provider_image_service.add_provider_image(domain)


@api.route('/find', methods=['GET'])
def find_document():
	domain = request.args.to_dict()
	return provider_image_service.find_provider_image(domain)
