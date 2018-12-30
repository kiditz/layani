IMAGE_EXTENSIONS = {'pdf', 'png', 'jpg', 'jpeg', 'gif', 'mp4', 'html'}
PAYLOAD = 'payload'


def allowed_file(filename):
	return '.' in filename and filename.rsplit('.', 1)[1].lower() in IMAGE_EXTENSIONS


class StockRef(object):
	TRANSACTION = 100
	IN = 101
	OUT = 102
	# Pemotongan stok yang di sebabkan oleh discount atau promo yang di lakukan oleh pemilik usaha
	DISCOUNT = 103
	NEW_STOCK = 'new.stock'
	ADD_STOCK = 'add.stock'
	EDIT_STOCK = 'edit.stock'
	CLEAR_STOCK = 'clear.stock'


class PaymentMethod(object):
	CASH = 'CASH'	
	CARD = 'CARD'	


class CashboxType(object):
	DEBIT = 'DEBIT'
	CREDIT = 'CREDIT'

class CashboxStatus(object):
	OPEN = 'O'
	END = 'E'

class CashDrawer(object):
	CASH_DRAWER = 'CASH_DRAWER'
	BANK = 'BANK'


class ProductType(object):
	ITEM = 'ITEM'
	SERVICE = 'SERVICE'


class DiscountMethod(object):
	DISCOUNT_AMOUNT_TRANSACTION = 0
	BY_N_GET_ONE = 1
	DISCOUNT_AMOUNT_PRODUCT = 2
	
	
class OrderStatus(object):
	IN_PROGRESS = 'I'
	PENDING = 'P'
	SUCCESS = 'S'
	VOID = 'V'
	CREATED = 'C'
		

class ErrorCode(object):
	PRODUCT_NOT_FOUND = 'product.not.found'
	REFUND_FAILED = "refund.failed"
	CASHBOX_NOT_FOUND = "cashbox.not.found"
	CASHBOX_HAS_END = "cashbox.has.end"
	UPLOAD_FAIL = 'upload.image.fail'
	FILE_NOT_FOUND = 'file.not.found'
	FILE_CANNOT_BE_EMPTY = 'file.cannot.be.empty'
	USER_NOT_FOUND = 'user.not.found'
	USER_HAS_EXISTS = 'user.has.exists'
	PHONE_NUMBER_HAS_EXISTS = 'phone_number.has.exists'
	EMAIL_HAS_EXISTS = 'email.has.exists'
	USER_WRONG_PASSWORD = 'user.wrong.password'
	
	REQUIRED_CATEGORY = 'required.valid.category_name',
	PRODUCT_CODE_EXIST = 'product.code.exists'
	NOT_ENOUGH_STOCK = 'stock.is.not.enough'
	DISCOUNT_NOT_FOUND = 'discount.not.found'
	DISCOUNT_NOT_FOR_TODAY = 'discount.not.for.today'
	ORDER_NOT_FOUND = 'order.not.found'
	ORDER_CANNOT_BE_DELETED = 'order.cannot.be.deleted'
	ORDER_ITEM_NOT_FOUND = 'order.item.not.found'
	INVALID_PHONE_NUMBER = 'invalid.phone.number'
	INVALID_EMAIL_ADDRESS = 'invalid.email.address'
	INVALID_TOTAL_AMOUNT = 'invalid.total.amount'
