from entity.models import Cashbox, CashboxHistory
from utils.api_constant import CashDrawer, CashboxType
from slerp.logger import logging
from slerp.validator import Number, Blank
from decimal import Decimal
log = logging.getLogger(__name__)


class CashboxService(object):
	def __init__(self):
		super(CashboxService, self).__init__()
		
	@Number(['outlet_id', 'page', 'size'])
	def get_cashbox_by_outlet_id(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		cashbox_q = Cashbox.query.filter_by(outlet_id=domain['outlet_id']).order_by(Cashbox.created_at.asc()).paginate(page, size, error_out=False)
		cashbox_list = list(map(lambda x: x.to_dict(), cashbox_q.items))
		return {'payload': cashbox_list, 'total': cashbox_q.total, 'total_pages': cashbox_q.pages}
	
	@Number(['outlet_id'])
	@Blank(['total_amount', 'remark'])
	def edit_cashbox(self, domain):
		total_amount = Decimal(domain['total_amount'])
		cashbox = Cashbox.query.filter_by(outlet_id=domain['outlet_id']).filter_by(name=CashDrawer.CASH_DRAWER).first()
		cashbox.total_amount = total_amount
		cashbox_history = CashboxHistory()
		cashbox_history.cash_box_id = cashbox.id
		cashbox_history.amount = total_amount
		if total_amount < 0:
			cashbox_history.payment_method = CashboxType.CREDIT
		else:
			cashbox_history.payment_method = CashboxType.DEBIT
		cashbox_history.remark = domain['remark']
		cashbox.save()
		cashbox_history.save()