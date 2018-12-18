package com.overflow.cash.realm

import com.overflow.cash.model.OrderItem
import com.overflow.libs.core.Data
import io.realm.Realm
import io.realm.RealmResults
import timber.log.Timber

class OrderItemRealm(val realm: Realm){
    fun addItem(input:Data, updateQty:Boolean=true): OrderItem? {
        val data = Data.convertKeyToCamelCase(input)
        realm.beginTransaction()
        val item = realm.createOrUpdateObjectFromJson(OrderItem::class.java, data.toString())!!
        if(updateQty){
            item.qty = item.qty+ 1
        } else{
        }

        Timber.d("Items: %s", item.productId.toString())
        realm.commitTransaction()
        return item
    }

    fun setItem(input:Data): OrderItem? {
        realm.beginTransaction()
        val item = realm.createOrUpdateObjectFromJson(OrderItem::class.java, input.toString())!!
        realm.commitTransaction()
        return item
    }

    fun loadAll():RealmResults<OrderItem>{
        return realm.where(OrderItem::class.java).findAll()
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