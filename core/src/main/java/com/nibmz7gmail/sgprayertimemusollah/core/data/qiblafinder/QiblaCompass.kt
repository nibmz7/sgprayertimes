package com.nibmz7gmail.sgprayertimemusollah.core.data.qiblafinder

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.*
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

enum class QiblaError{
    ACCURACY,
    NULL
}

//https://stackoverflow.com/questions/4308262/calculate-compass-bearing-heading-to-location-in-android/44249170
class QiblaCompass @Inject constructor(
    context: Context
): SensorEventListener, LiveData<Result<FloatArray>>() {

    private val sensorManager: SensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
    private val sensor: Sensor

    private val target = Location("Kabba Loc")

    private var userLoc: Location? = null

    // Prevents multiple consumers requesting data at the same time
    private val locationDataLock = Any()

    var currentDegree = 0.0f

    private val _accuracy = MutableLiveData<Int>()
    val accuracy: LiveData<Int>
        get() = _accuracy

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Timber.i("Accuracy changed: $accuracy")
        _accuracy.value = accuracy
    }

    override fun onSensorChanged(event: SensorEvent) {
        if(userLoc != null) calibrateCompass(event.values[0])
    }

    private fun calibrateCompass(someValue: Float) {

        val geoField = GeomagneticField(
            userLoc!!.latitude.toFloat(),
            userLoc!!.longitude.toFloat(),
            userLoc!!.altitude.toFloat(),
            System.currentTimeMillis())

        var head = Math.round(someValue).toFloat()
        var bearTo = calculateBearingAngle(userLoc!!, target)
        head -= geoField.declination

        if (bearTo < 0) {
            bearTo += 360
        }

        var direction = bearTo - head

        if (direction < 0) {
            direction += 360
        }


        val newValues = floatArrayOf(currentDegree, direction)
        value = Result.Success(newValues)
        currentDegree = direction

    }

    fun start(location: Location) {
        Timber.i("Compass started")
            userLoc = location

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME) //SensorManager.SENSOR_DELAY_Fastest
    }

    override fun onInactive() {
        super.onInactive()
        Timber.i("Compass Paused")
        sensorManager.unregisterListener(this)
    }

    private fun calculateBearingAngle(from: Location, to: Location): Float {

        val phi1 = Math.toRadians(from.latitude)
        val phi2 = Math.toRadians(to.latitude)
        val deltaLambda = Math.toRadians(to.longitude - from.longitude)
        val theta = atan2(
            sin(deltaLambda) * cos(phi2),
            cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(deltaLambda)
        )
        return Math.toDegrees(theta).toFloat()
    }

    init {
        Timber.i("Compass created")
        target.latitude = 21.422487 //kaaba latitude setting
        target.longitude = 39.826206 //kaaba longitude setting
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
    }

}