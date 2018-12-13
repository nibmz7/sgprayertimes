package com.nibmz7gmail.sgprayertimemusollah.core.model

import com.squareup.moshi.Json

data class HijriDate(
    @Json(name = "dH") val day: Int,
    @Json(name = "mH") val month: Int,
    @Json(name = "yH") val year: Int,
    @Json(name = "eH") val event: String
)