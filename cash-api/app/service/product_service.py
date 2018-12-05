import base64
import os
from datetime import datetime

from entity.models import Product, ProductSellPrice, Stock, ProductPurchasePrice, Category, StockHistory, Discount
from slerp.app import app
from slerp.string_utils import is_not_blank
from slerp.logger import logging
from slerp.validator import Key, Number, ValidationException
from sqlalchemy import and_, func, between, or_

from api.document_api import document_service
from utils.api_constant import ErrorCode, StockRef

log = logging.getLogger(__name__)


class ProductService(object):
	def __init__(self):
		super(ProductService, self).__init__()
	
	@Key(['name', 'code', 'product_type', 'sell_price', 'purchase_price'])
	@Number(['merchant_id'])
	def add_product(self, domain):
		self.validate_product(domain)
		product = Product(domain)
		if 'image' in domain:
			document_dict = self.handle_add_image(domain['image'])['payload']
			product.document_id = document_dict['id']
		else:
			product.document_id = None
		product.save()
		
		sell_price = ProductSellPrice()
		sell_price.sell_price = domain["sell_price"]
		sell_price.name = "STANDARD"
		sell_price.product_id = product.id
		sell_price.save()
		
		purchase_price = ProductPurchasePrice()
		purchase_price.product_id = product.id
		purchase_price.purchase_price = domain['purchase_price']
		purchase_price.save()
		product_dict = product.to_dict()
		
		if 'qty' in domain:
			stock = Stock()
			stock.product_id = product.id
			stock.quantity = domain['qty']
			stock.save()
			
			stock_history = StockHistory()
			stock_history.quantity = stock.quantity
			stock_history.stock_id = stock.id
			stock_history.remark = StockRef.NEW_STOCK
			stock_history.ref_id = StockRef.IN
			stock_history.save()
			product_dict["qty"] = stock.quantity
		product_dict["sell_price"] = sell_price.sell_price
		return {'payload': product_dict}
	
	@staticmethod
	def handle_add_image(image):
		decoded = base64.b64decode(image)
		upload_folder = app.config['UPLOAD_FOLDER']
		folder = os.path.join(upload_folder, 'product')
		if not os.path.exists(folder):
			os.makedirs(folder)
		filename = '{}.png'.format(datetime.now().strftime('%Y_%m_%d_%H_%M_%S'))
		final_file_path = os.path.join(folder, filename)
		with open(final_file_path, 'wb') as f:
			f.write(decoded)
			document_dict = {
				'filename': filename,
				'folder': folder,
				'mimetype': 'image/png',
				'original_filename': filename
			}
			return document_service.add_document(document_dict)
	
	@Key(['query'])
	@Number(['page', 'size'])
	def get_product_list(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		order = domain['order'] if 'order' in domain and is_not_blank(domain['order']) else 'product_name asc'
		log.info('Order : %s', order)
		entities = (
			Product.id.label('product_id'),
			func.upper(Product.name).label('product_name'),
			Product.code.label('product_code'),
			func.coalesce(Product.document_id, -1).label("document_id"),
			Stock.quantity.label('stock'),
			ProductSellPrice.sell_price,
			Product.use_stock,
			ProductPurchasePrice.purchase_price,
			func.coalesce(Category.id, -1).label('category_id'),
			func.coalesce(Category.name, '').label('category_name'),
			Product.unit.label("unit"),
			func.count(Discount.id).label("count_discount")
		)

		now = datetime.now()

		product_q = Product.query.with_entities(*entities) \
			.outerjoin(Stock, Product.id == Stock.product_id) \
			.join(ProductSellPrice, and_(Product.id == ProductSellPrice.product_id, ProductSellPrice.name == 'STANDARD')) \
			.join(ProductPurchasePrice, and_(ProductPurchasePrice.product_id == Product.id, between(now, ProductPurchasePrice.start_at, ProductPurchasePrice.end_at)))\
			.outerjoin(Discount, Discount.product_id == Product.id) \
			.outerjoin(Category, Category.id == Product.category_id)

		if 'category_id' in domain and int(domain['category_id']) > 0:
			product_q = product_q.filter(Category.id == domain['category_id'])
		product_q = product_q.filter(Product.active == True)
		product_q = product_q.filter(or_(Product.name.ilike('%' + domain['query'] + '%'), Product.code.ilike('%' + domain['query'] + '%')))\
			.group_by(Product.id, ProductSellPrice.id, Category.id, ProductPurchasePrice.id, Stock.id) \
			.order_by(order).paginate(page, size, error_out=False)
		product_list = list(map(lambda x: x._asdict(), product_q.items))
		
		return {'payload': product_list, 'total': product_q.total, 'total_pages': product_q.pages}
	
	@staticmethod
	def validate_product(domain):
		# category = Category.query.get(domain['category_id'])
		# if category is None:
		# 	raise ValidationException(ErrorCode.REQUIRED_CATEGORY)
		product_count = Product.query.filter_by(code=domain['code']).count()
		if product_count > 0:
			raise ValidationException(ErrorCode.PRODUCT_CODE_EXIST)
		pass
	
	@Key(['id', 'category_id', 'name', 'code', 'product_type', 'sell_price', 'purchase_price'])
	def edit_product_by_id(self, domain):
		
		product = Product.query.filter_by(id=domain['id']).first()
		if 'image' in domain:
			document_dict = self.handle_add_image(domain['image'])['payload']
			product.document_id = document_dict['id']
		product.update(domain)
		sell_price = ProductSellPrice.query.filter_by(product_id=product.id).filter_by(name='STANDARD').first()
		sell_price.update({'sell_price': domain['sell_price']})
		now = datetime.now()
		purchase_price = ProductPurchasePrice.query.filter(and_(ProductPurchasePrice.product_id == product.id, between(now, ProductPurchasePrice.start_at, ProductPurchasePrice.end_at))).first()
		purchase_price.update({'purchase_price': domain['purchase_price']})
		stock = Stock.query.filter_by(product_id=product.id).first()
		if stock is not None:
			latest_stock = StockHistory.query.with_entities(func.sum(StockHistory.quantity).label("quantity")).filter(StockHistory.stock_id == stock.id).first()._asdict()
			if product.use_stock:
				if stock.quantity != domain['qty']:
					stock_history = StockHistory()
					stock_history.quantity = int(latest_stock['quantity']) * -1
					stock_history.stock_id = stock.id
					stock_history.remark = StockRef.CLEAR_STOCK
					stock_history.ref_id = StockRef.OUT
					
					stock_history.save()
					stock_history = StockHistory()
					stock_history.stock_id = stock.id
					stock_history.quantity = int(domain['qty'])
					stock_history.remark = StockRef.EDIT_STOCK
					stock_history.ref_id = StockRef.IN
					stock_history.save()
					stock.update({'quantity': domain['qty']})			
		else:
			if product.use_stock and 'qty' in domain:
				stock = Stock()
				stock.product_id = product.id
				stock.quantity = domain['qty']
				stock.save()
				stock_history = StockHistory()
				stock_history.quantity = stock.quantity
				stock_history.stock_id = stock.id
				stock_history.remark = StockRef.NEW_STOCK
				stock_history.ref_id = StockRef.IN
				stock_history.save()

		
		return {'payload': product.to_dict()}
	
	@Key(['code'])
	def find_product_by_code(self, domain):
		product = Product.query.filter_by(code=domain['code']).first()
		return {'payload': product.to_dict()}
	
	@Key(['code'])
	def edit_product_by_code(self, domain):
		product = Product.query.filter_by(code=domain['code']).first()
		product.update(domain)
		return {'payload': product.to_dict()}