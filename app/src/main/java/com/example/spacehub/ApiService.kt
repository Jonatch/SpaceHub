// ApiService.kt
package com.example.spacehub

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import com.example.spacehub.SpaceNotification


private val client = OkHttpClient()

suspend fun runNotificationsFetch(
    startDate: String,
    endDate: String,
    type: String,
    apiKey: String
): List<SpaceNotification>? =
    withContext(Dispatchers.IO) {
        val url = "https://api.nasa.gov/DONKI/notifications?" +
                "startDate=$startDate&endDate=$endDate&type=$type&api_key=$apiKey"
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                when (response.code) {
                    401 -> println("Unauthorized: Check API key")
                    403 -> println("Forbidden: API key may be invalid or has insufficient permissions")
                    else -> println("HTTP error code: ${response.code}")
                }
                return@withContext null
            } else {
                val jsonString = response.body?.string()
                return@withContext Gson().fromJson(jsonString, Array<SpaceNotification>::class.java).toList()
            }
        }
    }
