package com.nibmz7gmail.sgprayertimemusollah.ui.prayertimes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import com.nibmz7gmail.sgprayertimemusollah.domain.LoadTodaysDataUseCase
import javax.inject.Inject

class PrayerTimesViewModel @Inject constructor(
	private val loadTodaysDataUseCase: LoadTodaysDataUseCase
) : ViewModel() {

	private val _calendarDataObservable = MediatorLiveData<Result<CalendarData>>()
	val calendarDataObservable: LiveData<Result<CalendarData>>
		get() = _calendarDataObservable

	fun getPrayerTimes() {
		_calendarDataObservable.value = Result.Loading
		loadTodaysDataUseCase(false)
	}

	init {
	    _calendarDataObservable.addSource(loadTodaysDataUseCase.liveData) {
			_calendarDataObservable.value = it
		}
	}

}