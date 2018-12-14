package com.nibmz7gmail.sgprayertimemusollah.ui.nearby

import androidx.lifecycle.ViewModel
import com.nibmz7gmail.sgprayertimemusollah.core.di.FragmentScoped
import com.nibmz7gmail.sgprayertimemusollah.core.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class NearbyModule {

	@FragmentScoped
	@ContributesAndroidInjector
	internal abstract fun contributePrayerTimesFragment(): NearbyFragment

	@Binds
	@IntoMap
	@ViewModelKey(NearbyViewModel::class)
	abstract fun bindMainViewModel(viewModel: NearbyViewModel): ViewModel



}