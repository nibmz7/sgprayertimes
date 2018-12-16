package com.nibmz7gmail.sgprayertimemusollah

import com.nibmz7gmail.sgprayertimemusollah.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber

class MainApplication : DaggerApplication() {


	override fun onCreate() {
		super.onCreate()
		Timber.plant(Timber.DebugTree())
	}

	override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
		return DaggerAppComponent
			.builder()
			.create(this)
	}

}