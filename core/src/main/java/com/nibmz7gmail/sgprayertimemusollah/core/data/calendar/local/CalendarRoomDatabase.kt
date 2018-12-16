package com.nibmz7gmail.sgprayertimemusollah.core.data.calendar.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import com.nibmz7gmail.sgprayertimemusollah.core.util.StringTypeConverter

@Database(entities = [CalendarData::class], version = 1, exportSchema = false)
@TypeConverters(StringTypeConverter::class)
abstract class CalendarRoomDatabase : RoomDatabase() {

    abstract fun calendarDao(): CalendarDao
}