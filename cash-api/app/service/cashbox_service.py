from slerp.validator import Key, Number, Blank
from slerp.logger import logging
from slerp.app import db

from entity.models import Cashbox


log = logging.getLogger(__name__)


class CashboxService(object):
	def __init__(self):
		super(CashboxService, self).__init__()
	
	@Blank(['outlet_id'])
	@Number(['page', 'size'])
	def get_cashbox_by_outlet_id(self, domain):
	
		page = int(domain['page'])
		size = int(domain['size'])
		cashbox_q = Cashbox.query.filter_by(outlet_id=domain['outlet_id']).order_by(Cashbox.created_at.asc()).paginate(page, size, error_out=False)
		cashbox_list = list(map(lambda x: x.to_dict(), cashbox_q.items))
		return {'payload': cashbox_list, 'total': cashbox_q.total, 'total_pages': cashbox_q.pages}