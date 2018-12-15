import logging as log
import unittest

import requests


class TestProduct(unittest.TestCase):
	BASE_URL = "http://localhost:5010/product/"
	
	def setUp(self):
		log.basicConfig(level=log.DEBUG)
		pass
	
	def test_add_product(self):
		result = requests.post(self.BASE_URL + 'add', json={
			"code": "HEADSET_SAMSUNG",
			"sell_price": 250000,
			"qty": 20,
			"product_type": "ITEM",
			"name": "Headset Samsung",
			"outlet_id": 1
		})
		log.debug("Result : %s", result.content)
		self.assertIs(result.status_code, 200)
		self.assertEqual(result.json()['status'], 'OK')
	

if __name__ == '__main__':

	unittest.main()