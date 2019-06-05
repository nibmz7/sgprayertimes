package com.nibmz7gmail.sgprayertimemusollah.ui.qibla

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.RotateAnimation
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nibmz7gmail.sgprayertimemusollah.MainNavigationFragment
import com.nibmz7gmail.sgprayertimemusollah.R
import com.nibmz7gmail.sgprayertimemusollah.core.data.qiblafinder.COMPASS_UNSUPPORTED
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import com.nibmz7gmail.sgprayertimemusollah.core.util.activityViewModelProvider
import com.nibmz7gmail.sgprayertimemusollah.ui.PermissionFragment
import kotlinx.android.synthetic.main.fragment_qibla.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject


class QiblaFragment : PermissionFragment(), MainNavigationFragment {

    private val accuracies = arrayOf("Unreliable", "Low", "Medium", "High", "Unknown")
    private val colors = arrayOf("#FF5722", "#FF9800", "#8BC34A", "#4CAF50", "#7f8c8d")

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: QiblaViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_qibla, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back.setOnClickListener {
            requireActivity().onBackPressed()
        }

        title.text = "Qibla Compass"

        showLoadingWithText(getString(R.string.loading_info))

        viewModel = activityViewModelProvider(viewModelFactory)

        displayAccuracy(4)

        viewModel.calendarDataObservable.observe(this, Observer {

            if(it is Result.Success){

                hideLoadingScreen()

                val rotateAnimation = RotateAnimation(
                    it.data[0],
                    it.data[1],
                    RELATIVE_TO_SELF, 0.5f,
                    RELATIVE_TO_SELF, 0.5f)
                rotateAnimation.duration = 210
                rotateAnimation.repeatCount = 0
                rotateAnimation.fillAfter = true

                needle.startAnimation(rotateAnimation)

            }
        })

        viewModel.accuracyObservable.observe(this, Observer {
            if(it == COMPASS_UNSUPPORTED) {
                showMessage(getString(R.string.compass_unsupprted_message))
            } else {
                displayAccuracy(it)
            }
        })

        retry()

    }

    private fun displayAccuracy(i: Int) {
        val accuracy = accuracies[i]
        val color = colors[i]
        val message = "Start navigating with the compass while the phone is held horizontally.<br><br>Compass accuracy is : <font color=$color>$accuracy</font><br><br>\nTo improve accuracy, shake your phone vigorously."
        message_box.text = Html.fromHtml(message)
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