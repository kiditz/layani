from decimal import Decimal
from datetime import datetime
from entity.models import CashboxHistory, CashboxSummary, User
from slerp.logger import logging
from slerp.validator import Number, Blank, Key, ValidationException
from sqlalchemy import cast, func
from sqlalchemy.dialects.mssql import DATE

from utils.api_constant import CashboxType, CashboxStatus, ErrorCode

log = logging.getLogger(__name__)


class CashboxService(object):
	def __init__(self):
		super(CashboxService, self).__init__()
	
	@Number(['outlet_id', 'user_id'])
	@Blank(['total_amount', 'remark'])
	def edit_cashbox_history(self, domain):
		total_amount = Decimal(domain['total_amount'])
		remark = domain['remark']
		outlet_id = domain['outlet_id']
		cashbox_summary = CashboxSummary.query\
			.filter(CashboxSummary.outlet_id == outlet_id)\
			.filter(cast(CashboxSummary.start_at, DATE) == datetime.now().date())\
			.filter(CashboxSummary.status == CashboxStatus.OPEN).first()
		if cashbox_summary is None:
			cashbox_summary = CashboxSummary()
			cashbox_summary.start_at = datetime.now()
			cashbox_summary.outlet_id = domain['outlet_id']
			cashbox_summary.user_id = domain['user_id']
			
		cashbox_history = CashboxHistory()
		cashbox_history.cash_box_summary_id = cashbox_summary.id
		cashbox_history.amount = total_amount
		if total_amount > 0:
			cashbox_history.payment_method = CashboxType.DEBIT
			cashbox_history.ref_id = 1
		else:
			cashbox_history.payment_method = CashboxType.CREDIT
			cashbox_history.ref_id = 2
		cashbox_history.remark = remark
		cashbox_summary.save()
		cashbox_history.save()
		return {'payload': cashbox_history.to_dict()}

	@Number(['cash_box_summary_id', 'page', 'size'])
	def get_cashbox_history(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		cashbox_history_q = CashboxHistory.query\
			.filter(CashboxHistory.cash_box_summary_id == domain['cash_box_summary_id'])\
			.order_by(CashboxHistory.id.desc())\
			.paginate(page, size, error_out=False)
		cashbox_history_list = list(map(lambda x: x.to_dict(), cashbox_history_q.items))
		return {'payload': cashbox_history_list, 'total': cashbox_history_q.total, 'total_pages': cashbox_history_q.pages}
	
	@Number(['outlet_id', 'page', 'size'])
	def get_cashbox_summary(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		outlet_id = domain['outlet_id']
		entities = (
			CashboxSummary.id,
			CashboxSummary.start_at,
			CashboxSummary.end_at,
			CashboxSummary.status,
			CashboxSummary.pending,
			CashboxSummary.cash,
			CashboxSummary.card,
			CashboxSummary.transaction,
			CashboxSummary.difference,
			CashboxSummary.outlet_id,
			User.fullname
		)
		
		cashbox_summary_q = CashboxSummary.query.with_entities(*entities)\
			.join(User, CashboxSummary.user_id == User.id)\
			.filter(CashboxSummary.outlet_id == outlet_id)\
			.order_by(CashboxSummary.id.desc())\
			.paginate(page, size, error_out=False)
		cashbox_summary_list = list(map(lambda x: x._asdict(), cashbox_summary_q.items))
		return {'payload': cashbox_summary_list, 'total': cashbox_summary_q.total, 'total_pages': cashbox_summary_q.pages}
	
	@Number(['id', 'card', 'cash'])
	@Key(['end_at', 'void', 'sales', 'pending'])
	def edit_cashbox_summary_by_id(self, domain):
		sales = Decimal(domain['sales'])
		void = Decimal(domain['void'])
		cash = Decimal(domain['cash'])
		card = Decimal(domain['card'])
		cashbox_summary = CashboxSummary.query.filter(CashboxSummary.id == domain['id']).first()
		if cashbox_summary is None:
			raise ValidationException(ErrorCode.CASHBOX_NOT_FOUND)
		
		cash_in = CashboxHistory.query.with_entities(func.coalesce(func.sum(CashboxHistory.amount), 0.0).label('amount'))\
			.filter(CashboxHistory.ref_id == 1) \
			.filter(CashboxHistory.cash_box_summary_id == cashbox_summary.id) \
			.first()
		cash_out = CashboxHistory.query.with_entities(func.coalesce(func.sum(CashboxHistory.amount), 0.0).label('amount'))\
			.filter(CashboxHistory.ref_id == 1) \
			.filter(CashboxHistory.cash_box_summary_id == cashbox_summary.id) \
			.first()
		cashbox_summary.pending = domain["pending"]
		cashbox_summary.transaction = sales - void + cash_in - cash_out
		cashbox_summary.cash = cash
		cashbox_summary.card = card
		cashbox_summary.refund = void
		cashbox_summary.status = CashboxStatus.END
		cashbox_summary.difference = (cash + card) - cashbox_summary.transaction
		cashbox_summary.end_at = domain['end_at']
		cashbox_summary.save()
		return {'payload': cashbox_summary.to_dict()}
