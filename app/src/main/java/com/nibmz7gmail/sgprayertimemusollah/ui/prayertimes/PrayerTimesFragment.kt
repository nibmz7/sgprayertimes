package com.nibmz7gmail.sgprayertimemusollah.ui.prayertimes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nibmz7gmail.sgprayertimemusollah.MainActivity
import com.nibmz7gmail.sgprayertimemusollah.MainNavigationFragment
import com.nibmz7gmail.sgprayertimemusollah.R
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import com.nibmz7gmail.sgprayertimemusollah.core.util.activityViewModelProvider
import com.nibmz7gmail.sgprayertimemusollah.ui.nearby.NearbyFragment
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

        button.setOnClickListener {
            (requireActivity() as MainActivity).addFragment(NearbyFragment(), FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        }

    }

    private fun handleUpdates(result: Result<CalendarData>) {
        when(result) {
            is Result.Success -> {
                Timber.e("Success!!!!!!!\n\n${result.data}")
                textView.text = result.data.toString()
            }
            is Result.Error -> {
                Timber.e("Error ${result.errorType}")
            }

            is Result.Loading -> {
                Timber.e("Loading...")
            }
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}