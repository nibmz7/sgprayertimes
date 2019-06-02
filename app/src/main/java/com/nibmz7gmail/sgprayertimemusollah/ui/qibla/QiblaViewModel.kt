package com.nibmz7gmail.sgprayertimemusollah.ui.qibla

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.nibmz7gmail.sgprayertimemusollah.core.data.LocationLiveData
import com.nibmz7gmail.sgprayertimemusollah.core.data.qiblafinder.COMPASS_UNSUPPORTED
import com.nibmz7gmail.sgprayertimemusollah.core.data.qiblafinder.QiblaCompass
import com.nibmz7gmail.sgprayertimemusollah.core.data.qiblafinder.QiblaError
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import javax.inject.Inject

class QiblaViewModel @Inject constructor(
	private val qiblaCompass: QiblaCompass,
    private val locationLiveData: LocationLiveData
) : ViewModel() {

    private val _calendarDataObservable = MediatorLiveData<Result<FloatArray>>()
	val calendarDataObservable: LiveData<Result<FloatArray>>
        get() = _calendarDataObservable

    val accuracyObservable: LiveData<Int>
        get() = qiblaCompass.accuracy

    init {
        _calendarDataObservable.addSource(locationLiveData) {
            if(it == null) {
                _calendarDataObservable.value = Result.Error(QiblaError.NULL)
            }
            else {
                if(qiblaCompass.accuracy.value != COMPASS_UNSUPPORTED)
                    qiblaCompass.start(it)
            }
        }
        _calendarDataObservable.addSource(qiblaCompass) {
            _calendarDataObservable.value = it
        }
    }

    fun startLocationUpdates() {
        locationLiveData.startLocationUpdates()
    }

}