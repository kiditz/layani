from slerp.logger import logging

log = logging.getLogger(__name__)


class ReportService(object):
	def __init__(self):
		super(ReportService, self).__init__()
	
	# @Blank(['outlet_id'])
	# @Number(['page', 'size'])
	# def get_customer_list(self, domain):
	# 	page = int(domain['page'])
	# 	size = int(domain['size'])
	# 	customer_q = Customer.query\
	# 		.filter_by(outlet_id=domain['outlet_id'])\
	# 		.filter(Customer.name.ilike('%' + domain['name'] + '%'))\
	# 		.filter(Customer.active == True)\
	# 		.order_by(Customer.name.asc()).paginate(page, size, error_out=False)
	# 	customer_list = list(map(lambda x: x.to_dict(), customer_q.items))
	# 	return {'payload': customer_list, 'total': customer_q.total, 'total_pages': customer_q.pages}
	#
	# @Key(['id'])
	# def edit_customer_by_id(self, domain):
	# 	customer = Customer.query.filter_by(id=domain['id']).first()
	# 	if 'phone_number' in domain and is_not_empty(domain['phone_number']):
	# 		if not re.match(r'[\+]?[0-9.-]+', domain['phone_number']):
	# 			raise ValidationException(ErrorCode.INVALID_PHONE_NUMBER)
	# 	if 'email' in domain and is_not_empty(domain['email']):
	# 		if not re.match(r"^[A-Za-z0-9\.\+_-]+@[A-Za-z0-9\._-]+\.[a-zA-Z]*$", domain['email']):
	# 			raise ValidationException(ErrorCode.INVALID_EMAIL_ADDRESS)
	#
	# 	if customer is None:
	# 		customer = Customer()
	# 		domain.pop('id')
	# 	customer.update(domain)
	# 	return {'payload': customer.to_dict()}