package com.nibmz7gmail.sgprayertimemusollah.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nibmz7gmail.sgprayertimemusollah.MainNavigationFragment
import com.nibmz7gmail.sgprayertimemusollah.R
import com.nibmz7gmail.sgprayertimemusollah.core.util.PrayerTimesUtils.getTodaysDate
import com.nibmz7gmail.sgprayertimemusollah.core.util.activityViewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject

class CalendarFragment : DaggerFragment(), MainNavigationFragment {

    private val adapter = CalendarAdapter()
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: CalendarViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back.setOnClickListener {
            requireActivity().onBackPressed()
        }

        title.text = "Islamic Calendar"

        recyclerview.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerview.adapter = adapter

        viewModel = activityViewModelProvider(viewModelFactory)

        viewModel.calendarDataObservable.observe(this, Observer {
            val todaysDate = getTodaysDate()
            loadingBar.visibility = View.GONE
            adapter.submitList(it)
            val pos = it.indexOfFirst { data -> data.date == todaysDate }
            recyclerview.scrollToPosition(pos)
        })

        recyclerview.addOnScrollListener( object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val isTrue = recyclerView.canScrollVertically(-1)
                toolbar.isSelected = isTrue
                header.isSelected = isTrue
            }
        })

    }

    override fun onBackPressed(): Boolean {
        return true
    }
}