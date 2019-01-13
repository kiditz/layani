from datetime import datetime

from flask_migrate import Migrate, MigrateCommand
from flask_script import Manager
from slerp.app import app
from slerp.app import db
from slerp.entity import Entity

__author__ = "Rifky Aditya Bastara"

client_grant = db.Table(
	'co_client_grant', db.metadata,
	db.Column('client_id', db.String(255), nullable=False),
	db.Column('grant_name', db.String(255), nullable=False)
)

client_redirect = db.Table(
	'co_client_redirect', db.metadata,
	db.Column('client_id', db.String(255), nullable=False),
	db.Column('redirect_uri', db.String(255))
)

client_scope = db.Table(
	'co_client_scope', db.metadata,
	db.Column('client_id', db.String(255), nullable=False),
	db.Column('scope', db.String(255))
)


class User(db.Model, Entity):
	__tablename__ = 'co_user'
	id = db.Column(db.BigInteger, db.Sequence('co_user_id_seq'), primary_key=True)	
	phone_number = db.Column(db.String(20), nullable=False, index=True)
	username = db.Column(db.String(60), nullable=False, index=True)
	fullname = db.Column(db.String(100), nullable=False, index=True)
	business_name = db.Column(db.Text, nullable=False, index=True, server_default=' ', default=' ')
	hash_password = db.Column(db.LargeBinary(60), nullable=False)	
	enabled = db.Column(db.Boolean, nullable=False, default=False)
	account_non_expired = db.Column(db.Boolean, nullable=False, default=False)
	account_non_locked = db.Column(db.Boolean, nullable=False, default=False)
	credentials_non_expired = db.Column(db.Boolean, nullable=False, default=False)	
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	__json_hidden__ = ['hash_password', 'enabled', 'account_non_expired', 'account_non_locked', 'credentials_non_expired']
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)	


class Client(db.Model, Entity):
	__tablename__ = 'co_client'
	id = db.Column(db.BigInteger, db.Sequence('co_client_id_seq'), primary_key=True)
	client_id = db.Column(db.String(255), unique=True)
	client_secret = db.Column(db.String(255))
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class NotificationToken(db.Model, Entity):
	__tablename__ = 'f_notification_token'
	client_id = db.Column(db.String(255), unique=True, primary_key=True)
	user_id = db.Column(db.ForeignKey(u'co_user.id'), index=True)
	token = db.Column(db.Text, nullable=False)
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class Notification(db.Model, Entity):
	__tablename__ = 'f_notification'
	id = db.Column(db.BigInteger, db.Sequence('f_notification_id_seq'), primary_key=True)
	user_id = db.Column(db.ForeignKey(u'co_user.id'), index=True)
	title = db.Column(db.String(100), nullable=False)
	body = db.Column(db.Text, nullable=False)
	data = db.Column(db.Text, nullable=False)
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class Document(db.Model, Entity):
	__tablename__ = 'co_document'
	id = db.Column(db.BigInteger, db.Sequence('co_document_id_seq'), primary_key=True)
	filename = db.Column(db.Text, nullable=False)
	original_filename = db.Column(db.Text, nullable=False, server_default='')
	thumbnails = db.Column(db.Text)
	mimetype = db.Column(db.String(60), nullable=False)
	folder = db.Column(db.Text, nullable=False)
	secure = db.Column(db.Boolean, nullable=False, server_default='t')
	user_id = db.Column(db.ForeignKey(u'co_user.id'), index=True)

	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)	
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class Authority(db.Model, Entity):
	__tablename__ = 'co_authority'
	id = db.Column(db.BigInteger, db.Sequence('co_authority_id_seq'), primary_key=True)
	authority = db.Column(db.String(255))
	user_id = db.Column(db.ForeignKey(u'co_user.id'), nullable=False)
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class Outlet(db.Model, Entity):
	__tablename__ = 'co_outlet'
	id = db.Column(db.BigInteger, db.Sequence('co_outlet_id_seq'),primary_key=True)
	name = db.Column(db.Text, nullable=False, server_default='', index=True)
	phone_number = db.Column(db.String(20), nullable=False, server_default='', unique=True)
	address = db.Column(db.Text, nullable=False, server_default='')
	email = db.Column(db.String(255), nullable=False, server_default='')
	user_id = db.Column(db.ForeignKey(u'co_user.id'), nullable=False, index=True)
	document_id = db.Column(db.ForeignKey(u'co_document.id'))
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class Category(db.Model, Entity):
	__tablename__ = 'co_category'
	id = db.Column(db.BigInteger, primary_key=True)
	outlet_id = db.Column(db.ForeignKey(u'co_outlet.id'), nullable=False)
	name = db.Column(db.Text, nullable=False, server_default='', index=True)		
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class Product(db.Model, Entity):
	__tablename__ = 'co_product'
	id = db.Column(db.BigInteger, primary_key=True)
	category_id = db.Column(db.ForeignKey(u'co_category.id'))
	name = db.Column(db.Text, nullable=False, server_default='-', index=True)		
	code = db.Column(db.String(60), nullable=False, server_default='-', index=True)	
	product_type = db.Column(db.String(60), nullable=False, server_default='')
	document_id = db.Column(db.ForeignKey(u'co_document.id'))
	description = db.Column(db.Text, nullable=False, server_default='')	
	active = db.Column(db.Boolean, nullable=False, server_default='t', default=True)
	unit = db.Column(db.String(10), nullable=False, server_default='pcs')
	use_stock = db.Column(db.Boolean, nullable=False, server_default='t')
	outlet_id = db.Column(db.ForeignKey(u'co_outlet.id'))	
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)	
	parent_id = db.Column(db.BigInteger, default=-1)
	__table_args__ = (db.UniqueConstraint('code', 'outlet_id', name='co_product_code_outlet_id_key'),)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)		


class ProductSellPrice(db.Model, Entity):
	__tablename__ = 'co_product_sell_price'
	id = db.Column(db.BigInteger, primary_key=True)
	product_id = db.Column(db.ForeignKey(u'co_product.id'), nullable=False)
	name = db.Column(db.Text, nullable=False, server_default='-')		
	sell_price = db.Column(db.Numeric, nullable=False, server_default='0')	
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)	

	def __init__(self, obj=None):
		Entity.__init__(self, obj)		


class ProductPurchasePrice(db.Model, Entity):
	__tablename__ = 'co_product_purchase_price'	
	id = db.Column(db.BigInteger, primary_key=True)
	product_id = db.Column(db.ForeignKey(u'co_product.id'), nullable=False)		
	purchase_price = db.Column(db.Numeric, nullable=False, server_default='0')		
	start_at = db.Column(db.DateTime(timezone=False),  server_default='now()')
	end_at = db.Column(db.DateTime(timezone=False), default=datetime(2101, 1, 1, 0, 0, 0))
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)	

	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class Stock(db.Model, Entity):
	__tablename__ = 'co_product_stock'	
	id = db.Column(db.BigInteger, primary_key=True)
	product_id = db.Column(db.ForeignKey(u'co_product.id'), nullable=False)	
	quantity = db.Column(db.BigInteger, nullable=False, server_default='0')
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)	

	def __init__(self, obj=None):
		Entity.__init__(self, obj)	
		

class StockHistory(db.Model, Entity):
	__tablename__ = 'co_product_stock_history'
	id = db.Column(db.BigInteger, primary_key=True)
	stock_id = db.Column(db.ForeignKey(u'co_product_stock.id'), nullable=False)
	quantity = db.Column(db.BigInteger, nullable=False, server_default='0')	
	purchase_price = db.Column(db.Numeric, nullable=False, server_default='0')		
	ref_id = db.Column(db.BigInteger, nullable=False)
	remark = db.Column(db.Text, nullable=False, default='')
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class Discount(db.Model, Entity):
	__tablename__ = 'co_discount'
	id = db.Column(db.BigInteger, primary_key=True)
	product_id = db.Column(db.ForeignKey(u'co_product.id'))	
	name = db.Column(db.Text)
	method = db.Column(db.BigInteger)	
	amount = db.Column(db.Numeric, nullable=False, server_default='0', default=0)	
	quantity = db.Column(db.BigInteger, nullable=False, server_default='0', default=0)	
	bill_amount = db.Column(db.Numeric, nullable=False, server_default='0', default=0)
	free_product_id = db.Column(db.ForeignKey(u'co_product.id'))
	discount_type = db.Column(db.String(30), nullable=False, server_default='PERCENTAGE', default='PERCENTAGE')	
	day_of_week = db.Column(db.String(60))	
	start_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	end_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	outlet_id = db.Column(db.ForeignKey(u'co_outlet.id'), nullable=False)	
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class Order(db.Model, Entity):
	__tablename__ = 'co_order'
	id = db.Column(db.BigInteger, primary_key=True)	
	customer_id = db.Column(db.ForeignKey(u'co_customer.id'))		
	description = db.Column(db.Text, nullable=False, server_default=' ', default=' ')
	outlet_id = db.Column(db.ForeignKey(u'co_outlet.id'), nullable=False)
	order_code = db.Column(db.Text, index=True)
	total_amount = db.Column(db.Numeric, nullable=False, server_default='0', default=0)
	discount_amount = db.Column(db.Numeric, nullable=False, server_default='0', default=0)
	discount_name = db.Column(db.Text)
	total_payment = db.Column(db.Numeric, nullable=False, server_default='0', default=0)		
	payment_method = db.Column(db.String(20), nullable=False, server_default='-', default='-')
	cashback = db.Column(db.Numeric, nullable=False, server_default='0', default=0)	
	status=db.Column(db.String(1), nullable=False, server_default='I', default='I', index=True)
	order_at = db.Column(db.DateTime(timezone=False), index=True, default=datetime.now)
	profit = db.Column(db.Numeric, nullable=False, server_default='0', default=0)	
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	user_id = db.Column(db.ForeignKey(u'co_user.id'), index=True)	
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)		


class AccountReceiveable(db.Model, Entity):
	__tablename__ = 'co_account_receive_able'	
	order_id = db.Column(db.ForeignKey(u'co_order.id'), nullable=False, primary_key=True)
	total_credit = db.Column(db.Numeric, nullable=False, server_default='0', default=0)	
	payment_amount = db.Column(db.Numeric, nullable=False, server_default='0', default=0)	
	receiveable_date = db.Column(db.DateTime(timezone=False), default=datetime.now)	
	outlet_id = db.Column(db.ForeignKey(u'co_outlet.id'), nullable=False)	
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class OrderItem(db.Model, Entity):
	__tablename__ = 'co_order_item'
	id = db.Column(db.BigInteger, primary_key=True)	
	order_id = db.Column(db.ForeignKey(u'co_order.id'), nullable=False)
	product_id = db.Column(db.ForeignKey(u'co_product.id'), nullable=False)	
	discount_amount = db.Column(db.Numeric, nullable=False, server_default='0', default=0)	
	discount_name = db.Column(db.Text)
	qty = db.Column(db.BigInteger, nullable=False, server_default='0', default=0)		
	sub_total = db.Column(db.Numeric, nullable=False, server_default='0', default=0)		
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	sell_price_id = db.Column(db.ForeignKey(u'co_product_sell_price.id'))
	sell_price = db.Column(db.Numeric, nullable=False, server_default='0', default=0)
	purchase_price_id = db.Column(db.ForeignKey(u'co_product_purchase_price.id'), nullable=False)	
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class Customer(db.Model, Entity):
	__tablename__ = 'co_customer'
	id = db.Column(db.BigInteger, primary_key=True)				
	name = db.Column(db.Text, nullable=False, server_default='-', default='-', index=True)
	phone_number = db.Column(db.String(30), nullable=False, server_default='-')	
	email = db.Column(db.String(255), nullable=False, server_default='-')
	outlet_id = db.Column(db.ForeignKey(u'co_outlet.id'), nullable=False)
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	active = db.Column(db.Boolean, nullable=False, server_default='t', default=True)

	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class CashboxSummary(db.Model, Entity):
	__tablename__ = 'co_cash_box_summary'
	id = db.Column(db.BigInteger, primary_key=True)	
	start_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	end_at = db.Column(db.DateTime(timezone=False))
	status = db.Column(db.String(1), nullable=False, server_default='O', default='O')
	pending = db.Column(db.BigInteger, nullable=False, server_default='0', default=0)
	cash = db.Column(db.Numeric, nullable=False, server_default='0', default=0)
	card = db.Column(db.Numeric, nullable=False, server_default='0', default=0)
	transaction = db.Column(db.Numeric, nullable=False, server_default='0', default=0)	
	refund = db.Column(db.Numeric, nullable=False, server_default='0', default=0)	
	difference = db.Column(db.Numeric, nullable=False, server_default='0', default=0)	
	user_id = db.Column(db.ForeignKey(u'co_user.id'), index=True)	
	outlet_id = db.Column(db.ForeignKey(u'co_outlet.id'), nullable=False)
	cash_in = db.Column(db.Numeric, nullable=False, server_default='0', default=0)
	cash_out = db.Column(db.Numeric, nullable=False, server_default='0', default=0)	
	sales = db.Column(db.Numeric, nullable=False, server_default='0', default=0)	
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class CashboxHistory(db.Model, Entity):
	__tablename__ = 'co_cash_box_history'
	id = db.Column(db.BigInteger, primary_key=True)
	cash_box_summary_id = db.Column(db.ForeignKey(u'co_cash_box_summary.id'), nullable=False)
	amount = db.Column(db.Numeric, nullable=False, server_default='0')		
	remark = db.Column(db.Text, nullable=False, default='')
	ref_id = db.Column(db.BigInteger, index=True, nullable=False, server_default='0', default='0')
	payment_method = db.Column(db.String(10), nullable=False, server_default='-', default='-')
	transaction_date = db.Column(db.DateTime(timezone=False), default=datetime.now)	
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class Deposit(db.Model, Entity):
	__tablename__ = 'ps_deposit'
	id = db.Column(db.BigInteger, db.Sequence('ps_deposit_id_seq'), primary_key=True)		
	outlet_id = db.Column(db.ForeignKey(u'co_outlet.id'), nullable=False)
	amount = db.Column(db.Numeric(14, 2), nullable=False, server_default='0')
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class DepositLog(db.Model, Entity):
	__tablename__ = 'ps_deposit_log'
	id = db.Column(db.BigInteger, db.Sequence('ps_deposit_log_id_seq'), primary_key=True)		
	outlet_id = db.Column(db.ForeignKey(u'co_outlet.id'), nullable=False, index=True)
	deposit_id = db.Column(db.ForeignKey(u'ps_deposit.id'), nullable=False, index=True)
	balance_amount = db.Column(db.Numeric(14, 2), nullable=False, server_default='0')
	remark = db.Column(db.Text, nullable=False, server_default='-', index=True)
	ref_id = db.Column(db.BigInteger, index=True, nullable=False, server_default='0', default='0')
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)						


class Provider(db.Model, Entity):
	__tablename__ = 'ps_provider'
	id = db.Column(db.BigInteger, db.Sequence('ps_provider_id_seq'), primary_key=True)		
	name = db.Column(db.String(255), nullable=False)	
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class ProviderPrefix(db.Model, Entity):
	__tablename__ = 'ps_provider_prefix'
	id = db.Column(db.BigInteger, db.Sequence('ps_provider_prefix_id_seq'), primary_key=True)		
	provider_id = db.Column(db.ForeignKey(u'ps_provider.id'), nullable=False, index=True)
	prefix = db.Column(db.String(5), nullable=False)
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class CategoryLayani(db.Model, Entity):
	__tablename__ = 'ps_category'
	id = db.Column(db.BigInteger,db.Sequence('ps_category_id_seq'), primary_key=True)
	name = db.Column(db.Text, nullable=False, server_default='', index=True)	
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)	
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class ProductLayani(db.Model, Entity):
	__tablename__ = 'ps_product'
	id = db.Column(db.BigInteger, db.Sequence('ps_product_id_seq'), primary_key=True)			
	name = db.Column(db.Text, nullable=False, server_default='-', index=True)		
	code = db.Column(db.String(60), nullable=False, server_default='-', index=True, unique=True)			
	nominal = db.Column(db.Numeric(14, 2), nullable=False, server_default='0.0', default=0.0)	
	active = db.Column(db.Boolean, nullable=False, default=True, server_default='t')
	provider_id = db.Column(db.ForeignKey(u'ps_provider.id'), index=True, nullable=False)
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)	
	category_id = db.Column(db.ForeignKey(u'ps_category.id'), index=True)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class Partner(db.Model, Entity):
	__tablename__ = 'ps_partner'
	id = db.Column(db.BigInteger, db.Sequence('ps_partner_id_seq'), primary_key=True)			
	name = db.Column(db.Text, nullable=False, server_default='-', index=True)
	code = db.Column(db.String(60), nullable=False, server_default='-', index=True)
	url = db.Column(db.Text, nullable=False, server_default='-')
	active = db.Column(db.Boolean, nullable=False, default=True, server_default='t')
	partner_type = db.Column(db.String(60), nullable=False, default='DEPOSIT', server_default='DEPOSIT')		
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)								


class PartnerProduct(db.Model, Entity):
	__tablename__ = 'ps_partner_product'
	id = db.Column(db.BigInteger, db.Sequence('ps__partner_product_id_seq'), primary_key=True)				
	name = db.Column(db.Text, nullable=False, server_default='-', index=True)		
	code = db.Column(db.String(60), nullable=False, server_default='-', index=True)
	active = db.Column(db.Boolean, nullable=False, default=True, server_default='t')			
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	partner_id = db.Column(db.ForeignKey(u'ps_partner.id'), index=True)	
	__table_args__ = (db.UniqueConstraint('code', 'partner_id', name='ps_partner_product_unique_key'),)

	def __init__(self, obj=None):
		Entity.__init__(self, obj)								


class ProductLayaniSellPrice(db.Model, Entity):
	__tablename__ = 'ps_product_sell_price'
	id = db.Column(db.BigInteger, db.Sequence('ps_product_sell_price_id_seq'), primary_key=True)		
	product_id = db.Column(db.ForeignKey(u'ps_product.id'), index=True, nullable=False)	
	sell_price = db.Column(db.Numeric, nullable=False, server_default='0')	
	flg_tax = db.Column(db.String(1), nullable=False, server_default=' ')			
	tax_percentage = db.Column(db.Numeric(14, 2), nullable=False, server_default='0.0', default=0.0)		
	start_at = db.Column(db.DateTime(timezone=False),  server_default='now()')
	end_at = db.Column(db.DateTime(timezone=False), default=datetime(2101, 1, 1, 0, 0, 0))
	active = db.Column(db.Boolean, nullable=False, server_default='t', default=True)
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class PartnerProductPurchasePrice(db.Model, Entity):
	__tablename__ = 'ps_partner_product_purchase_price'
	id = db.Column(db.BigInteger, db.Sequence('ps_partner_product_purchase_price_id_seq'), primary_key=True)		
	partner_product_id = db.Column(db.ForeignKey(u'ps_partner_product.id'), index=True, nullable=False)	
	purchase_price = db.Column(db.Numeric, nullable=False, server_default='0')	
	flg_tax = db.Column(db.String(1), nullable=False, server_default=' ')			
	tax_percentage = db.Column(db.Numeric(14, 2), nullable=False, server_default='0.0', default=0.0)		
	start_at = db.Column(db.DateTime(timezone=False),  server_default='now()')
	end_at = db.Column(db.DateTime(timezone=False), default=datetime(2101, 1, 1, 0, 0, 0))
	active = db.Column(db.Boolean, nullable=False, server_default='t', default=True)
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class PartnerDepositBalance(db.Model, Entity):
	__tablename__ = 'ps_partner_deposit'
	id = db.Column(db.BigInteger, db.Sequence('ps_partner_deposit_id_seq'), primary_key=True)		
	partner_id = db.Column(db.ForeignKey(u'ps_partner.id'), index=True, nullable=False)	
	balance_amount = db.Column(db.Numeric, nullable=False, server_default='0')	
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)				


class PartnerDepositBalanceLog(db.Model, Entity):
	__tablename__ = 'ps_partner_deposit_log'
	id = db.Column(db.BigInteger, db.Sequence('ps_partner_deposit_log_id_seq'), primary_key=True)		
	partner_deposit_id = db.Column(db.ForeignKey(u'ps_partner_deposit.id'), index=True, nullable=False)	
	ref_id = db.Column(db.BigInteger, nullable=False)
	balance_amount = db.Column(db.Numeric, nullable=False, server_default='0')	
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)						


class OrderPulsa(db.Model, Entity):
	__tablename__ = 'ps_order'
	id = db.Column(db.BigInteger, db.Sequence('ps_order_id_seq'), primary_key=True)		
	msisdn = db.Column(db.String(30), nullable=False)
	customer_id = db.Column(db.ForeignKey(u'co_customer.id'))		
	outlet_id = db.Column(db.ForeignKey(u'co_outlet.id'), nullable=False)
	sales_type = db.Column(db.String(20), nullable=False, default='TRX')
	product_id = db.Column(db.ForeignKey(u'ps_product.id'), nullable=False)
	partner_product_id = db.Column(db.ForeignKey(u'ps_partner_product.id'))	
	reqid = db.Column(db.Text, index=True, unique=True)	
	sell_price = db.Column(db.Numeric, nullable=False, server_default='0', default=0)		
	purchase_price = db.Column(db.Numeric, nullable=False, server_default='0', default=0)		
	status = db.Column(db.String(1), nullable=False, server_default='I', default='I', index=True)
	sn = db.Column(db.Text, index=True)
	order_at = db.Column(db.DateTime(timezone=False), index=True, default=datetime.now)	
	remark = db.Column(db.Text, nullable=False, server_default=' ', default=' ')
	user_id = db.Column(db.ForeignKey(u'co_user.id'), index=True)
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)		
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)		


class OrderPulsaApi(db.Model, Entity):
	__tablename__ = 'ps_order_api'	
	id = db.Column(db.BigInteger, db.Sequence('ps_order_detail_id_seq'), primary_key=True)		
	order_id = db.Column(db.ForeignKey(u'ps_order.id'), nullable=False)
	request = db.Column(db.Text, nullable=False, server_default=' ', default=' ')
	response = db.Column(db.Text, nullable=False, server_default=' ', default=' ')
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)		
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)		


class OrderPayload(db.Model, Entity):
	__tablename__ = 'ps_order_payload'	
	id = db.Column(db.BigInteger, db.Sequence('ps_order_detail_id_seq'), primary_key=True)		
	order_id = db.Column(db.ForeignKey(u'ps_order.id'), nullable=False)
	payload = db.Column(db.Text, nullable=False, server_default=' ', default=' ')	
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)		

	def __init__(self, obj=None):
		Entity.__init__(self, obj)		


class OrderPulsaMessageMapping(db.Model, Entity):
	__tablename__ = 'ps_order_message_mapping'	
	id = db.Column(db.BigInteger, db.Sequence('ps_order_detail_id_seq'), primary_key=True)			
	partner_id = db.Column(db.ForeignKey(u'ps_partner.id'), index=True, nullable=False)	
	partner_message = db.Column(db.Text, nullable=False, server_default=' ', default=' ')
	layani_message = db.Column(db.Text, nullable=False, server_default=' ', default=' ')
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)		

	def __init__(self, obj=None):
		Entity.__init__(self, obj)				
	
		
product_prefix = db.Table('ps_product_prefix',
    db.Column('provider_prefix_id', db.Integer, db.ForeignKey('ps_provider_prefix.id')),
    db.Column('product_id', db.Integer, db.ForeignKey('ps_product.id'))
)


partner_product_has_product = db.Table('ps_partner_product_has_product',
    db.Column('partner_product_id', db.BigInteger, db.ForeignKey('ps_partner_product.id')),
    db.Column('product_id', db.BigInteger, db.ForeignKey('ps_product.id'))
)

if __name__ == '__main__':
	migrate = Migrate(app, db)
	manager = Manager(app)
	manager.add_command('db', MigrateCommand)
	manager.run()
