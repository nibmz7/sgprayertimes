package com.nibmz7gmail.sgprayertimemusollah.core.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.*
import timber.log.Timber
import javax.inject.Inject

const val CACHED_LOCATION = "cache"

class LocationLiveData @Inject constructor(
    context: Context,
    val limiter: Int
) : LiveData<Location?>() {

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationRequest: LocationRequest = LocationRequest.create()
    private val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()

    var oldLocation: Location? = null

    override fun onInactive() {
        super.onInactive()
        stopLocationUpdates()
        value = null
    }

    override fun onActive() {
        super.onActive()
        oldLocation?.let {
            Timber.i("Updating location from cache")
            value = it
        }
        startLocationUpdates()
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in Location?>) {
        super.observe(owner, observer)
        if (hasActiveObservers()) {
            Timber.e("Multiple observers registered but only one will be notified of changes.")
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            if(locationResult == null) {
                value = null
            } else {
                val newLocation = locationResult.lastLocation
                if(oldLocation == null) {
                    setNewLocation(newLocation)
                }
                else {
                    val distance = oldLocation!!.distanceTo(newLocation)
                    if(distance > limiter) {
                        setNewLocation(newLocation)
                    }
                }
            }
        }
    }

    fun setNewLocation(newLocation: Location) {
        value = newLocation
        if(oldLocation == null) oldLocation = Location(CACHED_LOCATION)
        oldLocation?.latitude = newLocation.latitude
        oldLocation?.longitude = newLocation.longitude
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        Timber.i("Location updates started")
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            null /* Looper */)
    }

    private fun stopLocationUpdates() {
        Timber.i("Location updates stopped")
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    init {
        Timber.i("Location tracker created")
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        builder.addLocationRequest(locationRequest)
        builder.build()
    }

}
