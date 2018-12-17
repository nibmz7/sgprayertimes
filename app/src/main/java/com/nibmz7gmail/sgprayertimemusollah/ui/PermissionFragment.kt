package com.nibmz7gmail.sgprayertimemusollah.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import com.google.android.material.snackbar.Snackbar
import com.nibmz7gmail.sgprayertimemusollah.BuildConfig
import com.nibmz7gmail.sgprayertimemusollah.R
import com.nibmz7gmail.sgprayertimemusollah.ui.nearby.NearbyFragment
import timber.log.Timber

//https://github.com/googlesamples/android-play-location
abstract class PermissionFragment: ProgressFragment() {

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    fun checkPermissions() =
        ActivityCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PermissionChecker.PERMISSION_GRANTED

    fun startLocationPermissionRequest() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun showSnackbar(snackStrId: Int, actionStrId: Int = 0, listener: View.OnClickListener? = null) {
        val snackbar = Snackbar.make(requireActivity().findViewById(android.R.id.content), getString(snackStrId),
            Snackbar.LENGTH_INDEFINITE
        )
        if (actionStrId != 0 && listener != null) {
            snackbar.duration = 5000
            snackbar.setAction(getString(actionStrId), listener)
        }
        snackbar.show()
    }

    abstract fun permissionGranted()


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Timber.i("onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {

                grantResults.isEmpty() -> Timber.i("User interaction was cancelled.")

                (grantResults[0] == PackageManager.PERMISSION_GRANTED) -> {
                    Timber.i("Permission granted")
                    permissionGranted()
                }

                else -> {
                    Timber.i("Permission denied")
                    showSnackbar(
                        R.string.permission_denied_explanation, R.string.settings,
                        View.OnClickListener {
                            // Build intent that displays the App settings screen.
                            val intent = Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivity(intent)
                        })
                }
            }
        }
    }
}