package com.nibmz7gmail.sgprayertimemusollah.core.data.calendar.remote

import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import com.nibmz7gmail.sgprayertimemusollah.core.model.HijriDate
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

//https://grokonez.com/kotlin/kotlin-read-write-csv-file-example

//Date,Day,Subuh,Syuruk,Zohor,Asar,Maghrib,Isyak,dayH,monthH,yearH,eventH
//19/12/2018,2,537,701,104,428,704,819,11,3,1440,---
object CSVDataParser {

    fun parseCalendar(inputStream: InputStream): List<CalendarData> {
        val calendar = ArrayList<CalendarData>()
        val csvReader = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        // Read CSV header
        csvReader.readLine()

        // Read the file line by line starting from the second line
        line = csvReader.readLine()
        while (line != null) {
            val tokens = line.split(",")
            if (tokens.isNotEmpty()) {

                val prayerTimes = mutableListOf<String>()
                prayerTimes.add(tokens[2].addSpace())
                prayerTimes.add(tokens[4].addSpace())
                prayerTimes.add(tokens[5].addSpace())
                prayerTimes.add(tokens[6].addSpace())
                prayerTimes.add(tokens[7].addSpace())
                prayerTimes.add(tokens[3].addSpace())

                val hijriDate = HijriDate(
                    dayH = tokens[8].toInt(),
                    monthH = tokens[9].toInt(),
                    yearH = tokens[10].toInt(),
                    eventH = tokens[11]
                )

                val calendarData = CalendarData(
                    date = tokens[0],
                    day = tokens[1].toInt(),
                    prayerTimes = prayerTimes,
                    hijriDate = hijriDate

                )

                calendar.add(calendarData)
            }

            line = csvReader.readLine()
        }

        return calendar
    }

    private fun String.addSpace(): String {
        val idx = length - 2
        val str = StringBuilder(this)
        return str.insert(idx, ' ').toString()
    }

}