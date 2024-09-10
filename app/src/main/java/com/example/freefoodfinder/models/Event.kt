package com.example.freefoodfinder.models

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import java.sql.Time
import java.util.Date


data class Event (
    val id: Int?,
    val title: String,
    val description: String,
    val date: Date,
    val startTime: Time,
    val endTime: Time,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val image: Any?
)