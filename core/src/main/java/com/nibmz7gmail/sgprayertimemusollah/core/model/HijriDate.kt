package com.nibmz7gmail.sgprayertimemusollah.core.model

import com.squareup.moshi.Json

data class HijriDate(
    @Json(name = "dH") val dayH: Int,
    @Json(name = "mH") val monthH: Int,
    @Json(name = "yH") val yearH: Int,
    @Json(name = "eH") val eventH: String
)