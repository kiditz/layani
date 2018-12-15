package com.overflow.cash

import com.overflow.libs.core.Data
import com.overflow.libs.core.Group
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class GroupListByDate{
    @Test
    fun groupingList(){
        val data = """
            {
  "payload": [
    {
      "cash_box_id": null,
      "cashback": 2500.0,
      "customer_name": "Anes",
      "id": 65,
      "order_at": 1544801477000,
      "order_code": "0000000065",
      "outlet_id": 1,
      "payment_method": "CASH",
      "receiveable_date": null,
      "status": "S",
      "total_amount": 2500.0,
      "total_credit": null,
      "total_payment": 5000.0
    },
    {
      "cash_box_id": null,
      "cashback": 5000.0,
      "customer_name": null,
      "id": 64,
      "order_at": 1544801421000,
      "order_code": "0000000064",
      "outlet_id": 1,
      "payment_method": "CASH",
      "receiveable_date": null,
      "status": "S",
      "total_amount": 5000.0,
      "total_credit": null,
      "total_payment": 10000.0
    },
    {
      "cash_box_id": null,
      "cashback": 500.0,
      "customer_name": null,
      "id": 63,
      "order_at": 1544798678000,
      "order_code": "0000000063",
      "outlet_id": 1,
      "payment_method": "CASH",
      "receiveable_date": null,
      "status": "S",
      "total_amount": 12500.0,
      "total_credit": null,
      "total_payment": 13000.0
    },
    {
      "cash_box_id": null,
      "cashback": 0.0,
      "customer_name": "Anes",
      "id": 62,
      "order_at": 1544791819000,
      "order_code": "0000000062",
      "outlet_id": 1,
      "payment_method": "CARD",
      "receiveable_date": null,
      "status": "S",
      "total_amount": 5000.0,
      "total_credit": null,
      "total_payment": 5000.0
    },
    {
      "cash_box_id": null,
      "cashback": 0.0,
      "customer_name": "Anes",
      "id": 61,
      "order_at": 1544791335000,
      "order_code": "0000000061",
      "outlet_id": 1,
      "payment_method": "CASH",
      "receiveable_date": null,
      "status": "S",
      "total_amount": 5000.0,
      "total_credit": null,
      "total_payment": 5000.0
    },
    {
      "cash_box_id": null,
      "cashback": 0.0,
      "customer_name": "Anes",
      "id": 60,
      "order_at": 1544789350000,
      "order_code": "0000000060",
      "outlet_id": 1,
      "payment_method": "CASH",
      "receiveable_date": null,
      "status": "S",
      "total_amount": 10000.0,
      "total_credit": null,
      "total_payment": 10000.0
    },
    {
      "cash_box_id": null,
      "cashback": 0.0,
      "customer_name": null,
      "id": 59,
      "order_at": 1544730027000,
      "order_code": "0000000059",
      "outlet_id": 1,
      "payment_method": "CASH",
      "receiveable_date": null,
      "status": "S",
      "total_amount": 20000.0,
      "total_credit": null,
      "total_payment": 20000.0
    },
    {
      "cash_box_id": null,
      "cashback": 0.0,
      "customer_name": null,
      "id": 58,
      "order_at": 1544729932000,
      "order_code": "0000000058",
      "outlet_id": 1,
      "payment_method": "CASH",
      "receiveable_date": null,
      "status": "S",
      "total_amount": 5000.0,
      "total_credit": null,
      "total_payment": 5000.0
    },
    {
      "cash_box_id": null,
      "cashback": 5000.0,
      "customer_name": "Anes",
      "id": 57,
      "order_at": 1544720571482,
      "order_code": "0000000057",
      "outlet_id": 1,
      "payment_method": "CASH",
      "receiveable_date": null,
      "status": "S",
      "total_amount": 15000.0,
      "total_credit": null,
      "total_payment": 20000.0
    },
    {
      "cash_box_id": null,
      "cashback": 0.0,
      "customer_name": null,
      "id": 56,
      "order_at": 1544548236375,
      "order_code": "0000000056",
      "outlet_id": 1,
      "payment_method": "CARD",
      "receiveable_date": null,
      "status": "S",
      "total_amount": 15000.0,
      "total_credit": null,
      "total_payment": 15000.0
    }
  ],
  "status": "OK",
  "total": 25,
  "total_pages": 3
}

        """.trimIndent()
        val payloads = Data(data).getList("payload")
        for (item in Group.generate(payloads, "total_amount")){
            println("${item.type}")
            println("   ${item["total_amount"]}")
        }
    }
}