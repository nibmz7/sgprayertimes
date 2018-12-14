package com.nibmz7gmail.sgprayertimemusollah.core.data.mosque

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.nibmz7gmail.sgprayertimemusollah.core.AsyncScheduler
import com.nibmz7gmail.sgprayertimemusollah.core.model.Mosque
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

enum class LocationError{
    PERMISSION,
    NULL
}

@Singleton
class NearbyMosqueLocator @Inject constructor(
    context: Context,
    private val mosqueDataSource: MosqueDataSource
) {

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationRequest: LocationRequest = LocationRequest.create()
    private val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()

    private var mosqueList: List<Mosque>? = null

    private var oldLocation: Location? = null
    private var hasUpdated: Boolean = false

    private val _nearbyMosques = MutableLiveData<Result<List<Mosque>>>()
    val nearbyMosques: LiveData<Result<List<Mosque>>>
        get() = _nearbyMosques

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            if(locationResult == null) {
                _nearbyMosques.value = Result.Error(LocationError.NULL)
            } else {
                val newLocation = locationResult.lastLocation
                if(oldLocation == null) onNewLocationAvailable(newLocation)
                else {
                    val distance = oldLocation!!.distanceTo(newLocation)
                    if(distance > 300) {
                        onNewLocationAvailable(newLocation)
                        return
                    }
                    if(!hasUpdated) {
                        mosqueList?.let {
                            Timber.e("Updating from cache")
                            _nearbyMosques.value = Result.Success(it)
                            hasUpdated = true
                        }
                    }
                }
            }

        }
    }

    @Synchronized
    private fun onNewLocationAvailable(newLocation: Location) {
        Timber.e("New location available")
        //Sort list
        oldLocation = newLocation
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
            _nearbyMosques.postValue(Result.Success(nearbyList))
            mosqueList = nearbyList
            hasUpdated = true
        }
    }

    private fun getMosqueData(): List<Mosque> {
        return mosqueDataSource.getMosqueData()
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        Timber.e("Location updates started")
        _nearbyMosques.value = Result.Loading
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            null /* Looper */)
    }

    fun stopLocationUpdates() {
        Timber.e("Location updates stopped")
        fusedLocationClient.removeLocationUpdates(locationCallback)
        hasUpdated = false
    }

    private inner class DistanceComparator : Comparator<Mosque> {
        override fun compare(o1: Mosque, o2: Mosque): Int {
            return java.lang.Float.compare(o1.distance, o2.distance)
        }
    }

    init {
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        builder.addLocationRequest(locationRequest)
        builder.build()
    }


}