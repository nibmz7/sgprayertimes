package com.nibmz7gmail.sgprayertimemusollah

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import com.nibmz7gmail.sgprayertimemusollah.core.result.Result
import com.nibmz7gmail.sgprayertimemusollah.core.util.inTransaction
import com.nibmz7gmail.sgprayertimemusollah.ui.prayertimes.PrayerTimesFragment
import com.nibmz7gmail.sgprayertimemusollah.ui.prayertimes.PrayerTimesViewModel
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject


class MainActivity : DaggerAppCompatActivity() {

    companion object {
        private const val FRAGMENT_ID = R.id.fragment_container
        private const val FIRST_FRAGMENT = "main_frag"
        private const val SECOND_FRAGMENT = "sec_frag"
    }

    private lateinit var currentFragment: MainNavigationFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (savedInstanceState == null) {
            replaceFragment(PrayerTimesFragment(), FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        } else {
            // Find the current fragment
            currentFragment =
                    supportFragmentManager.findFragmentById(FRAGMENT_ID) as? MainNavigationFragment
                    ?: throw IllegalStateException("Activity recreated, but no fragment found!")
        }
    }


    override fun onBackPressed() {
        if (!currentFragment.onBackPressed()) {
            super.onBackPressed()
        }else {
            removeSecondFragment()
        }
    }


    private fun <F> replaceFragment(fragment: F, transition: Int) where F : Fragment, F : MainNavigationFragment {
        supportFragmentManager.inTransaction {
            setTransition( transition )
            currentFragment = fragment
            replace(FRAGMENT_ID, fragment, FIRST_FRAGMENT)
        }
    }

    fun <F> addFragment(fragment: F, transition: Int) where F : Fragment, F : MainNavigationFragment {
        supportFragmentManager.inTransaction {
            setTransition( transition )
            supportFragmentManager.findFragmentByTag(FIRST_FRAGMENT)?.let {
                hide(it)
            }
            currentFragment = fragment
            add(FRAGMENT_ID, fragment, SECOND_FRAGMENT)
        }
    }

    private fun removeSecondFragment() {
        supportFragmentManager.inTransaction {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            supportFragmentManager.findFragmentByTag(SECOND_FRAGMENT)?.let { remove(it) }
            supportFragmentManager.findFragmentByTag(FIRST_FRAGMENT)?.let {
                currentFragment = it as MainNavigationFragment
                show(it)
            }
        }
    }

}
