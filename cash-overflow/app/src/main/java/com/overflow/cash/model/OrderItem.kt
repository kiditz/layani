package com.overflow.cash.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required


open class OrderItem: RealmObject() {
    @PrimaryKey
    var productId:Long=0
    @Required
    var productName:String=""
    var documentId:Long=0
    var countDiscount:Long=0
    var discountAmount:Double= 0.0
    var discountType:String="PERCENTAGE"
    var qty:Long=0
    var subTotal:Double=0.0
    var sellPrice:Double=0.0
    var useStock:Boolean=false
    @Required
    var unit:String="pcs"
}