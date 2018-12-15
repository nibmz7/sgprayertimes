package com.nibmz7gmail.sgprayertimemusollah.core.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.nibmz7gmail.sgprayertimemusollah.core.data.LocationLiveData
import com.nibmz7gmail.sgprayertimemusollah.core.data.calendar.CalendarDataRepository
import com.nibmz7gmail.sgprayertimemusollah.core.data.calendar.local.CalendarDao
import com.nibmz7gmail.sgprayertimemusollah.core.data.calendar.local.CalendarRoomDatabase
import com.nibmz7gmail.sgprayertimemusollah.core.data.calendar.remote.RemoteCalendarDataSource
import com.nibmz7gmail.sgprayertimemusollah.core.data.mosque.MosqueDataSource
import com.nibmz7gmail.sgprayertimemusollah.core.data.mosque.NearbyMosqueLocator
import com.nibmz7gmail.sgprayertimemusollah.core.data.qiblafinder.QiblaCompass
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CoreModule {

    @Singleton
    @Provides
    fun provideDb(app: Application): CalendarRoomDatabase {
        return Room
            .databaseBuilder(app, CalendarRoomDatabase::class.java, "calendar_data.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideCalendarDao(db: CalendarRoomDatabase): CalendarDao {
        return db.calendarDao()
    }

    @Singleton
    @Provides
    fun provideRemoteCalendarDataSource(context: Context): RemoteCalendarDataSource {
        return RemoteCalendarDataSource(context)
    }

    @Singleton
    @Provides
    fun provideCalendarDataRepo(calendarDao: CalendarDao, remoteCalendarDataSource: RemoteCalendarDataSource): CalendarDataRepository {
        return CalendarDataRepository(calendarDao, remoteCalendarDataSource)
    }

    @Singleton
    @Provides
    fun provideMosqueDataSource(context: Context): MosqueDataSource {
        return MosqueDataSource(context)
    }

    @Singleton
    @Provides
    fun provideNearbyMosqueLocator(mosqueDataSource: MosqueDataSource): NearbyMosqueLocator {
        return NearbyMosqueLocator(mosqueDataSource)
    }

    @Singleton
    @Provides
    fun provideQiblaCompass(context: Context): QiblaCompass {
        return QiblaCompass(context)
    }

    @Singleton
    @Provides
    fun provideLocationLiveData(context: Context): LocationLiveData {
        return LocationLiveData(context, 100)
    }
}