package com.nibmz7gmail.sgprayertimemusollah.core.util

import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import timber.log.Timber
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


object PrayerTimesUtils {

    private const val DATE_PATTERN = "hh mm aa"

    private val DIFF_FILLERS = arrayOf(
        "Fajr prayer",
        "Syuruk",
        "Zuhr prayer",
        "Asr prayer",
        "Maghrib prayer",
        "Isya' prayer"
    )



    fun CalendarData.getActivePrayerTime(): Pair<Int, String> {
        var activeTime = 0
        var infoText = "No info text"
        try {
            val sdf = SimpleDateFormat(DATE_PATTERN, Locale.US)
            val currentTime = sdf.parse(sdf.format(Calendar.getInstance().time))
            val prayerTimes = arrayOf(this.prayerTimes[0], this.prayerTimes[5], this.prayerTimes[1], this.prayerTimes[2], this.prayerTimes[3], this.prayerTimes[4])

            val fajrTime = sdf.parsePrayerTime(prayerTimes[0], 0)
            if(!currentTime.before(fajrTime)) {
                activeTime += 1
                for (i in 0..4) {
                    val currentPrayerTime = sdf.parsePrayerTime(prayerTimes[i], i)
                    val nextPrayerTime = sdf.parsePrayerTime(prayerTimes[i + 1], i)
                    if (currentTime.after(currentPrayerTime) && currentTime.before(nextPrayerTime)) {
                        activeTime += 1
                        infoText = nextPrayerTime.timeDifference(activeTime, currentTime.time)
                        break
                    }
                    activeTime += 1
                }
            } else {
                infoText = if(activeTime == 7) "Time for Fajr prayer tomorrow: ${prayerTimes[0]}± am"
                else fajrTime.timeDifference(activeTime, currentTime.time)
            }
        } catch (e: Exception){
            Timber.e(e, "Error finding active prayer time")
        } finally {
            return Pair(activeTime, infoText)
        }

    }

    private fun Date.timeDifference(idx: Int, time2: Long): String{
        val millse = this.time - time2
        val mills = Math.abs(millse)

        val hrs = (mills / (1000 * 60 * 60)).toInt()
        val mins = (mills / (1000 * 60)).toInt() % 60

        val type = DIFF_FILLERS[idx-1]

        if(hrs == 0) return "Time until $type: $mins mins"
        if(hrs == 1) return "Time until $type: $hrs hour and $mins mins"
        return "Time until $type: $hrs hours and $mins mins"
    }


    private fun SimpleDateFormat.parsePrayerTime(prayerTime: String, idx: Int): Date {
        val suffix = if(idx < 2) "am" else "pm"
        val time = if(prayerTime.length < 5) "0$prayerTime" else prayerTime
        return this.parse("$time $suffix")
    }




}