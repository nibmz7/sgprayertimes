package com.nibmz7gmail.sgprayertimemusollah

import android.content.BroadcastReceiver
import com.nibmz7gmail.sgprayertimemusollah.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasBroadcastReceiverInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber
import javax.inject.Inject



class MainApplication : DaggerApplication(), HasBroadcastReceiverInjector {

	@Inject
	lateinit var broadcastReceiverInjector: DispatchingAndroidInjector<BroadcastReceiver>

	override fun onCreate() {
		super.onCreate()
		Timber.plant(Timber.DebugTree())
	}

	override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
		return DaggerAppComponent
			.builder()
			.create(this)
	}

	override fun broadcastReceiverInjector(): DispatchingAndroidInjector<BroadcastReceiver>? {
		return broadcastReceiverInjector
	}


}