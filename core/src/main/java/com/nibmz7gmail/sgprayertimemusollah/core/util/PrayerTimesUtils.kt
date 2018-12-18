package com.nibmz7gmail.sgprayertimemusollah.core.util

import com.nibmz7gmail.sgprayertimemusollah.core.R
import com.nibmz7gmail.sgprayertimemusollah.core.model.CalendarData
import com.nibmz7gmail.sgprayertimemusollah.core.util.PrayerTimesUtils.timeDifference
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

    val TIME_OF_DAY = arrayOf("Fajr", "Zuhr", "Asr", "Maghrib", "Isya'")

    private val islamicMonths = arrayOf("Muharram", "Safar", "Rabiul-Awwal", "Rabi-uthani", "Jumadi-ul-Awwal", "Jumadi-uthani", "Rajab", "Sha’ban", "Ramadan", "Shawwal", "Zhul-Q’ada", "Zhul-Hijja")

    fun getTodaysDate(): String = Calendar.getInstance().time.toString("dd/M/yyyy")


    fun CalendarData.getActivePrayerTime(): Pair<Int, String> {
        var activeTime = 1
        var infoText = "No info text"
        try {
            val sdf = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
            val currentTime = sdf.parse(sdf.format(Calendar.getInstance().time))
            val fajrTime = sdf.parsePrayerTime(prayerTimes[0], 0)

            val list = mutableListOf<String>()
            list.add(prayerTimes[0])
            list.add(prayerTimes[5])
            list.add(prayerTimes[1])
            list.add(prayerTimes[2])
            list.add(prayerTimes[3])
            list.add(prayerTimes[4])

            if(!currentTime.before(fajrTime)) {

                for (i in 0..4) {
                    val currentPrayerTime = sdf.parsePrayerTime(list[i], i)
                    val nextPrayerTime = sdf.parsePrayerTime(list[i + 1], i + 1)
                    Timber.i("$currentPrayerTime  $nextPrayerTime\n")
                    if (currentTime.isBetween(currentPrayerTime, nextPrayerTime)) {
                        activeTime += 1
                        infoText = nextPrayerTime.timeDifference(activeTime, currentTime.time)
                        break
                    }
                    if(i == 4) activeTime +=1
                    activeTime += 1
                    infoText = "Time for Fajr prayer tomorrow: ${prayerTimes[0]}± am"
                }

            } else {
                infoText = fajrTime.timeDifference(activeTime, currentTime.time)
            }
        } catch (e: Exception){
            Timber.e(e, "Error finding active prayer time")
        } finally {
            Timber.i("$activeTime")
            return Pair(activeTime, infoText)
        }

    }

    fun Date.isBetween(date1: Date, date2: Date): Boolean {
        return after(date1) && before(date2)
    }

    fun Int.toTimeIdx(): Int {
        return if(this >= 3) this - 3 else 0
    }

    private fun Date.timeDifference(idx: Int, time2: Long): String{
        val millse = this.time - time2
        val mills = Math.abs(millse)

        val hrs = (mills / (1000 * 60 * 60)).toInt()
        val mins = (mills / (1000 * 60)).toInt() % 60

        val type = DIFF_FILLERS[idx - 1]

        if(hrs == 0) return "Time until $type: $mins mins"
        if(hrs == 1) return "Time until $type: $hrs hour and $mins mins"
        return "Time until $type: $hrs hours and $mins mins"
    }

    fun CalendarData.toHijriDate(): String {
        return hijriDate.dayH.toString() + " " + islamicMonths[hijriDate.monthH] + " " + hijriDate.yearH + "H"
    }

    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val mdformat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())
        return mdformat.format(calendar.time)
    }

    fun CalendarData.toBeautifiedDate() : String {
        val dfInput = SimpleDateFormat("dd/M/yyyy", java.util.Locale.getDefault())
        val dfOutput = SimpleDateFormat("EEEE, dd MMM yyyy", java.util.Locale.getDefault())
        val parsedDate = dfInput.parse(this.date)
        return dfOutput.format(parsedDate)
    }

    private fun SimpleDateFormat.parsePrayerTime(prayerTime: String, idx: Int): Date {
        val suffix = if(idx <= 1) "am" else "pm"
        val time = if(prayerTime.length < 5) "0$prayerTime" else prayerTime
        return this.parse("$time $suffix")
    }




}