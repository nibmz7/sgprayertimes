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
import android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
import android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
import android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
import android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS




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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val decorView = window.decorView
            decorView.systemUiVisibility = FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS or SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        setSupportActionBar(bar)

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            android.R.id.home -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

}
