package com.overflow.libs.core

import java.text.SimpleDateFormat
import java.util.*

/**
 * This class is used to generate group for RecyclerView
 * Adapter with multiple view holder seperated by type
 * @author Rifky
 * @since 2018-12-14 00:32
 * */
class Group : Data() {
    var type: Int = HEADER
    companion object {
        const val HEADER = 1
        const val GENERAL = 2

        @JvmStatic
        fun generate(inputList:List<Data>, targetList:MutableList<Group>, key:String, format: SimpleDateFormat?=null){
            val groupBy = if (format != null){

                inputList.groupBy {
                    if (it[key] == null){
                        it[key] = System.currentTimeMillis()
                    }
                    format.format(Date(it.getLong(key)))
                }
            }else{
                inputList.groupBy { it[key] }
            }

            groupBy.keys.forEach {
                // Only Add if header key not exists in the list
                if (!hashKey(targetList, it)) {
                    val itemHeader = Group()
                    itemHeader.type = Group.HEADER
                    itemHeader[key] = it
                    targetList.add(itemHeader)
                }

                groupBy[it]?.forEach {
                    val itemData = Group()
                    itemData.putAll(it.map)
                    itemData.type = Group.GENERAL
                    targetList.add(itemData)
                }
            }

        }
        @JvmStatic
        private fun hashKey(payloads: List<Group>, key: Any?): Boolean {
            for (payload in payloads) {
                if (payload[key] == key)
                    return true
            }
            return false
        }
    }



}