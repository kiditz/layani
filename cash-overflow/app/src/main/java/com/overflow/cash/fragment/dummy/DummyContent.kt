package com.overflow.cash.fragment.dummy

import java.util.ArrayList
import java.util.HashMap

/**
 * Helper class for providing sample productName for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object DummyContent {

    val ITEMS: MutableList<DummyItem> = ArrayList()
    private val ITEM_MAP: MutableMap<String, DummyItem> = HashMap()

    private const val COUNT = 1

    init {
        for (i in 1 until COUNT) {
            addItem(createDummyItem(i))
        }
    }

    private fun addItem(item: DummyItem) {
        ITEMS.add(item)
        ITEM_MAP[item.productCode] = item
    }

    private fun createDummyItem(position: Int): DummyItem {
        return DummyItem(position.toString(), "Item $position", "Rp. 150$position")
    }


    /**
     * A dummy item representing a piece of productName.
     */
    data class DummyItem(val productCode: String, val productName: String, val sellPrice: String) {
        override fun toString(): String = productName
    }
}
