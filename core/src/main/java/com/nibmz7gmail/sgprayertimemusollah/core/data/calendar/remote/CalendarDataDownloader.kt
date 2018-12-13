package com.nibmz7gmail.sgprayertimemusollah.core.data.calendar.remote

import android.content.Context
import androidx.annotation.WorkerThread
import com.nibmz7gmail.sgprayertimemusollah.core.BuildConfig
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.io.IOException

class CalendarDataDownloader(
    private val context: Context
){
    private val client: OkHttpClient by lazy {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = HttpLoggingInterceptor.Level.BASIC

        val protocols = arrayListOf(Protocol.HTTP_1_1, Protocol.HTTP_2) // Support h2

        val cacheSize = 2L * 1024 * 1024 // 2 MiB
        val cacheDir = context.getDir("conference_data", Context.MODE_PRIVATE)
        val cache = Cache(cacheDir, cacheSize)

        OkHttpClient.Builder()
            .protocols(protocols)
            .cache(cache)
            .addInterceptor(logInterceptor)
            .build()
    }

    @Throws(IOException::class)
    @WorkerThread
    fun fetch(): Response {

        val url = BuildConfig.CALENDAR_DATA_URL

        Timber.d("Download started from: $url")

        val httpBuilder = HttpUrl.parse(url)?.newBuilder()
            ?: throw IllegalArgumentException("Malformed Session data URL")

        val request = Request.Builder()
            .url(httpBuilder.build())
            .cacheControl(CacheControl.FORCE_NETWORK)
            .build()

        // Blocking call
        val response = client.newCall(request).execute()

        Timber.d("Downloaded bytes: ${response.body()?.contentLength() ?: 0}")

        return response ?: throw IOException("Network error")
    }
}