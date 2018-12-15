package com.overflow.cash

import org.junit.Test

import org.junit.Assert.*
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val round3 = round(2500.0, -3)
        print(round3)
    }

    private fun round(value: Double, places: Int): Double {
        var bd = BigDecimal(java.lang.Double.toString(value))
        bd = bd.setScale(places, RoundingMode.HALF_UP)
        return bd.toDouble()
    }
}
