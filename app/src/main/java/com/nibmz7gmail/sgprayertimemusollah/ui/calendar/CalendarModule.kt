package com.nibmz7gmail.sgprayertimemusollah.ui.calendar

import androidx.lifecycle.ViewModel
import com.nibmz7gmail.sgprayertimemusollah.core.di.FragmentScoped
import com.nibmz7gmail.sgprayertimemusollah.core.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class CalendarModule {

	@FragmentScoped
	@ContributesAndroidInjector
	internal abstract fun contributeCalendarFragment(): CalendarFragment

	@Binds
	@IntoMap
	@ViewModelKey(CalendarViewModel::class)
	abstract fun bindCalendarModel(viewModel: CalendarViewModel): ViewModel

}