package com.nibmz7gmail.sgprayertimemusollah.core.data.mosque

import android.content.Context
import com.nibmz7gmail.sgprayertimemusollah.core.R
import com.nibmz7gmail.sgprayertimemusollah.core.model.Mosque
import com.nibmz7gmail.sgprayertimemusollah.core.util.toListItems
import timber.log.Timber
import javax.inject.Inject

class MosqueDataSource @Inject constructor(private val context: Context) {

    fun getMosqueData(): List<Mosque> {

        val jsonString = context.resources.openRawResource(R.raw.masjids)
            .bufferedReader().use { it.readText() }

        val parsedData = try {
            jsonString.toListItems<Mosque>()
        } catch (e: RuntimeException) {
            Timber.e(e, "Error parsing mosque data")
            null
        }

        val mosques: MutableList<Mosque> = mutableListOf()
        parsedData?.forEach {
            mosques.add(it)
        }

        return mosques

    }

}