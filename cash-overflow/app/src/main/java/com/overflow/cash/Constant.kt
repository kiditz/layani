package com.overflow.cash

class Constant {

    companion object {
        // Request Code
        const val REQUEST_LOGIN: Int = 0
        const val REQUEST_CODE_IMAGE: Int = 1
        const val REQUEST_PERMISSION_CODE: Int = 2
        const val REQUEST_CODE_SCANNER: Int = 3
        const val REQUEST_CODE_VIEW_CUSTOMER: Int = 4
        // Intent Action
        const val ACTION_REGISTER_DEVICE = "com.slerpio.fl.REGISTER"
        const val ACTION_DEREGISTER_DEVICE = "com.slerpio.fl.DEREGISTER"
        const val ACTION_ENABLE_NOTIFICATION = "com.slerpio.fl.notification.ENABLE"
        const val ACTION_RECEIVE_MESSAGE = "com.slerpio.fl.notification.RECEIVE_MESSAGE"
        const val SUCCESS_MESSAGE = "success_message"
        const val GOTO = "goto"
        const val SALES = "sales"
        const val MAX_PAGE = "MAX_PAGE"
        const val TEXT_EMPTY = ""
    }

    interface Sort{
        companion object {
            const val BY_NAME: String = "product_name asc"
            const val BY_LEAST_STOCK: String = "stock asc"
            const val BY_MOST_STOCK: String = "stock desc"
            const val BY_SELL_PRICE: String = "sell_price asc"
            const val BY_PURCHASE_PRICE: String = "purchase_price asc"
        }
    }
    interface Directory {
        companion object {
            const val PROFILE: String = "profile"
            const val GROUP: String = "group"
        }
    }
    interface ActivityType {
        companion object {
            const val LESSON: String = "LESSON"
            const val TASK: String = "TASK"
            const val FOLLOW: String = "FOLLOW"
            const val UNFOLLOW: String = "UNFOLLOW"
            const val LESSON_VIEWER: String = "LESSON_VIEWER"
        }
    }

    interface QuestionType {
        companion object {
            const val ESSAY: String = "E"
            const val MULTIPLE_CHOICE: String = "MC"
        }
    }

    interface TranslationsKey{
        companion object {
            const val REQUIRED_VALUE_STORE_NAME = "required.value.store_name"
            const val REQUIRED_VALUE_STORE_ADDRESS = "required.value.store_address"
            const val REQUIRED_VALUE_STORE_PHONE_NUMBER = "required.value.store_phone_number"
            const val REQUIRED_VALUE_STORE_EMAIL = "required.value.store_email"
            const val REQUIRED_VALUE_STORE_OWNER_NAME="required.value.store_owner_name"

            const val REQUIRED_VALUE_PHONE_NUMBER = "required.value.phone_number"
            const val INVALID_PHONE_NUMBER = "invalid.phone.number"

            const val REQUIRED_VALUE_CATEGORY_NAME = "required.value.category_name"
            const val REQUIRED_VALUE_NAME = "required.value.name"
            const val REQUIRED_VALUE_USERNAME = "required.value.username"
            const val REQUIRED_VALUE_FULLNAME = "required.value.fullname"
            const val REQUIRED_VALUE_PASSWORD = "required.value.password"
            const val INVALID_PASSWORD_LENGTH = "invalid.password.length"
            const val REQUIRED_VALUE_ADDRESS = "required.value.address"

            const val REQUIRED_VALUE_EMAIL = "required.value.email"

            const val NO_INTERNET = "no.internet"
            const val CONNECTION_TIMEOUT = "connection.timeout"
            const val SYSTEM_ERROR = "system.error"

            const val CATEGORY_CREATED_SUCCESSFULLY = "category.created.successfully"
            const val PRODUCT_CREATED_SUCCESSFULLY = "product.created.successfully"
            const val STOCK_CREATED_SUCCESSFULLY = "stock.created.successfully"
            const  val SALES_CREATED_SUCCESSFULY = "sales.created.successfully"
            const  val ACCOUNT_RECEIVEABLE_CREATED_SUCCESSFULY = "account_receiveable.created.successfully"
            const  val ACCOUNT_RECEIVEABLE_SAVED_SUCCESSFULY = "account_receiveable.saved.successfully"
            const val DISCOUNT_CREATED_SUCCESSFULLY = "discount.created.successfully"
            const val REQUIRED_VALUE_PRODUCT_CODE = "required.value.product_code"
            const val REQUIRED_VALUE_CUSTOMER_NAME = "required.value.customer_name"
            const val REQUIRED_VALUE_PRODUCT_NAME = "required.value.product_name"
            const val REQUIRED_VALUE_PRODUCT_WEIGHT = "required.value.product_weight"
            const val REQUIRED_VALUE_PRODUCT_UNIT = "required.value.product_unit"
            const val REQUIRED_VALUE_PRODUCT_SELL_PRICE = "required.value.product_sell_price"
            const val REQUIRED_VALUE_PRODUCT_INIT_PRICE = "required.value.product_init_price"
            const val REQUIRED_VALUE_PRODUCT_PURCH_PRICE = "required.value.product_purch_price"
            const val REQUIRED_VALUE_PRODUCT_QTY = "required.value.product_qty"
            const val REQUIRED_VALUE_CASHBOX = "required.value.cashbox"
            const val SELL_PRICE_MUST_GREATER_THAN_ZERO = "sell_price.must.be.greater_than_zero"
            const val INIT_PRICE_MUST_GREATER_THAN_ZERO = "init_price.must.be.greater_than_zero"
            const val UNIT_MUST_LESS_THAN_THERR = "unit.must.be.less_than_three"
            const val PURCHASE_PRICE_MUST_GREATER_THAN_ZERO = "purchase_price.must.be.greater_than_zero"
            const val STOCK_PRICE_MUST_GREATER_THAN_ZERO = "stock.must.be.greater_than_zero"
            const val DISCOUNT_MUST_GREATER_THAN_ZERO = "discount.must.be.greater_than_zero"
            const val DISCOUNT_WHEN_MUST_GREATER_THAN_ZERO = "discount_when.must.be.greater_than_zero"
            const val TOTAL_AMOUNT_GREATER_THAN = "total_amount.must.be.greater_than"
            const val TOTAL_AMOUNT_LESS_THAN = "total_amount.must.be.less_than"
            //LOGIN
            const val USER_NOT_FOUND = "user.not.found"
            const val USER_WRONG_PASSWORD = "user.wrong.password"


        }
    }

    interface PaymentMethod{
        companion object {
            const val CASH: String = "CASH"
            const val CREDIT: String = "CREDIT"
            const val IN_PROGRESS: String = "IN_PROGRESS"
        }
    }

    interface PaymentStatus{
        companion object {
            const val DONE: String = "payment.done"
            const val IN_PROGRESS: String = "payment.in_progress"
        }
    }

    interface TransactionStatus{
        companion object {
            const val SUCCESS = "S"
            const val IN_PROGRESS: String = "I"
            const val CANCEL: String = "C"
        }
    }

}