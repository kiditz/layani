from slerp.app import app, run

from api import *
from utils import JsonEncoder

app.json_encoder = JsonEncoder

app.register_blueprint(merchant_api_blue_print)
app.register_blueprint(health_api_blue_print)
app.register_blueprint(category_api_blue_print)
app.register_blueprint(product_api_blue_print)
app.register_blueprint(document_api_blue_print)
app.register_blueprint(stock_api_blue_print)
app.register_blueprint(discount_api_blue_print)
app.register_blueprint(cashbox_api_blue_print)
app.register_blueprint(order_api_blue_print)
app.register_blueprint(customer_api_blue_print)
app.register_blueprint(discount_api_blue_print)
app.register_blueprint(account_receiveable_api_blue_print)

if __name__ == '__main__':
    run()
