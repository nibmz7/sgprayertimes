package com.nibmz7gmail.sgprayertimemusollah.core.domain

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
    private val calendarDataRepository: CalendarDataRepository
) {
    private var todaysDataCache: CalendarData? = null
    private val _liveData = MutableLiveData<Result<CalendarData>>()
    val liveData: LiveData<Result<CalendarData>> = _liveData

    // Prevents multiple consumers requesting data at the same time
    private val loadCacheLock = Any()

    operator fun invoke() {

        if(!cacheIsUptoDate()) {
            fetchNewData()
        }

    }

    fun getCachedDate(): CalendarData? {
        synchronized(loadCacheLock){
            return todaysDataCache
        }
    }

    fun cacheIsUptoDate(): Boolean {
        synchronized(loadCacheLock) {
            val currentDate = currentDate()

            todaysDataCache?.let {
                if(it.date != currentDate) return@let
                _liveData.value = Result.Success(it)
                Timber.i("Cache exists")
                return true
            }
            Timber.i("Cache is faulty or not up-to-date")
            return false
        }
    }

    private fun fetchNewData() {
        synchronized(loadCacheLock) {
            val currentDate = currentDate()

            todaysDataCache?.let {
                if (it.date == currentDate) {
                    Timber.i("Fetching new data but cache already exists so terminating")
                    return
                }
            }

            Timber.i("Fetching new data")

            AsyncScheduler.execute {
                val dataFromDb = calendarDataRepository.getTodaysData(currentDate)

                if (dataFromDb == null) {
                    Timber.i("Data doesn't exist in database...Fetching from url")
                    _liveData.postValue(Result.Loading)

                    val success = calendarDataRepository.refreshCalendarData()

                    if (success) {
                        val newlyFetchedData = calendarDataRepository.getTodaysData(currentDate)

                        if(newlyFetchedData == null) {
                            Timber.e("Data fetched successfully but date not found")
                            _liveData.postValue(Result.Error(ErrorTypes.DATE))
                        } else {
                            Timber.i("Cache successfully updated from url and saved locally")
                            _liveData.postValue(Result.Success(newlyFetchedData))
                            todaysDataCache = newlyFetchedData
                        }

                    } else {
                        Timber.e("Failed to fetch data from url")
                        _liveData.postValue(Result.Error(ErrorTypes.NETWORK))
                    }
                } else {
                    Timber.i("Cache successfully updated from database")
                    _liveData.postValue(Result.Success(dataFromDb))
                    todaysDataCache = dataFromDb
                }
            }
        }
    }

    fun notiftWidgets() {
//        val appWidgetManager = AppWidgetManager.getInstance(context)
//        val appWidgetIds = appWidgetManager.getAppWidgetIds(
//            ComponentName(context, WidgetProvider::class.java)
//        )
//        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listview)
    }

    private fun currentDate(): String = Calendar.getInstance().time.toString("dd/M/yyyy")

}