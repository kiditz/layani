from entity.models import Outlet, User, Authority, Cashbox
from flask_bcrypt import Bcrypt
from slerp.app import app
from slerp.logger import logging
from slerp.validator import Key, Blank, ValidationException

from utils.api_constant import ErrorCode

log = logging.getLogger(__name__)
bcrypt = Bcrypt(app=app)


class OutletService(object):
	def __init__(self):
		super(OutletService, self).__init__()
	
	@Key(['store.name', 'store.phone_number', 'store.address', 'store.email', 'username', 'fullname', 'password'])
	def add_outlet(self, domain):
		password = domain['password']
		phone_number = domain['store']['phone_number']
		email = domain['store']['email']
		user_count = User.query.filter_by(username=domain['username']).count()
		# Validate unique by username
		if user_count > 0:
			raise ValidationException(ErrorCode.USER_HAS_EXISTS)
		# Validate unique by phone_number
		outlet_count = Outlet.query.filter_by(phone_number=phone_number).count()
		if outlet_count > 0:
			raise ValidationException(ErrorCode.PHONE_NUMBER_HAS_EXISTS)

		outlet_count = Outlet.query.filter_by(email=email).count()
		if outlet_count > 0:
			raise ValidationException(ErrorCode.EMAIL_HAS_EXISTS)

		if phone_number.startswith('0'):
			phone_number = phone_number.replace('0', '+62', 1)		
		user = User(domain)
		user.username = domain['username']
		user.fullname = domain['password']
		
		user.phone_number = phone_number
		user.outlet_name = domain['store']['name']
		user.hash_password = bcrypt.generate_password_hash(password, 10).replace(b'$2b$', b'$2a$')
		user.save()
		# Save into authority admin
		authority = Authority({'authority': 'ADMINISTRATOR', 'user_id': user.id})
		authority.save()
		domain['store']['name'] = 'Outlet 1'
		outlet = Outlet(domain['store'])
		outlet.user_id = user.id
		outlet.save()
		outlet_dict = outlet.to_dict()
		outlet_dict['password'] = password
		outlet_dict['username'] = domain["username"]
		cashbox = Cashbox()
		cashbox.name = 'Cash'
		cashbox.total_amount = 0.0
		cashbox.outlet_id = outlet.id
		cashbox.save()
		cashbox = Cashbox()
		cashbox.name = 'Bank'
		cashbox.total_amount = 0.0
		cashbox.outlet_id = outlet.id
		cashbox.save()
		return {'payload': outlet_dict}
	
	@Blank(['username', 'password'])
	def find_outlet(self, domain):
		"""
		:param domain:input is the username or phone number registered
		:return dict:
		"""
		self.check_password(domain['username'], domain['password'])
		entities = (
			Outlet.name,
			Outlet.phone_number,
			Outlet.address,
			Outlet.email,
			Outlet.id,
			User.id.label('user_id'),
			User.outlet_name,
			User.username
		)
		outlet = Outlet.query.with_entities(*entities).join(User, User.id == Outlet.user_id) \
			.filter(User.username == domain['username']) \
			.first()
		outlet_dict = outlet._asdict()
		outlet_dict['username'] = domain["username"]
		return {'payload': outlet_dict}
	
	@staticmethod
	def check_password(username, password):
		user = User.query.filter(User.username == username).first()
		
		if user is None:
			raise ValidationException(ErrorCode.USER_NOT_FOUND)
		
		if not bcrypt.check_password_hash(user.hash_password, password):
			raise ValidationException(ErrorCode.USER_WRONG_PASSWORD)
