package com.nibmz7gmail.sgprayertimemusollah.ui.qibla

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.RotateAnimation
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nibmz7gmail.sgprayertimemusollah.MainActivity
import com.nibmz7gmail.sgprayertimemusollah.MainNavigationFragment
import com.nibmz7gmail.sgprayertimemusollah.R
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import com.nibmz7gmail.sgprayertimemusollah.core.util.activityViewModelProvider
import com.nibmz7gmail.sgprayertimemusollah.ui.PermissionFragment
import com.nibmz7gmail.sgprayertimemusollah.ui.nearby.NearbyFragment
import com.nibmz7gmail.sgprayertimemusollah.ui.prayertimes.PrayerTimesViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_prayertimes.*
import kotlinx.android.synthetic.main.fragment_qibla.*
import timber.log.Timber
import javax.inject.Inject

class QiblaFragment : PermissionFragment(), MainNavigationFragment {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: QiblaViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_qibla, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showLoadingWithText("Loading...\nMake sure location is turned on")

        viewModel = activityViewModelProvider(viewModelFactory)

        viewModel.calendarDataObservable.observe(this, Observer {

            if(it is Result.Success){

                hideLoadingScreen()

                val rotateAnimation = RotateAnimation(
                    it.data[0],
                    it.data[1],
                    RELATIVE_TO_SELF, 0.5f,
                    RELATIVE_TO_SELF, 0.5f)
                rotateAnimation.duration = 210
                rotateAnimation.fillAfter = true

                needle.startAnimation(rotateAnimation)
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
            showLoadingWithText("Loading...Make sure location is turned on")
            viewModel.startLocationUpdates()
        }
    }

    override fun onBackPressed(): Boolean {
        return true
    }
}