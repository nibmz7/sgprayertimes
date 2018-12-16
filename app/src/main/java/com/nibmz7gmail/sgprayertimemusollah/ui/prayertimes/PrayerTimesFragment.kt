package com.nibmz7gmail.sgprayertimemusollah.ui.prayertimes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nibmz7gmail.sgprayertimemusollah.MainNavigationFragment
import com.nibmz7gmail.sgprayertimemusollah.R
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import com.nibmz7gmail.sgprayertimemusollah.core.util.activityViewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_prayertimes.*
import timber.log.Timber
import com.nibmz7gmail.sgprayertimemusollah.core.util.PrayerTimesUtils.getActivePrayerTime
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
                val data = result.data.getActivePrayerTime()
                Timber.i("Active prayer time is $data")
                populateData(result.data, data)
            }
            is Result.Error -> {
                Timber.i("Error ${result.errorType}")
            }

            is Result.Loading -> {
                Timber.i("Loading...")
            }
        }
    }

    private fun populateData(calendarData: CalendarData, data: Pair<Int, String>) {
        val context = requireContext()
        val idx = if(data.first > 3) data.first - 3 else 0
        val color = if(data.first == 1 || data.first == 3) R.color.inactive else R.color.active
        val white = ContextCompat.getColor(requireContext(), R.color.white)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0, 0.3f
        )
        params.setMargins(dpToPixels(13, context).toInt(), 0, dpToPixels(13, context).toInt(), 0)


        for (i in 0 until list.childCount) {
            val cardView = list.getChildAt(i) as CardView

            if(idx == i) {
                cardView.apply {
                    layoutParams = params
                    cardElevation = dpToPixels(3, context)
                    setCardBackgroundColor(ContextCompat.getColor(context, color))
                }
            }

            cardView.findViewById<TextView>(R.id.text_prayer_type)?.apply {
                text = TIME_OF_DAY[i]
                if(idx == i) setTextColor(white)
            }
            cardView.findViewById<TextView>(R.id.text_prayer_time)?.apply {
                text = calendarData.prayerTimes[i]
                if(idx == i) setTextColor(white)
            }
            cardView.findViewById<ImageView>(R.id.img_prayer_time)?.apply {
                if(idx == i)setImageResource(TIME_OF_DAY_IMG_WHITE[i])
                else setImageResource(TIME_OF_DAY_IMG_WHITE[i])
            }
            if(idx == i) {
                cardView.findViewById<TextView>(R.id.text_time_left).apply {
                    text = data.second
                    setTextColor(white)
                    visibility = View.VISIBLE
                }
            }
        }

        list.visibility = View.VISIBLE
    }

    private fun dpToPixels(dp: Int, context: Context): Float {
        return dp * context.resources.displayMetrics.density
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    companion object {
        val TIME_OF_DAY = arrayOf("Fajr", "Zuhr", "Asr", "Maghrib", "Isya'")
        val TIME_OF_DAY_IMG = arrayOf(R.drawable.prayer1, R.drawable.prayer2, R.drawable.prayer3, R.drawable.prayer4, R.drawable.prayer5)
        val TIME_OF_DAY_IMG_WHITE = arrayOf(R.drawable.prayer1_white, R.drawable.prayer2_white, R.drawable.prayer3_white, R.drawable.prayer4_white, R.drawable.prayer5_white)
    }
}