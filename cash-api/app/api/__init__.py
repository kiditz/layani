from .cashbox_api import cashbox_api_blue_print
from .category_api import category_api_blue_print
from .chart_api import chart_api_blue_print
from .customer_api import customer_api_blue_print
from .discount_api import discount_api_blue_print
from .discount_api import discount_api_blue_print
from .document_api import document_api_blue_print
from .health_api import health_api_blue_print
from .order_api import order_api_blue_print
from .outlet_api import outlet_api_blue_print
from .product_api import product_api_blue_print
from .stock_api import stock_api_blue_print
from .receipt_api import receipt_api_blue_print
from .notification_token_api import notification_token_api_blue_print
from slerp.app import app
from utils import splitThousands
app.jinja_env.globals.update(thousand=splitThousands)
