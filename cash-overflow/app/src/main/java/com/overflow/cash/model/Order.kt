package com.overflow.cash.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class Order : RealmObject() {
    @PrimaryKey
    var id:Long=0
    @Required
    var sales:String=""
}