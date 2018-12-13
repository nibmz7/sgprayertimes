package com.nibmz7gmail.sgprayertimemusollah.ui.prayertimes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nibmz7gmail.sgprayertimemusollah.core.domain.LoadTodaysDataUseCase
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import javax.inject.Inject

class PrayerTimesViewModel @Inject constructor(
	private val loadTodaysDataUseCase: LoadTodaysDataUseCase
) : ViewModel() {

	val calendarDataObservable: LiveData<Result<CalendarData>> by lazy {
		loadTodaysDataUseCase.liveData
	}


	fun getPrayerTimes() {
		loadTodaysDataUseCase()
	}


}