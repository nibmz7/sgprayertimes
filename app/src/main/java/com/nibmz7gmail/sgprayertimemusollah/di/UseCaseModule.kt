package com.nibmz7gmail.sgprayertimemusollah.di

import android.content.Context
import com.nibmz7gmail.sgprayertimemusollah.core.data.calendar.CalendarDataRepository
import com.nibmz7gmail.sgprayertimemusollah.domain.LoadTodaysDataUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UseCaseModule {

    @Singleton
    @Provides
    fun provideGetTodaysDataUseCase(context: Context, calendarDataRepository: CalendarDataRepository): LoadTodaysDataUseCase {
        return LoadTodaysDataUseCase(context, calendarDataRepository)
    }

}