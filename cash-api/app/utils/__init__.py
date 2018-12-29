from .encoder import JsonEncoder
from decimal import Decimal
import random


def gen_password(length):
	return ''.join([str(random.randint(0, 10)) for _ in range(length)])


def str2bool(v):
	return v.lower() in ("yes", "true", "t", "1")


def splitThousands(val):
	s = '%d' % abs(val)
	groups = []
	while s and s[-1].isdigit():
		groups.append(s[-3:])
		s = s[:-3]
	return s + '.'.join(reversed(groups))
