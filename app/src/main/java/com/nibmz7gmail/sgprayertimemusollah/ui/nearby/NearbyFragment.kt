package com.nibmz7gmail.sgprayertimemusollah.ui.nearby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nibmz7gmail.sgprayertimemusollah.MainNavigationFragment
import com.nibmz7gmail.sgprayertimemusollah.R
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import com.nibmz7gmail.sgprayertimemusollah.core.model.Mosque
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import com.nibmz7gmail.sgprayertimemusollah.core.util.PrayerTimesUtils.getActivePrayerTime
import com.nibmz7gmail.sgprayertimemusollah.core.util.activityViewModelProvider
import com.nibmz7gmail.sgprayertimemusollah.domain.ErrorTypes
import com.nibmz7gmail.sgprayertimemusollah.ui.PermissionFragment
import kotlinx.android.synthetic.main.fragment_nearby.*
import timber.log.Timber
import javax.inject.Inject

class NearbyFragment : PermissionFragment(), MainNavigationFragment {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: NearbyViewModel


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
                hideLoadingScreen()
                val sb = StringBuilder()
                it.data.forEach { mosque ->
                    sb.append("${mosque.name}\n${mosque.distance}\n")
                }
                textView.text = sb.toString()
            }
        })

        retry()

    }

    override fun permissionGranted() {
        retry()
    }

    override fun retry() {
        if (!checkPermissions()) {
            showError("Location permission has not been granted")
            startLocationPermissionRequest()
        } else {
            showLoadingWithText("Loading...Make sure location is enabled")
            viewModel.startLocationUpdates()
        }
    }

    override fun onBackPressed(): Boolean {
        return true
    }

}