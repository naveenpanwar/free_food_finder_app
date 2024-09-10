package com.example.freefoodfinder.network

import com.example.freefoodfinder.models.Event
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String)

interface APIInterface {
    @POST("events")
    fun createEvent(@Body event: Event): Call<Event>

    @GET("event")
    fun getEvent(@Body event: Event): Call<Event>

    @GET("events")
    fun getEvents() : Call<List<Event>>

    @POST("auth-token/")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}