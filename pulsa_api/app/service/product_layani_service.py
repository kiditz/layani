from slerp.validator import Key, Number, Blank
from slerp.logger import logging
from slerp.app import db

from entity.models import ProductLayani


log = logging.getLogger(__name__)


class ProductLayaniService(object):
	def __init__(self):
		super(ProductLayaniService, self).__init__()

	@Key(['name', 'code', 'nominal', 'provider_id'])
	def add_product_layani(self, domain):
		product_layani = ProductLayani(domain)
		product_layani.save()
		return {'payload': product_layani.to_dict()}
	
	@Blank(['code'])
	@Number(['page', 'size'])
	def get_product_layani_by_code(self, domain):
	
		page = int(domain['page'])
		size = int(domain['size'])
		product_layani_q = ProductLayani.query.filter_by(code=domain['code']).order_by(ProductLayani.nominal.asc()).paginate(page, size, error_out=False)
		product_layani_list = list(map(lambda x: x.to_dict(), product_layani_q.items))
		return {'payload': product_layani_list, 'total': product_layani_q.total, 'total_pages': product_layani_q.pages}