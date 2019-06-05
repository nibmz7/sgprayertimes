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
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.Sensor.TYPE_MAGNETIC_FIELD
import android.hardware.SensorManager








enum class QiblaError{
    ACCURACY,
    NULL
}
const val COMPASS_UNSUPPORTED =5

//https://github.com/rhmkds/kiblat-android
class QiblaCompass @Inject constructor(
    context: Context
): SensorEventListener, LiveData<Result<FloatArray>>() {

    private var sensorManager: SensorManager? = null
    private lateinit var gsensor: Sensor
    private lateinit var msensor: Sensor

    private val mGravity = FloatArray(3)
    private val mGeomagnetic = FloatArray(3)
    private val R = FloatArray(9)
    private val I = FloatArray(9)

    private var azimuth: Float = 0.toFloat()
    private val azimuthFix: Float = 0.toFloat()

    private val target = Location("Kabba Loc")

    private var userLoc: Location? = null

    val alpha = 0.97f

    var currentAzimuth = 0.0f

    private val _accuracy = MutableLiveData<Int>()
    val accuracy: LiveData<Int>
        get() = _accuracy

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Timber.i("Accuracy changed: $accuracy")
        _accuracy.value = accuracy
    }

    override fun onSensorChanged(event: SensorEvent) {
       synchronized(this) {
           if (event.sensor.type == TYPE_ACCELEROMETER) {

               mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0]
               mGravity[1] = alpha * mGravity[1] + (1 - alpha) * event.values[1]
               mGravity[2] = alpha * mGravity[2] + (1 - alpha) * event.values[2]

           }

           if (event.sensor.type == TYPE_MAGNETIC_FIELD) {

               mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * event.values[0]
               mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * event.values[1]
               mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * event.values[2]

           }

           val success = SensorManager.getRotationMatrix(
               R, I, mGravity,
               mGeomagnetic
           )
           if (success) {
               val orientation = FloatArray(3)
               SensorManager.getOrientation(R, orientation)

               azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat() // orientation
               azimuth = (azimuth + azimuthFix + 360) % 360

               val bearingAngle = calculateBearingAngle(userLoc!!, target)

               value = Result.Success(floatArrayOf(bearingAngle - currentAzimuth, azimuth))
               currentAzimuth = azimuth
           }

       }
    }

    fun start(location: Location) {
        Timber.i("Compass started")
        userLoc = location
        sensorManager?.registerListener(this, gsensor,
            SensorManager.SENSOR_DELAY_GAME)
        sensorManager?.registerListener(this, msensor,
            SensorManager.SENSOR_DELAY_GAME)
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

    override fun onInactive() {
        super.onInactive()
        Timber.i("Compass Paused")
        sensorManager?.unregisterListener(this)
    }

    init {

        try {
            Timber.i("Compass created")
            target.latitude = 21.422487 //kaaba latitude setting
            target.longitude = 39.826206 //kaaba longitude setting
            sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
            gsensor = sensorManager!!.getDefaultSensor(TYPE_ACCELEROMETER)
            msensor = sensorManager!!.getDefaultSensor(TYPE_MAGNETIC_FIELD)
        } catch (e: Exception) {
            _accuracy.value = COMPASS_UNSUPPORTED
        }



    }

}