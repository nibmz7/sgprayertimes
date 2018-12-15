package com.nibmz7gmail.sgprayertimemusollah.domain

import android.content.Context
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import com.nibmz7gmail.sgprayertimemusollah.core.data.calendar.CalendarDataRepository
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import javax.inject.Inject
import androidx.lifecycle.MutableLiveData
import com.nibmz7gmail.sgprayertimemusollah.core.AsyncScheduler
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import com.nibmz7gmail.sgprayertimemusollah.core.util.toString
import timber.log.Timber
import java.util.*

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

        if(!cacheIsUptoDate(isWidget)) {
            fetchNewData(isWidget)
        }

    }

    fun getCachedDate(): Result<CalendarData>? {
        synchronized(loadCacheLock){
            return todaysDataCache
        }
    }

    private fun cacheIsUptoDate(isWidget: Boolean): Boolean {
        synchronized(loadCacheLock) {
            val currentDate = currentDate()

            todaysDataCache?.let {
                if(it is Result.Success) {
                    if(it.data.date != currentDate) return@let
                    notifyObserver(isWidget)
                }
                Timber.i("Cache exists")
                return true
            }
            Timber.i("Cache is faulty or not up-to-date")
            return false
        }
    }

    private fun fetchNewData(isWidget: Boolean) {
        synchronized(loadCacheLock) {
            val currentDate = currentDate()

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

        } else {
            _liveData.value = todaysDataCache
        }
    }

    fun notiftWidgets() {
//        val appWidgetManager = AppWidgetManager.getInstance(context)
//        val appWidgetIds = appWidgetManager.getAppWidgetIds(
//            ComponentName(context, WidgetProvider::class.java)
//        )
//        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listview)
        //context.sendBroadcast
    }

    private fun currentDate(): String = Calendar.getInstance().time.toString("dd/M/yyyy")

}