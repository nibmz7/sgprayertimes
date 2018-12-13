package com.nibmz7gmail.sgprayertimemusollah.core.data.calendar.remote

import android.content.Context
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import com.nibmz7gmail.sgprayertimemusollah.core.util.isConnectedToInternet
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

interface RemoteDataSource {
    fun getRemoteCalendarData(): List<CalendarData>?
}

class RemoteCalendarDataSource @Inject constructor(
    private val context: Context
) : RemoteDataSource {

    override fun getRemoteCalendarData(): List<CalendarData>? {
        if (!context.isConnectedToInternet()) {
            Timber.e("Network not connected")
            return null
        }

        Timber.e("Trying to download data from network")
        val responseSource = try {
            CalendarDataDownloader(context).fetch()
        } catch (e: IOException) {
            Timber.e(e)
            throw e
        }
        val jsonData = responseSource.body()?.string() ?: return null

        val parsedData = try {
            CalendarDataJsonParser.parseConferenceData(jsonData)
        } catch (e: RuntimeException) {
            Timber.e(e, "Error parsing cached data")
            null
        }
        responseSource.close()
        return parsedData
    }

}
