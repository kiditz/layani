from slerp.app import app, run
from flask_babel import Babel
from flask import g, abort
from api import *
from utils import JsonEncoder

app.json_encoder = JsonEncoder
babel = Babel(app)


@babel.localeselector
def get_locale():
    return g.get('lang_code', app.config['BABEL_DEFAULT_LOCALE'])


@babel.timezoneselector
def get_timezone():
    user = g.get('user', None)
    if user is not None:
        return user.timezone
    

app.register_blueprint(outlet_api_blue_print)
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
app.register_blueprint(chart_api_blue_print)
app.register_blueprint(receipt_api_blue_print)
if __name__ == '__main__':
    run()
