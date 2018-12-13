package com.nibmz7gmail.sgprayertimemusollah.core.model

data class Mosque(
    val id: Int,
    val name: String,
    val address: String,
    val fbPage: String,
    val latitude: Double,
    val longitude: Double,
    val tel: String,
    val wcFriendly: Boolean
)
