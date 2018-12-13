package com.nibmz7gmail.sgprayertimemusollah.ui.prayertimes

import androidx.lifecycle.ViewModel
import com.nibmz7gmail.sgprayertimemusollah.core.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class PrayerTimesModule {

	@Binds
	@IntoMap
	@ViewModelKey(PrayerTimesViewModel::class)
	abstract fun bindMainViewModel(viewModel: PrayerTimesViewModel): ViewModel

}