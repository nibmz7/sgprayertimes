package com.nibmz7gmail.sgprayertimemusollah.core.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json


@Entity(tableName = "calendar_table")
data class CalendarData (
    @Json(name = "dtN") @PrimaryKey val date: String,
    @Json(name = "dN") val day: Int,
    @Json(name = "P")@Embedded val prayerTimes: PrayerTimes,
    @Json(name = "H")@Embedded val hijriDate: HijriDate
)