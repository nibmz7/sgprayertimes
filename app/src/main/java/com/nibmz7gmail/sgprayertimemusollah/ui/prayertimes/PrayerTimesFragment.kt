package com.nibmz7gmail.sgprayertimemusollah.ui.prayertimes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nibmz7gmail.sgprayertimemusollah.MainActivity
import com.nibmz7gmail.sgprayertimemusollah.MainNavigationFragment
import com.nibmz7gmail.sgprayertimemusollah.R
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import com.nibmz7gmail.sgprayertimemusollah.core.util.activityViewModelProvider
import com.nibmz7gmail.sgprayertimemusollah.ui.calendar.CalendarFragment
import com.nibmz7gmail.sgprayertimemusollah.ui.nearby.NearbyFragment
import com.nibmz7gmail.sgprayertimemusollah.ui.qibla.QiblaFragment
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_prayertimes.*
import timber.log.Timber
import javax.inject.Inject

class PrayerTimesFragment : DaggerFragment(), MainNavigationFragment {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: PrayerTimesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_prayertimes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activityViewModelProvider(viewModelFactory)

        viewModel.calendarDataObservable.observe(this, Observer(::handleUpdates))

        viewModel.getPrayerTimes()

    }

    private fun handleUpdates(result: Result<CalendarData>) {
        when(result) {
            is Result.Success -> {
                Timber.i(result.data.toString())
                distributeData(result.data)
            }
            is Result.Error -> {
                Timber.i("Error ${result.errorType}")
            }

            is Result.Loading -> {
                Timber.i("Loading...")
            }
        }
    }

    private fun distributeData(calendarData: CalendarData) {
        val viewGroup = root as ViewGroup
        for (i in 0 until viewGroup.childCount) {
            val cardView = viewGroup.getChildAt(i) as CardView
            cardView.findViewById<TextView>(R.id.text_prayer_type)?.apply {
                text = TIME_OF_DAY[i]
            }
            cardView.findViewById<TextView>(R.id.text_prayer_time)?.apply {
                text = calendarData.prayerTimes[i]
            }
            cardView.findViewById<ImageView>(R.id.img_prayer_time)?.apply {
                setImageResource(TIME_OF_DAY_IMG[i])
            }
        }

    }

    override fun onBackPressed(): Boolean {
        return false
    }

    companion object {
        val TIME_OF_DAY = arrayOf("Fajr", "Zuhr", "Asr", "Maghrib", "Isya'")
        val TIME_OF_DAY_IMG = arrayOf(R.drawable.prayer1, R.drawable.prayer2, R.drawable.prayer3, R.drawable.prayer4, R.drawable.prayer5)
    }
}