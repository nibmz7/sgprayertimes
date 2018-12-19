package com.nibmz7gmail.sgprayertimemusollah.core.util

import timber.log.Timber

object StringUtils {

    fun String.toStringList(): List<String>? {
        return split(",")
    }

    fun List<String>.toJsonString(): String {

        val sb = StringBuilder("")

        var idx = 0
        do {
            sb.append(get(idx))
            sb.append(",")
            idx++
        } while (idx < size-1)

        sb.append(get(idx))
        return sb.toString()
    }

}