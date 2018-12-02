from slerp.validator import Key, Number, Blank
from slerp.logger import logging
from slerp.app import db

from entity.models import Category


log = logging.getLogger(__name__)


class CategoryService(object):
	def __init__(self):
		super(CategoryService, self).__init__()

	@Key(['merchant_id', 'name'])
	def add_category(self, domain):
		category = Category(domain)
		category.save()
		return {'payload': category.to_dict()}
		
	@Key(['merchant_id', 'name'])
	def get_category_list(self, domain):
		category_q = Category.query\
			.with_entities(Category.name.label('category_name'), Category.id.label('category_id'))\
			.filter_by(merchant_id=domain['merchant_id'])\
			.filter(Category.name.ilike('%' + domain['name'] + '%'))\
			.order_by(Category.name.asc())
		if 'page' in domain and 'size' in domain:
			page = int(domain['page'])
			size = int(domain['size'])
			category_list = list(map(lambda x: x._asdict(), category_q.paginate(page, size, error_out=False).items))
		else:
			category_list = list(map(lambda x: x._asdict(), category_q.all()))
		return {'payload': category_list}
	
	@Number(['id'])
	def find_category_by_id(self, domain):
		category = Category.query.filter_by(id=domain['id']).first()
		return {'payload': category.to_dict()}
	
	@Key(['id'])
	def edit_category_by_id(self, domain):
		category = Category.query.filter_by(id=domain['id']).first()
		category.update(domain)
		return {'payload': category.to_dict()}
	
	@Key(['id'])
	def delete_category_by_id(self, domain):
		category = Category.query.filter_by(id=domain['id']).first()
		category.delete()
		return {'payload': {'success':True}}