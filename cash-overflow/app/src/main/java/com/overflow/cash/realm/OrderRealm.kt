package com.overflow.cash.realm

import com.overflow.cash.model.Order
import com.overflow.libs.core.Data
import io.realm.Realm

class OrderRealm(val realm: Realm){
    fun addOrder(input:Data): Order? {
        val data = Data()
        data["id"] = getNextId()
        data["sales"] = input.toString()
        realm.beginTransaction()
        val order = realm.createObjectFromJson(Order::class.java, data.toString())!!
        realm.commitTransaction()
        return order
    }

    fun getNextId():Int{
        val currentIdNum = realm.where(Order::class.java).max("id")
        val nextId: Int
        nextId = if (currentIdNum == null) {
            1
        } else {
            currentIdNum.toInt() + 1
        }
        return nextId
    }
}