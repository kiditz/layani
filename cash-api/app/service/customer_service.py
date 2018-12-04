from slerp.validator import Key, Number, Blank
from slerp.logger import logging
from slerp.app import db

from entity.models import Customer


log = logging.getLogger(__name__)


class CustomerService(object):
	def __init__(self):
		super(CustomerService, self).__init__()
	
	@Blank(['merchant_id'])
	@Number(['page', 'size'])
	def get_customer_list(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		customer_q = Customer.query\
			.filter_by(merchant_id=domain['merchant_id'])\
			.filter(Customer.name.ilike('%' + domain['name'] + '%'))\
			.filter(Customer.active == True)\
			.order_by(Customer.name.asc()).paginate(page, size, error_out=False)
		customer_list = list(map(lambda x: x.to_dict(), customer_q.items))
		return {'payload': customer_list, 'total': customer_q.total, 'total_pages': customer_q.pages}
	
	@Key(['id'])
	def edit_customer_by_id(self, domain):
		customer = Customer.query.filter_by(id=domain['id']).first()
		if customer is None:
			customer = Customer()
			domain.pop('id')
		customer.update(domain)
		return {'payload': customer.to_dict()}