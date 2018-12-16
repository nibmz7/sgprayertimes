package com.nibmz7gmail.sgprayertimemusollah.core.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nibmz7gmail.sgprayertimemusollah.core.util.StringTypeConverter
import com.squareup.moshi.Json


@Entity(tableName = "calendar_table")
data class CalendarData (
    @Json(name = "dtN") @PrimaryKey val date: String,
    @Json(name = "dN") val day: Int,
    @Json(name = "P") val prayerTimes: List<String>,
    @Json(name = "H")@Embedded val hijriDate: HijriDate
)