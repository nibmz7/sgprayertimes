package com.nibmz7gmail.sgprayertimemusollah.ui.nearby

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import com.nibmz7gmail.sgprayertimemusollah.BuildConfig.APPLICATION_ID
import com.nibmz7gmail.sgprayertimemusollah.MainNavigationFragment
import com.nibmz7gmail.sgprayertimemusollah.R
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import com.nibmz7gmail.sgprayertimemusollah.core.util.activityViewModelProvider
import com.nibmz7gmail.sgprayertimemusollah.core.util.showToast
import com.nibmz7gmail.sgprayertimemusollah.ui.qibla.QiblaViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_nearby.*
import timber.log.Timber
import javax.inject.Inject

class NearbyFragment : DaggerFragment(), MainNavigationFragment {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: NearbyViewModel
    private var isSearching: Boolean = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_nearby, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Tied to activity lifecycle
        viewModel = activityViewModelProvider(viewModelFactory)

        viewModel.nearbyMosqueObservable.observe(this, Observer {

            if(it is Result.Success) {
                val sb = StringBuilder()
                it.data.forEach { mosque ->
                    sb.append("${mosque.name}\n${mosque.distance}\n")
                }
                textView.text = sb.toString()
            }
            else requireContext().showToast("Loading", Toast.LENGTH_SHORT)
        })

        if (!checkPermissions()) {
            startLocationPermissionRequest()
        }

    }

    private fun showSnackbar(
        snackStrId: Int,
        actionStrId: Int = 0,
        listener: View.OnClickListener? = null
    ) {
        val snackbar = Snackbar.make(requireActivity().findViewById(android.R.id.content), getString(snackStrId),
            LENGTH_INDEFINITE)
        if (actionStrId != 0 && listener != null) {
            snackbar.setAction(getString(actionStrId), listener)
        }
        snackbar.show()
    }

    private fun checkPermissions() =
        ActivityCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PERMISSION_GRANTED

    private fun startLocationPermissionRequest() {
        requestPermissions(arrayOf(ACCESS_FINE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Timber.i("onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {

                grantResults.isEmpty() -> Timber.i("User interaction was cancelled.")

                (grantResults[0] == PackageManager.PERMISSION_GRANTED) -> {
                    Timber.e("Permission granted")
                }

                else -> {
                    Timber.e("Permission denied")
                    showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        View.OnClickListener {
                            // Build intent that displays the App settings screen.
                            val intent = Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", APPLICATION_ID, null)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivity(intent)
                        })
                }
            }
        }
    }

    override fun onBackPressed(): Boolean {
        return true
    }

    companion object {
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }

}