package com.nibmz7gmail.sgprayertimemusollah

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import com.nibmz7gmail.sgprayertimemusollah.ui.prayertimes.PrayerTimesViewModel
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject


class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var prayerTimesViewModel: PrayerTimesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prayerTimesViewModel = ViewModelProviders.of(this, viewModelFactory).get(PrayerTimesViewModel::class.java)

        prayerTimesViewModel.calendarDataObservable.observe(this, Observer(::handleUpdates))

        prayerTimesViewModel.getPrayerTimes()
    }

    fun handleUpdates(result: Result<CalendarData>) {
        when(result) {
            is Result.Success -> {
                Timber.e("Success!!!!!!!\n\n${result.data}")
                main_text.text = result.data.toString()
            }
            is Result.Error -> {
                Timber.e("Error ${result.errorType}")
            }

            is Result.Loading -> {
                Timber.e("Loading...")
            }
        }
    }
}
