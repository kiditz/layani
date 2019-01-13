from slerp.app import app, run
from api import *
from utils import JsonEncoder

app.json_encoder = JsonEncoder
app.register_blueprint(health_api_blue_print)
app.register_blueprint(order_pulsa_api_blue_print)
app.register_blueprint(product_layani_api_blue_print)

if __name__ == '__main__':
    run()