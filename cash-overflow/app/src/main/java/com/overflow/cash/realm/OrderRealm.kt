package com.overflow.cash.realm

import com.overflow.cash.model.OrderItem
import com.overflow.libs.core.Data
import io.realm.Realm
import timber.log.Timber

class OrderRealm(val realm: Realm){
    fun addItem(input:Data, updateQty:Boolean=true): OrderItem? {
        val data = Data.convertKeyToCamelCase(input)
        Timber.i("Data : %s", data.toString())
        realm.beginTransaction()
        val item = realm.createOrUpdateObjectFromJson(OrderItem::class.java, data.toString())!!
        if(updateQty){
            Timber.i("ADD Quantity")
            item.qty = item.qty+ 1
        } else{
            Timber.i("Not Quantity")
        }

        Timber.i("Item : %s", item.productId.toString())
        realm.commitTransaction()
        return item
    }

    fun sumOrderSubTotal(): Number? {
        var totalAmount:Number
        val items = realm.where(OrderItem::class.java).findAll()
        totalAmount = items.sum("subTotal")
        items.addChangeListener { t, _ ->
            totalAmount = t.sum("subTotal")
        }
        return totalAmount
    }
    fun loadOrder():MutableList<Data>{
        val results = realm.where(OrderItem::class.java).findAll()
        val copyResults = realm.copyFromRealm(results)
        val dataList = mutableListOf<Data>()
        copyResults.forEach{
            val data = Data(it)
            dataList.add(data)
        }
        return dataList
    }


    fun deleteItems(){

        realm.beginTransaction()
        realm.where(OrderItem::class.java).findAll().deleteAllFromRealm()
        realm.commitTransaction()
    }

    fun deleteItem(id:Long){
        realm.beginTransaction()
        val orderItem = realm.where(OrderItem::class.java).equalTo("productId", id).findFirst()
        orderItem?.deleteFromRealm()
        realm.commitTransaction()
    }

}