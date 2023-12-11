// ApiService.kt
package com.example.spacehub

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("DONKI/notifications")
    suspend fun getNotifications(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("type") type: String,
        @Query("api_key") apiKey: String
    ): List<SpaceNotification>?
}

object ApiClient {
    private const val BASE_URL = "https://api.nasa.gov/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

suspend fun runNotificationsFetch(
    startDate: String,
    endDate: String,
    type: String,
    apiKey: String
): List<SpaceNotification>? {
    return try {
        ApiClient.apiService.getNotifications(startDate, endDate, type, apiKey)
    } catch (e: Exception) {
        println("Error: ${e.message ?: "Unknown error"}")
        null
    }
}