package com.nibmz7gmail.sgprayertimemusollah.core.data.calendar.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData

@Database(entities = [CalendarData::class], version = 1)
abstract class CalendarRoomDatabase : RoomDatabase() {

    abstract fun calendarDao(): CalendarDao
}