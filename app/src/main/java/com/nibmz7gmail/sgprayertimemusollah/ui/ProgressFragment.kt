package com.nibmz7gmail.sgprayertimemusollah.ui

import android.os.Bundle
import android.view.View
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.progress_layout.*

abstract class ProgressFragment: DaggerFragment(){

    private val visible = View.VISIBLE
    private val gone = View.GONE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        retryButton.setOnClickListener { retry() }
    }

    fun showLoading() {
        infoScreen(gone)
        loadingBar.visibility = visible
        progress_screen.visibility = visible
    }

    fun showError(message: String) {
        info_message.text = message
        infoScreen(visible)
        loadingBar.visibility = gone
        progress_screen.visibility = visible
    }

    fun hideLoadingScreen() {
        progress_screen.visibility = gone
        loadingBar.visibility = gone
        infoScreen(gone)
    }

    fun showLoadingWithText(msg: String) {
        info_message.text = msg
        info_message.visibility = visible
        loadingBar.visibility = visible
        retryButton.visibility = gone
        progress_screen.visibility = visible
    }

    abstract fun retry()

    private fun infoScreen(visibility: Int) {
        info_message.visibility = visibility
        retryButton.visibility = visibility
    }

}