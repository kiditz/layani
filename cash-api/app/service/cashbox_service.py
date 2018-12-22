from entity.models import Cashbox, CashboxHistory
from utils.api_constant import CashDrawer, CashboxType
from slerp.logger import logging
from slerp.validator import Number, Blank

from sqlalchemy import and_, cast, func, Interval
from sqlalchemy.dialects.mssql import DATE

from decimal import Decimal
log = logging.getLogger(__name__)


class CashboxService(object):
	def __init__(self):
		super(CashboxService, self).__init__()
	
	@Number(['outlet_id'])
	@Blank(['total_amount', 'remark'])
	def edit_cashbox(self, domain):
		total_amount = Decimal(domain['total_amount'])
		remark = domain['remark']
		outlet_id = domain['outlet_id']
		cashbox = Cashbox.query.filter_by(outlet_id=outlet_id).filter_by(name=CashDrawer.CASH_DRAWER).first()
		cashbox.total_amount = total_amount		
		cashbox_history = CashboxHistory()
		cashbox_history.cash_box_id = cashbox.id
		cashbox_history.amount = total_amount
		if total_amount > 0:
			cashbox_history.payment_method = CashboxType.DEBIT
			cashbox_history.refid = 1
		else:
			cashbox_history.payment_method = CashboxType.CREDIT
			cashbox_history.refid = 2
		cashbox_history.remark = domain['remark']
		cashbox.save()
		cashbox_history.save()

	@Number(['cash_box_summary_id', 'page', 'size', 'date'])
	def get_cashbox_history(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		cashbox_history_q = CashboxHistory.query\
			.filter(CashboxHistory.cash_box_summary_id == domain['cash_box_summary_id'])\
			.order_by(CashboxHistory.id.desc())\
			.paginate(page, size, error_out=False)
		cashbox_history_list = list(map(lambda x: x._asdict(), cashbox_history_q.items))
		return {'payload': cashbox_history_list, 'total': cashbox_history_q.total, 'total_pages': cashbox_history_q.pages}

