package com.nibmz7gmail.sgprayertimemusollah.core.data.mosque

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nibmz7gmail.sgprayertimemusollah.core.AsyncScheduler
import com.nibmz7gmail.sgprayertimemusollah.core.data.CACHED_LOCATION
import com.nibmz7gmail.sgprayertimemusollah.core.model.Mosque
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

enum class NearbyError{
    PERMISSION,
    NULL
}

@Singleton
class NearbyMosqueLocator @Inject constructor(
    private val mosqueDataSource: MosqueDataSource
) {

    private var mosqueList: List<Mosque>? = null

    private val _nearbyMosques = MutableLiveData<List<Mosque>>()
    val nearbyMosques: LiveData<List<Mosque>>
        get() = _nearbyMosques

    fun onLocationChanged(newLocation: Location) {
        if(newLocation.provider == CACHED_LOCATION && mosqueList != null) {
            Timber.i("Updating mosque list form cache")
            _nearbyMosques.value = mosqueList
            return
        }
        Timber.i("Updating mosque list")
        //Sort list
        AsyncScheduler.execute {
            val nearbyList = mosqueList ?: getMosqueData()
            val mosqueLocation = Location("point B")

            nearbyList.forEach {
                mosqueLocation.latitude = it.latitude
                mosqueLocation.longitude = it.longitude
                it.distance = newLocation.distanceTo(mosqueLocation)
            }
            nearbyList.sortedBy { it.distance }
            Collections.sort(nearbyList, DistanceComparator())
            mosqueList = nearbyList
            _nearbyMosques.postValue(nearbyList)
        }
    }

    private fun getMosqueData(): List<Mosque> {
        return mosqueDataSource.getMosqueData()
    }

    private inner class DistanceComparator : Comparator<Mosque> {
        override fun compare(o1: Mosque, o2: Mosque): Int {
            return java.lang.Float.compare(o1.distance, o2.distance)
        }
    }

}