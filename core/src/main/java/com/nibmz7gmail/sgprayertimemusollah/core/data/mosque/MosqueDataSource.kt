package com.nibmz7gmail.sgprayertimemusollah.core.data.mosque

import android.content.Context
import com.nibmz7gmail.sgprayertimemusollah.core.R
import com.nibmz7gmail.sgprayertimemusollah.core.model.Mosque
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

class MosqueDataSource @Inject constructor(private val context: Context) {

    fun getMosqueData(): List<Mosque> {

        val jsonString = context.resources.openRawResource(R.raw.masjids)
            .bufferedReader().use { it.readText() }

        val mosques = ArrayList<Mosque>()
        try {

            val jArray = JSONArray(jsonString)

            // Extract data from json and store into ArrayList as class objects
            (0 until jArray.length())
                .asSequence()
                .map { jArray.getJSONObject(it) }
                .mapTo(mosques) { getMosque(it) }
        } catch (e: RuntimeException) {
            Timber.e(e, "Error parsing mosque data")

        }

        return mosques

    }

    private fun getMosque(json_data: JSONObject): Mosque {
        return Mosque(
            id = json_data.getInt("A"),
            name = json_data.getString("B"),
            address = json_data.getString("C"),
            fbPage = json_data.getString("D"),
            wcFriendly = json_data.getInt("E"),
            latitude = json_data.getDouble("F"),
            longitude = json_data.getDouble("G"),
            tel = json_data.getString("H")
        )
    }

}