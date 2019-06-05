package com.nibmz7gmail.sgprayertimemusollah

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.nibmz7gmail.sgprayertimemusollah.core.util.inTransaction
import com.nibmz7gmail.sgprayertimemusollah.ui.prayertimes.PrayerTimesFragment
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import com.nibmz7gmail.sgprayertimemusollah.ui.calendar.CalendarFragment
import com.nibmz7gmail.sgprayertimemusollah.ui.nearby.NearbyFragment
import com.nibmz7gmail.sgprayertimemusollah.ui.qibla.QiblaFragment
import com.nibmz7gmail.sgprayertimemusollah.ui.dialogs.MenuFragment
import timber.log.Timber


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

        setSupportActionBar(bar)

        fab_qibla.setOnClickListener {
            addFragment(QiblaFragment(), TRANSIT_FRAGMENT_OPEN)
        }

        if (savedInstanceState == null) {
            replaceFragment(PrayerTimesFragment(), TRANSIT_FRAGMENT_FADE)
        } else {
            // Find the current fragment
            Timber.i("Activity recreated and fragment found!")
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

    private fun <F> addFragment(newFragment: F, transition: Int) where F : Fragment, F : MainNavigationFragment {
        if(currentFragment.javaClass == newFragment.javaClass) {
            Timber.i("Adding same fragment again. WHAT FOR?")
            return
        }

        supportFragmentManager.inTransaction {
            setTransition( transition )
            supportFragmentManager.findFragmentByTag(FIRST_FRAGMENT)?.let {
                if(!currentFragment.onBackPressed()) {
                    Timber.i("Hiding First fragment even if it's hidden")
                    hide(it)
                }
            }
            if(currentFragment.onBackPressed()) {
                supportFragmentManager.findFragmentByTag(SECOND_FRAGMENT)?.let {
                    Timber.i("Adding new second fragment so removing current second fragment")
                    remove(it)
                }
            }
            currentFragment = newFragment
            add(FRAGMENT_ID, newFragment, SECOND_FRAGMENT)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            android.R.id.home -> {
                val bottomMenuFragment = MenuFragment()
                bottomMenuFragment.show(supportFragmentManager, bottomMenuFragment.tag)
                return true
            }

            R.id.nearby -> {
                addFragment(NearbyFragment(), TRANSIT_FRAGMENT_OPEN)
                return true
            }

            R.id.calendar -> {
                addFragment(CalendarFragment(), TRANSIT_FRAGMENT_OPEN)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
