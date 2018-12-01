from entity.models import Merchant, User, Authority, Cashbox
from flask_bcrypt import Bcrypt
from slerp.app import app
from slerp.logger import logging
from slerp.validator import Key, Blank, ValidationException

from utils.api_constant import ErrorCode

log = logging.getLogger(__name__)
bcrypt = Bcrypt(app=app)


class MerchantService(object):
	def __init__(self):
		super(MerchantService, self).__init__()
	
	@Key(['store.name', 'store.phone_number', 'store.address', 'store.email', 'username', 'fullname', 'password'])
	def add_merchant(self, domain):
		password = domain['password']
		user = User(domain)
		user.username = domain['username']
		user.fullname = domain['password']
		user.phone_number = domain['store']['phone_number']
		user.hash_password = bcrypt.generate_password_hash(password, 10).replace(b'$2b$', b'$2a$')
		user.save()
		# Save into authority admin
		authority = Authority({'authority': 'ADMINISTRATOR', 'user_id': user.id})
		authority.save()
		merchant = Merchant(domain['store'])
		merchant.user_id = user.id
		merchant.save()
		merchant_dict = merchant.to_dict()
		merchant_dict['password'] = password
		merchant_dict['username'] = domain["username"]
		cashbox = Cashbox()
		cashbox.name = 'Kas'
		cashbox.total_amount = 0.0
		cashbox.merchant_id = merchant.id
		cashbox.save()
		cashbox = Cashbox()
		cashbox.name = 'Bank'
		cashbox.total_amount = 0.0
		cashbox.merchant_id = merchant.id
		cashbox.save()
		return {'payload': merchant_dict}
	
	@Blank(['username', 'password'])
	def find_merchant(self, domain):
		"""
		:param domain:input is the username or phone number registered
		:return dict:
		"""
		self.check_password(domain['username'], domain['password'])
		merchant = Merchant.query.join(User, User.id == Merchant.user_id) \
			.filter(User.username == domain['username']) \
			.first()
		merchant_dict = merchant.to_dict()
		merchant_dict['username'] = domain["username"]
		return {'payload': merchant_dict}
	
	@staticmethod
	def check_password(username, password):
		user = User.query.filter(User.username == username).first()
		
		if user is None:
			raise ValidationException(ErrorCode.USER_NOT_FOUND)
		
		if not bcrypt.check_password_hash(user.hash_password, password):
			raise ValidationException(ErrorCode.USER_WRONG_PASSWORD)
