package com.nibmz7gmail.sgprayertimemusollah.ui.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nibmz7gmail.sgprayertimemusollah.core.data.calendar.CalendarDataRepository
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import javax.inject.Inject

class CalendarViewModel @Inject constructor(
	private val calendarDataRepository: CalendarDataRepository
) : ViewModel() {

	val calendarDataObservable: LiveData<List<CalendarData>> by lazy {
		calendarDataRepository.getAllData()
	}

}