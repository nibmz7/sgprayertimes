package com.nibmz7gmail.sgprayertimemusollah.core.data.calendar.remote

import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okio.BufferedSource
import java.io.BufferedReader
import java.io.InputStreamReader

object CalendarDataJsonParser {

    fun parseConferenceData(source: BufferedSource): List<CalendarData>? {

        val reader = JsonReader.of(source)
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val type = Types.newParameterizedType(List::class.java, CalendarData::class.java)
        val calendarDataAdapter = moshi.adapter<List<CalendarData>>(type)

        return calendarDataAdapter.fromJson(reader)
    }

}

