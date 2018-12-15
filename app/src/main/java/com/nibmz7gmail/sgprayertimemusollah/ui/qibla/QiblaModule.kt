package com.nibmz7gmail.sgprayertimemusollah.ui.qibla

import androidx.lifecycle.ViewModel
import com.nibmz7gmail.sgprayertimemusollah.core.di.FragmentScoped
import com.nibmz7gmail.sgprayertimemusollah.core.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class QiblaModule {

	@FragmentScoped
	@ContributesAndroidInjector
	internal abstract fun contributeQiblaFragment(): QiblaFragment

	@Binds
	@IntoMap
	@ViewModelKey(QiblaViewModel::class)
	abstract fun bindQiblaViewModel(viewModel: QiblaViewModel): ViewModel



}