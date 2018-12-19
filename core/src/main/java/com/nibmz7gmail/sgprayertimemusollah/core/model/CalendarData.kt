package com.nibmz7gmail.sgprayertimemusollah.core.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "calendar_table")
data class CalendarData (
    @PrimaryKey val date: String,
    val day: Int,
    val prayerTimes: List<String>,
    @Embedded val hijriDate: HijriDate
)