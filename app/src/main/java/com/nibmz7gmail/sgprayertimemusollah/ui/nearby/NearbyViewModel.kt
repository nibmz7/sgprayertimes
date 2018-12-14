package com.nibmz7gmail.sgprayertimemusollah.ui.nearby

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nibmz7gmail.sgprayertimemusollah.core.data.mosque.NearbyMosqueLocator
import com.nibmz7gmail.sgprayertimemusollah.core.domain.LoadTodaysDataUseCase
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import com.nibmz7gmail.sgprayertimemusollah.core.model.Mosque
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import javax.inject.Inject

class NearbyViewModel @Inject constructor(
	private val nearbyMosqueLocator: NearbyMosqueLocator
) : ViewModel() {

	val nearbyMosqueObservable: LiveData<Result<List<Mosque>>> by lazy {
		nearbyMosqueLocator.nearbyMosques
	}

	fun startSearching() {
		nearbyMosqueLocator.startLocationUpdates()
	}

	fun stopSearching() {
		nearbyMosqueLocator.stopLocationUpdates()
	}

}