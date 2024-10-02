package com.example.freefoodfinder.models

import com.google.gson.annotations.SerializedName


data class Event (
    val id: Int?,
    val title: String,
    val description: String,
    val date: String,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val image: Any?
)