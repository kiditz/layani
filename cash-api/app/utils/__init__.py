from .encoder import JsonEncoder
import random


def gen_password(length):
	return ''.join([str(random.randint(0, 10)) for _ in range(length)])


def str2bool(v):
  return v.lower() in ("yes", "true", "t", "1")