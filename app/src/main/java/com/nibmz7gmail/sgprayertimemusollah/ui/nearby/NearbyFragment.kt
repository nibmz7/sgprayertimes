package com.nibmz7gmail.sgprayertimemusollah.ui.nearby

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
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import com.nibmz7gmail.sgprayertimemusollah.core.model.Mosque
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import com.nibmz7gmail.sgprayertimemusollah.core.util.PrayerTimesUtils.getActivePrayerTime
import com.nibmz7gmail.sgprayertimemusollah.core.util.activityViewModelProvider
import com.nibmz7gmail.sgprayertimemusollah.domain.ErrorTypes
import com.nibmz7gmail.sgprayertimemusollah.ui.PermissionFragment
import com.nibmz7gmail.sgprayertimemusollah.ui.calendar.CalendarAdapter
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.android.synthetic.main.toolbar.*
import timber.log.Timber
import javax.inject.Inject

class NearbyFragment : PermissionFragment(), MainNavigationFragment {

    private val adapter = NearbyListAdapter()
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: NearbyViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back.setOnClickListener {
            requireActivity().onBackPressed()
        }

        title.text = "Nearby Masjids"

        recyclerview.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerview.adapter = adapter
        //Tied to activity lifecycle
        viewModel = activityViewModelProvider(viewModelFactory)

        viewModel.nearbyMosqueObservable.observe(this, Observer {
            if(it is Result.Success) {
                hideLoadingScreen()
                adapter.submitList(it.data)
            }
        })

        retry()

        recyclerview.addOnScrollListener( object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val isTrue = recyclerView.canScrollVertically(-1)
                toolbar.isSelected = isTrue
            }
        })
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