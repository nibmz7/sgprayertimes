package com.nibmz7gmail.sgprayertimemusollah.ui.nearby

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.nibmz7gmail.sgprayertimemusollah.core.data.LocationLiveData
import com.nibmz7gmail.sgprayertimemusollah.core.data.mosque.NearbyError
import com.nibmz7gmail.sgprayertimemusollah.core.data.mosque.NearbyMosqueLocator
import com.nibmz7gmail.sgprayertimemusollah.core.model.Mosque
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import javax.inject.Inject

class NearbyViewModel @Inject constructor(
	private val nearbyMosqueLocator: NearbyMosqueLocator,
	private val locationLiveData: LocationLiveData
) : ViewModel() {

	private val _nearbyMosqueObservable = MediatorLiveData<Result<List<Mosque>>>()
	val nearbyMosqueObservable: LiveData<Result<List<Mosque>>>
		get() = _nearbyMosqueObservable


	init {
		_nearbyMosqueObservable.addSource(locationLiveData) {
			if(it == null) {
				_nearbyMosqueObservable.value = Result.Error(NearbyError.NULL)
			}
			else {
				nearbyMosqueLocator.onLocationChanged(it)
			}
		}
		_nearbyMosqueObservable.addSource(nearbyMosqueLocator.nearbyMosques) {
			_nearbyMosqueObservable.value = Result.Success(it)
		}
	}

	fun startLocationUpdates() {
		locationLiveData.startLocationUpdates()
	}


}