package com.example.freefoodfinder.network

import com.example.freefoodfinder.models.Event
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String)

interface APIInterface {
    @Multipart
    @POST("events/")
    fun createEvent(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("date") date: RequestBody,
        @Part("start_time") startTime: RequestBody,
        @Part("end_time") endTime: RequestBody,
        @Part("location") location: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part image: MultipartBody.Part?
    ): Call<Event>

    @GET("events/{id}")
    fun getEvent(@Path("id") id: Int): Call<Event>

    @GET("events/")
    fun getEvents() : Call<List<Event>>

    @POST("auth-token/")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}