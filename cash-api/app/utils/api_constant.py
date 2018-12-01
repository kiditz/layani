IMAGE_EXTENSIONS = {'pdf', 'png', 'jpg', 'jpeg', 'gif', 'mp4', 'html'}


def allowed_file(filename):
	return '.' in filename and filename.rsplit('.', 1)[1].lower() in IMAGE_EXTENSIONS


class StockRef(object):
	TRANSACTION = 100
	IN = 101
	OUT = 102
	NEW_STOCK = 'new.stock'
	ADD_STOCK = 'add.stock'
	EDIT_STOCK = 'edit.stock'
	CLEAR_STOCK = 'clear.stock'


class PaymentMethod(object):
	CASH = 'CASH'
	CREDIT = 'CREDIT'
	DEBIT = 'DEBIT'


class ErrorCode(object):
	CASHBOX_NOT_FOUND = "cashbox.not.found"
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
	ORDER_NOT_FOUND = 'order.not.found'
