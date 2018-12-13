package com.nibmz7gmail.sgprayertimemusollah.core.data.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nibmz7gmail.sgprayertimemusollah.core.AsyncScheduler
import com.nibmz7gmail.sgprayertimemusollah.core.data.calendar.local.CalendarDao
import com.nibmz7gmail.sgprayertimemusollah.core.data.calendar.local.CalendarRoomDatabase
import com.nibmz7gmail.sgprayertimemusollah.core.data.calendar.remote.RemoteCalendarDataSource
import com.nibmz7gmail.sgprayertimemusollah.core.data.calendar.remote.RemoteDataSource
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import com.nibmz7gmail.sgprayertimemusollah.core.util.toString
import java.io.IOException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class CalendarDataRepository @Inject constructor(
    private val calendarDb: CalendarRoomDatabase,
    private val calendarDao: CalendarDao,
    private val remoteCalendarDataSource: RemoteDataSource
) {
    // In-memory cache of the calendar data
    private var calendarDataCache: List<CalendarData>? = null

    var latestException: Exception? = null
        private set

    fun getTodaysData(date: String): CalendarData {
        return calendarDao.findByDate(date)
    }

    fun getAllData(): LiveData<CalendarData> {
        val result = MutableLiveData<List<CalendarData>>()
        if(calendarDataCache != null) result.value = calendarDataCache
        else {
            AsyncScheduler.execute {
                calendarDataCache = calendarDao.getAll()
                result.postValue(calendarDataCache)
            }
        }
        return result as LiveData<CalendarData>
    }

    fun refreshCalendarData(): Boolean {
        if(calendarDataCache != null) return true

        val calendarData = try {
            remoteCalendarDataSource.getRemoteCalendarData()
        } catch (e: IOException) {
            latestException = e
            throw e
        } ?: return false

        calendarDataCache = calendarData
        calendarDao.insertAll(*calendarData.toTypedArray())

        return true
    }

}