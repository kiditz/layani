from datetime import datetime

from entity.models import ProductLayani, ProviderPrefix, ProductLayaniSellPrice, Provider, CategoryLayani, ProviderImage
from slerp.logger import logging
from slerp.validator import Key, Number, Blank
from sqlalchemy import and_, between

log = logging.getLogger(__name__)


class ProductLayaniService(object):
	def __init__(self):
		super(ProductLayaniService, self).__init__()

	@Key(['name', 'code', 'nominal', 'provider_id'])
	def add_product_layani(self, domain):
		product_layani = ProductLayani(domain)
		product_layani.save()
		return {'payload': product_layani.to_dict()}
	
	@Blank(['phone_number', 'category_id'])
	@Number(['page', 'size'])
	def get_product_pulsa_prefix(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		prefix = str(domain['phone_number'])[0:4]
		category_id = domain['category_id']
		provider_prefix = ProviderPrefix.query.filter(ProviderPrefix.prefix == prefix).first()
		now = datetime.now()
		entities = (
			ProductLayani.id,
			ProductLayani.code,
			ProductLayani.name,
			ProductLayani.nominal,
			ProductLayaniSellPrice.sell_price,
			Provider.name.label('provider'),
			
		)
		if provider_prefix is not None:
			product_layani_q = ProductLayani.query.with_entities(*entities)\
				.join(ProductLayaniSellPrice, and_(ProductLayaniSellPrice.product_id == ProductLayani.id,
			                                       ProductLayaniSellPrice.active == True,
			                                       between(now, ProductLayaniSellPrice.start_at, ProductLayaniSellPrice.end_at)))\
				.join(Provider, ProductLayani.provider_id == Provider.id)\
				.filter(ProductLayani.provider_id == provider_prefix.provider_id)\
				.filter(ProductLayani.category_id == category_id) \
				.filter(ProductLayani.active == True) \
				.order_by(ProductLayani.nominal.asc()).paginate(page, size, error_out=False)
		else:
			product_layani_q = ProductLayani.query.with_entities(*entities) \
				.join(ProductLayaniSellPrice, and_(ProductLayaniSellPrice.product_id == ProductLayani.id,
			                                       ProductLayaniSellPrice.active == True,
			                                       between(now, ProductLayaniSellPrice.start_at,
			                                               ProductLayaniSellPrice.end_at))) \
				.filter(ProductLayani.category_id == category_id) \
				.filter(ProductLayani.active == True) \
				.order_by(ProductLayani.nominal.asc()).paginate(page, size, error_out=False)
			
		product_layani_list = list(map(lambda x: x._asdict(), product_layani_q.items))
		return {'payload': product_layani_list, 'total': product_layani_q.total, 'total_pages': product_layani_q.pages}
	
	def get_category_layani(self):
		category_layani_q = CategoryLayani.query.order_by(CategoryLayani.id.asc()).all()
		category_layani_list = list(map(lambda x: x.to_dict(), category_layani_q))
		return {'payload': category_layani_list}
	
	@Key(['category_id'])
	def get_provider(self, domain):
		provider_layani_q = Provider.query\
			.join(ProductLayani, ProductLayani.provider_id == Provider.id)\
			.join(CategoryLayani, ProductLayani.category_id == CategoryLayani.id)\
			.filter(CategoryLayani.id == domain['category_id'])\
			.group_by(Provider.id) \
			.order_by(Provider.name) \
			.all()
		provider_layani_list = list(map(lambda x: x.to_dict(), provider_layani_q))
		return {'payload': provider_layani_list}
	
	@Number(['provider_id', 'page', 'size'])
	def get_product_by_provider(self, domain):
		provider_id = domain['provider_id']
		page = int(domain['page'])
		size = int(domain['size'])
		now = datetime.now()
		entities = (
			ProductLayani.id,
			ProductLayani.code,
			ProductLayani.name,
			ProductLayani.nominal,
			ProductLayaniSellPrice.sell_price,
			Provider.name.label('provider'),
			Provider.id.label('provider_id')
		)
		product_layani_q = ProductLayani.query.with_entities(*entities) \
			.join(ProductLayaniSellPrice, and_(ProductLayaniSellPrice.product_id == ProductLayani.id,
		                                       ProductLayaniSellPrice.active == True,
		                                       between(now, ProductLayaniSellPrice.start_at, ProductLayaniSellPrice.end_at)))\
			.join(Provider, Provider.id == ProductLayani.provider_id)\
			.filter(ProductLayani.provider_id == provider_id) \
			.filter(ProductLayani.active == True) \
			.order_by(ProductLayani.id.asc(), ProductLayani.nominal.asc()).paginate(page, size, error_out=False)
		product_layani_list = list(map(lambda x: x._asdict(), product_layani_q.items))
		return {'payload': product_layani_list, 'total': product_layani_q.total, 'total_pages': product_layani_q.pages}