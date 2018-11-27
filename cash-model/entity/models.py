from datetime import datetime

from flask_migrate import Migrate, MigrateCommand
from flask_script import Manager
from slerp.app import app
from slerp.app import db
from slerp.entity import Entity
import os

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
	phone_number = db.Column(db.String(20), nullable=False)
	username = db.Column(db.String(60), nullable=False)
	fullname = db.Column(db.String(100), nullable=False)
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
	id = db.Column(db.BigInteger, db.Sequence('co_authority_id_seq'),
	               primary_key=True)
	authority = db.Column(db.String(255))
	user_id = db.Column(db.ForeignKey(u'co_user.id'), nullable=False)
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class Merchant(db.Model, Entity):
	__tablename__ = 'co_merchant'
	id = db.Column(db.BigInteger, db.Sequence('co_merchant_id_seq'),primary_key=True)
	name = db.Column(db.Text, nullable=False, server_default='')
	phone_number = db.Column(db.String(20), nullable=False, server_default='', unique=True)
	address = db.Column(db.Text, nullable=False, server_default='')
	email = db.Column(db.String(255), nullable=False, server_default='')
	user_id = db.Column(db.ForeignKey(u'co_user.id'), nullable=False)
	document_id = db.Column(db.ForeignKey(u'co_document.id'))
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)		

class MerchantSetting(db.Model, Entity):
	__tablename__ = 'co_merchant_setting'
	merchant_id = db.Column(db.BigInteger, primary_key=True)
	notify_when_stock_less_than = db.Column(db.BigInteger, nullable=False, server_default='5')
	receipt_footer = db.Column(db.Text, nullable=False, server_default='')
	generate_qr_code_per_order=db.Column(db.Text, nullable=False, server_default='')	
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)		


class Category(db.Model, Entity):
	__tablename__ = 'co_category'
	id = db.Column(db.BigInteger, primary_key=True)
	merchant_id = db.Column(db.ForeignKey(u'co_merchant.id'), nullable=False)
	name = db.Column(db.Text, nullable=False, server_default='')		
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class Product(db.Model, Entity):
	__tablename__ = 'co_product'
	id = db.Column(db.BigInteger, primary_key=True)
	category_id = db.Column(db.ForeignKey(u'co_category.id'))
	name = db.Column(db.Text, nullable=False, server_default='-')		
	code = db.Column(db.String(60), nullable=False, server_default='-')	
	product_type = db.Column(db.String(60), nullable=False, server_default='')
	document_id = db.Column(db.ForeignKey(u'co_document.id'))
	description = db.Column(db.Text, nullable=False, server_default='')	
	unit = db.Column(db.String(10), nullable=False, server_default='pcs')
	use_stock = db.Column(db.Boolean, nullable=False, server_default='t')
	merchant_id = db.Column(db.ForeignKey(u'co_merchant.id'))	
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)	
	__table_args__ = (db.UniqueConstraint('code', 'merchant_id', name='co_product_code_merchant_id_key'),)
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
	ref_id = db.Column(db.BigInteger, nullable=False)
	remark = db.Column(db.Text, nullable=False, default='')
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class Discount(db.Model, Entity):
	__tablename__ = 'co_discount'
	id = db.Column(db.BigInteger, primary_key=True)
	product_id = db.Column(db.ForeignKey(u'co_product.id'), nullable=False)
	discount = db.Column(db.BigInteger, nullable=False, server_default='0')	
	discount_when = db.Column(db.BigInteger, nullable=False)	
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class Order(db.Model, Entity):
	__tablename__ = 'co_order'
	id = db.Column(db.BigInteger, primary_key=True)	
	customer_id = db.Column(db.ForeignKey(u'co_customer.id'))
	cash_box_id = db.Column(db.ForeignKey(u'co_cash_box.id'), nullable=False)
	merchant_id = db.Column(db.ForeignKey(u'co_merchant.id'), nullable=False)
	order_code = db.Column(db.Text, unique=True)
	total_amount = db.Column(db.Numeric, nullable=False, server_default='0', default=0)		
	total_payment = db.Column(db.Numeric, nullable=False, server_default='0', default=0)		
	payment_method = db.Column(db.String(20), nullable=False, server_default='-', default='-')
	cashback = db.Column(db.Numeric, nullable=False, server_default='0', default=0)	
	status=db.Column(db.String(1), nullable=False, server_default='I', default='I')
	order_at = db.Column(db.DateTime(timezone=False), index=True, default=datetime.now)
	profit = db.Column(db.Numeric, nullable=False, server_default='0', default=0)	
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)		


class AccountReceiveable(db.Model, Entity):
	__tablename__ = 'co_account_receive_able'	
	order_id = db.Column(db.ForeignKey(u'co_order.id'), nullable=False, primary_key=True)
	total_credit = db.Column(db.Numeric, nullable=False, server_default='0', default=0)	
	payment_amount = db.Column(db.Numeric, nullable=False, server_default='0', default=0)	
	receiveable_date = db.Column(db.DateTime(timezone=False), default=datetime.now)	
	merchant_id = db.Column(db.ForeignKey(u'co_merchant.id'), nullable=False)	
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)



class OrderItem(db.Model, Entity):
	__tablename__ = 'co_order_item'
	id = db.Column(db.BigInteger, primary_key=True)	
	order_id = db.Column(db.ForeignKey(u'co_order.id'), nullable=False)
	product_id = db.Column(db.ForeignKey(u'co_product.id'), nullable=False)
	qty = db.Column(db.BigInteger, nullable=False, server_default='0', default=0)		
	sub_total = db.Column(db.Numeric, nullable=False, server_default='0', default=0)	
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class Customer(db.Model, Entity):
	__tablename__ = 'co_customer'
	id = db.Column(db.BigInteger, primary_key=True)				
	name = db.Column(db.Text, nullable=False, server_default='-', default='-')
	phone_number = db.Column(db.String(30), nullable=False, server_default='-')	
	email = db.Column(db.String(255), nullable=False, server_default='-')
	merchant_id = db.Column(db.ForeignKey(u'co_merchant.id'), nullable=False)
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class Cashbox(db.Model, Entity):
	__tablename__ = 'co_cash_box'
	id = db.Column(db.BigInteger, primary_key=True)				
	name = db.Column(db.Text, nullable=False, server_default='-', default='-')	
	total_amount = db.Column(db.Numeric, nullable=False, server_default='0', default=0)
	merchant_id = db.Column(db.ForeignKey(u'co_merchant.id'), nullable=False)
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)



class CashboxHistory(db.Model, Entity):
	__tablename__ = 'co_cashbox_history'
	id = db.Column(db.BigInteger, primary_key=True)
	cash_box_id = db.Column(db.ForeignKey(u'co_cash_box.id'), nullable=False)
	amount = db.Column(db.Numeric, nullable=False, server_default='0')		
	remark = db.Column(db.Text, nullable=False, default='')
	payment_method = db.Column(db.String(10), nullable=False, server_default='-', default='-')
	transaction_date = db.Column(db.DateTime(timezone=False), default=datetime.now)
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)

if __name__ == '__main__':
	app.config['SQLALCHEMY_DATABASE_URI'] = os.environ.get('db.name', 'postgresql://kiditz:rioters7@172.17.0.1:2070/cash_overflow')
	migrate = Migrate(app, db)
	manager = Manager(app)
	manager.add_command('db', MigrateCommand)
	manager.run()
