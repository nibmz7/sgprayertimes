package com.nibmz7gmail.sgprayertimemusollah.domain

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import com.nibmz7gmail.sgprayertimemusollah.core.data.calendar.CalendarDataRepository
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import javax.inject.Inject
import androidx.lifecycle.MutableLiveData
import com.nibmz7gmail.sgprayertimemusollah.core.AsyncScheduler
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import com.nibmz7gmail.sgprayertimemusollah.core.util.PrayerTimesUtils.getTodaysDate
import com.nibmz7gmail.sgprayertimemusollah.ui.widgets.*
import timber.log.Timber


enum class ErrorTypes{
    NETWORK,
    DATE
}

class LoadTodaysDataUseCase @Inject constructor(
    private val context: Context,
    private val calendarDataRepository: CalendarDataRepository
) {
    private var todaysDataCache: Result<CalendarData>? = null
    private val _liveData = MutableLiveData<Result<CalendarData>>()
    val liveData: LiveData<Result<CalendarData>> = _liveData

    // Prevents multiple consumers requesting data at the same time
    private val loadCacheLock = Any()

    operator fun invoke(isWidget: Boolean) {

        if(!cacheIsUptoDate()) {
            fetchNewData(isWidget)
        } else {
            if(!isWidget) _liveData.value = todaysDataCache
        }
    }

    fun setCacheToNull() {
        Timber.i("Cache cleared")
        synchronized(loadCacheLock){
            todaysDataCache = null
        }
    }

    fun getCachedDate(): Result<CalendarData>? {
        synchronized(loadCacheLock){
            return todaysDataCache
        }
    }

    fun cacheIsUptoDate(): Boolean {
        synchronized(loadCacheLock) {
            val currentDate = getTodaysDate()

            todaysDataCache?.let {
                if(it is Result.Success) {
                    if(it.data.date != currentDate) return false
                    Timber.i("Cache exists")
                    return true
                }
            }
            Timber.i("Cache is faulty or not up-to-date")
            return false
        }
    }

    private fun fetchNewData(isWidget: Boolean) {
        synchronized(loadCacheLock) {
            val currentDate = getTodaysDate()
            Timber.i("Today's date is $currentDate")

            todaysDataCache?.let {
                if (it is Result.Success && it.data.date == currentDate) {
                    Timber.i("Fetching new data but cache already exists so terminating")
                    return
                }
            }

            Timber.i("Fetching new data")

            AsyncScheduler.execute {
                val dataFromDb = calendarDataRepository.getTodaysData(currentDate)

                if (dataFromDb == null) {
                    Timber.i("Data doesn't exist in database...Fetching from url")
                    val success = calendarDataRepository.refreshCalendarData()

                    todaysDataCache = if (success) {
                        val newlyFetchedData = calendarDataRepository.getTodaysData(currentDate)

                        if(newlyFetchedData == null) {
                            Timber.e("Data fetched successfully but date not found")
                            Result.Error(ErrorTypes.DATE)
                        } else {
                            Timber.i("Cache successfully updated from url and saved locally")
                            Result.Success(newlyFetchedData)
                        }

                    } else {
                        Timber.e("Failed to fetch data from url")
                        Result.Error(ErrorTypes.NETWORK)
                    }
                } else {
                    Timber.i("Cache successfully updated from database")
                    todaysDataCache = Result.Success(dataFromDb)
                }

                AsyncScheduler.postToMainThread {
                    notifyObserver(isWidget)
                }
            }
        }
    }

    @MainThread
    fun notifyObserver(isWidget: Boolean) {
        Timber.i("Notifying observer -> IsWidget=$isWidget")
        if(isWidget) {
            notifyWidgets()
        } else {
            _liveData.value = todaysDataCache
        }
    }

    private fun notifyWidgets() {
        Timber.i("Updating widgets called")
        updateWidgets()
    }

    private fun updateWidgets() {
        refreshWidget(WidgetPrayerTimesSmall::class.java)
        refreshWidget(WidgetPrayerTimesLarge::class.java)
    }

    private fun refreshWidget(widgetClass: Class<out BaseWidget>) {
        val intentSync = Intent(context, widgetClass)
        intentSync.action =
            ACTION_AUTO_UPDATE
        val pendingSync = PendingIntent.getBroadcast(context, 0, intentSync, PendingIntent.FLAG_UPDATE_CURRENT)
        pendingSync.send()
    }


}