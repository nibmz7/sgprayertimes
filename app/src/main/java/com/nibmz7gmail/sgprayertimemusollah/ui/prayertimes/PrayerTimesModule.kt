package com.nibmz7gmail.sgprayertimemusollah.ui.prayertimes

import androidx.lifecycle.ViewModel
import com.nibmz7gmail.sgprayertimemusollah.core.di.FragmentScoped
import com.nibmz7gmail.sgprayertimemusollah.core.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class PrayerTimesModule {

	@FragmentScoped
	@ContributesAndroidInjector
	internal abstract fun contributePrayerTimesFragment(): PrayerTimesFragment

	@Binds
	@IntoMap
	@ViewModelKey(PrayerTimesViewModel::class)
	abstract fun bindMainViewModel(viewModel: PrayerTimesViewModel): ViewModel

}