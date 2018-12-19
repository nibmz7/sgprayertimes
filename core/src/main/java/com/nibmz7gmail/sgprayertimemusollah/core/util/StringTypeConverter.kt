package com.nibmz7gmail.sgprayertimemusollah.core.util

import androidx.room.TypeConverter
import java.util.*
import com.nibmz7gmail.sgprayertimemusollah.core.util.StringUtils.toJsonString
import com.nibmz7gmail.sgprayertimemusollah.core.util.StringUtils.toStringList

class StringTypeConverter {

    @TypeConverter
    fun stringToSomeObjectList(data: String?): List<String> {
        return data?.toStringList() ?: Collections.emptyList()
    }

    @TypeConverter
    fun someObjectListToString(someObjects: List<String>): String {
        return someObjects.toJsonString()
    }
}