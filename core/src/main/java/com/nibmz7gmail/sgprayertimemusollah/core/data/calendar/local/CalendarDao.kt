package com.nibmz7gmail.sgprayertimemusollah.core.data.calendar.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData

@Dao
interface CalendarDao {

    @Query("SELECT * FROM calendar_table")
    fun getAll(): List<CalendarData>

    @Query("SELECT * FROM calendar_table WHERE date LIKE :date LIMIT 1")
    fun findByDate(date: String): CalendarData

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg datas: CalendarData)

    @Query("DELETE FROM calendar_table")
    fun deleteAll()
}