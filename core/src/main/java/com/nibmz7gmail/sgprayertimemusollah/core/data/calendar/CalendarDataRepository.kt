package com.nibmz7gmail.sgprayertimemusollah.core.data.calendar

import androidx.lifecycle.LiveData
import com.nibmz7gmail.sgprayertimemusollah.core.data.calendar.local.CalendarDao
import com.nibmz7gmail.sgprayertimemusollah.core.data.calendar.remote.RemoteDataSource
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

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

        calendarDao.deleteAll()
        calendarDao.insertAll(*calendarData.toTypedArray())

        return true
    }

}