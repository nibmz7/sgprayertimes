package com.nibmz7gmail.sgprayertimemusollah.core.model

import com.squareup.moshi.Json

data class Mosque(
    @Json(name = "A") val id: Int,
    @Json(name = "B") val name: String,
    @Json(name = "C") val address: String,
    @Json(name = "D") val fbPage: String,
    @Json(name = "E") val wcFriendly: Int,
    @Json(name = "F") val latitude: Double,
    @Json(name = "G") val longitude: Double,
    @Json(name = "H") val tel: String,
    @Transient var distance: Float = 0f
)
