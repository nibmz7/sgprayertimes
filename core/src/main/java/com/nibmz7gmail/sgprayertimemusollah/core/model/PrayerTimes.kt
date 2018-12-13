package com.nibmz7gmail.sgprayertimemusollah.core.model

import com.squareup.moshi.Json

data class PrayerTimes(
    @Json(name = "fP") val fajr: String,
    @Json(name = "sP") val syuruk: String,
    @Json(name = "zP") val zuhr: String,
    @Json(name = "aP") val asr: String,
    @Json(name = "mP") val maghrib: String,
    @Json(name = "iP") val isyak: String
)
