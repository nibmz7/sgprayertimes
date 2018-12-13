package com.nibmz7gmail.sgprayertimemusollah.core.domain

import androidx.lifecycle.LiveData
import com.nibmz7gmail.sgprayertimemusollah.core.data.calendar.CalendarDataRepository
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import javax.inject.Inject
import android.content.Context
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
    private var todaysDataCache: CalendarData? = null
    private val _liveData = MutableLiveData<Result<CalendarData>>()
    val liveData: LiveData<Result<CalendarData>> = _liveData

    operator fun invoke() {
        val currentDate = currentDate()

        todaysDataCache?.let {
            if(it.date != currentDate) return@let
            _liveData.value = Result.Success(it)
            return
        }

        fetchNewData(currentDate)

    }

    operator fun invoke(isWidget: Boolean): CalendarData? {
        val currentDate = currentDate()

        todaysDataCache?.let {
            if(it.date == currentDate) return it
        }

        fetchNewData(currentDate)

        return null
    }

    @Synchronized
    fun fetchNewData(currentDate: String) {
        todaysDataCache?.let {
            if(it.date == currentDate) return
        }

        Timber.e("Fetching new data")

        AsyncScheduler.execute {
            todaysDataCache = calendarDataRepository.getTodaysData(currentDate)

            if(todaysDataCache == null) {

                _liveData.postValue(Result.Loading)

                val success = calendarDataRepository.refreshCalendarData()

                if(success) {
                    todaysDataCache = calendarDataRepository.getTodaysData(currentDate)

                    todaysDataCache?.let{
                        _liveData.postValue(Result.Success(it))
                        notiftWidgets()
                    } ?: run {
                        _liveData.postValue(Result.Error(ErrorTypes.DATE))
                    }


                } else {
                    _liveData.postValue(Result.Error(ErrorTypes.NETWORK))
                }
            } else {
                todaysDataCache?.let{
                    _liveData.postValue(Result.Success(it))
                    notiftWidgets()
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

    fun currentDate(): String = Calendar.getInstance().time.toString("dd/M/yyyy")

}