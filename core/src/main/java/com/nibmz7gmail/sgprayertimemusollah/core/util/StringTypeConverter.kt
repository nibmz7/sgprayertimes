package com.nibmz7gmail.sgprayertimemusollah.core.util

import androidx.room.TypeConverter
import java.util.*

class StringTypeConverter {

    @TypeConverter
    fun stringToSomeObjectList(data: String?): List<String> {
        return data?.toListItems() ?: Collections.emptyList()
    }

    @TypeConverter
    fun someObjectListToString(someObjects: List<String>): String {
        return someObjects.toJsonString()
    }
}