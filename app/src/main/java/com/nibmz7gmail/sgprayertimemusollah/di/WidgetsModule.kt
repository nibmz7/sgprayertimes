package com.nibmz7gmail.sgprayertimemusollah.di

import com.nibmz7gmail.sgprayertimemusollah.WidgetPrayerTimes
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class WidgetsModule {
    @ContributesAndroidInjector
    internal abstract fun contributesPrayerTimesWidget(): WidgetPrayerTimes
}