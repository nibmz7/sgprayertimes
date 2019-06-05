package com.nibmz7gmail.sgprayertimemusollah.di

import com.nibmz7gmail.sgprayertimemusollah.ui.widgets.WidgetPrayerTimesLarge
import com.nibmz7gmail.sgprayertimemusollah.ui.widgets.WidgetPrayerTimesSmall
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class WidgetsModule {
    @ContributesAndroidInjector
    internal abstract fun contributesPrayerTimesWidget(): WidgetPrayerTimesLarge

    @ContributesAndroidInjector
    internal abstract fun contributesPrayerTimesWidgetSmall(): WidgetPrayerTimesSmall
}