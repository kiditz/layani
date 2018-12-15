package com.overflow.cash.utils

import java.math.BigDecimal
import java.math.RoundingMode

fun round(value: Double, places: Int): Double {
    var bd = BigDecimal(java.lang.Double.toString(value))
    bd = bd.setScale(places, RoundingMode.HALF_UP)
    return bd.toDouble()
}