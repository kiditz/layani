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

    companion object {
        const val HEADER = 1
        const val GENERAL = 2

        @JvmStatic
        fun generate(inputList:List<Data>, key:String, format: SimpleDateFormat?=null):List<Group>{
            val groupedList = mutableListOf<Group>()
            val groupBy = inputList.groupBy {
                if(format != null){
                    format.format(Date(it.getLong(key)))
                }else{
                    it[key]
                }
            }
            groupBy.keys.forEach {
                val itemHeader = Group()
                itemHeader.type = Group.HEADER
                itemHeader[key] = it
                groupedList.add(itemHeader)

                groupBy[it]?.forEach {
                    val itemData = Group()
                    itemData.putAll(it.map)
                    itemData.type = Group.GENERAL
                    groupedList.add(itemData)
                }
            }
            return groupedList
        }
    }

    var type: Int = HEADER
}