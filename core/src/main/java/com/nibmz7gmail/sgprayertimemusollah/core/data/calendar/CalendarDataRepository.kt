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
import timber.log.Timber
import java.io.IOException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

class CalendarDataRepository @Inject constructor(
    private val calendarDao: CalendarDao,
    private val remoteCalendarDataSource: RemoteDataSource
) {

    var latestException: Exception? = null
        private set

    fun getTodaysData(date: String): CalendarData? {
        return calendarDao.findByDate(date)
    }

    fun getAllData(): LiveData<List<CalendarData>> {
        return calendarDao.getAll()
    }

    fun refreshCalendarData(): Boolean {
        val calendarData = try {
            remoteCalendarDataSource.getRemoteCalendarData()
        } catch (e: IOException) {
            Timber.e(e)
            latestException = e
            throw e
        } ?: return false

        calendarDao.insertAll(*calendarData.toTypedArray())

        return true
    }

}