from entity.models import Stock, StockHistory, ProductPurchasePrice
from slerp.logger import logging
from slerp.validator import Key
from slerp.string_utils import is_not_empty
from sqlalchemy import and_, between
from datetime import datetime
from utils.api_constant import StockRef

log = logging.getLogger(__name__)


class StockService(object):
	def __init__(self):
		super(StockService, self).__init__()

	@Key(['product_id', 'quantity', 'start_date', 'description'])
	def add_stock(self, domain):
		now = datetime.now()
		log.info('Start Date ', domain['start_date'])
		start_date = datetime.strptime(domain['start_date'], '%Y-%m-%d %H:%M:%S')
		if 'purchase_price' in domain:
			update_purch_price = ProductPurchasePrice.query.filter(
				ProductPurchasePrice.product_id == domain['product_id']).filter(
				between(now, ProductPurchasePrice.start_at, ProductPurchasePrice.end_at)).first()
			update_purch_price.end_at = start_date
			update_purch_price.save()
			
			purchase_price = ProductPurchasePrice()
			purchase_price.product_id = domain['product_id']
			purchase_price.start_at = start_date
			purchase_price.purchase_price = domain['purchase_price']
			purchase_price.save()
		
		stock = Stock.query.filter_by(product_id=domain['product_id']).first()
		stock.quantity += domain['quantity']
		stock.save()
		
		stock_history = StockHistory()
		stock_history.stock_id = stock.id
		stock_history.ref_id = StockRef.IN
		stock_history.quantity = domain['quantity']
		stock_history.remark = domain['description'] if is_not_empty(domain['description']) else StockRef.ADD_STOCK
		stock_history.created_at = start_date
		stock_history.save()
		
		stock_dict = stock.to_dict()
		
		if 'purchase_price' in domain:
			selected_purchase_price = ProductPurchasePrice.query.filter(
				ProductPurchasePrice.product_id == domain['product_id']).filter(
				between(now, ProductPurchasePrice.start_at, ProductPurchasePrice.end_at)).first()
			stock_dict['purchase_price'] = selected_purchase_price.purchase_price
			
		return {'payload': stock_dict}