package com.example.spacehub

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.spacehub.ui.theme.SpaceHubTheme
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.time.LocalDate
import java.time.YearMonth

class MainActivity : ComponentActivity() {

    private val client = OkHttpClient()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpaceHubTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CMEEventList()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    fun CMEEventList() {
        var year by remember { mutableStateOf("") }
        var month by remember { mutableStateOf("") }
        var result by remember { mutableStateOf("Loading...") }

        val context = LocalContext.current
        val keyboardController = LocalSoftwareKeyboardController.current
        val coroutineScope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Year input
            TextField(
                value = year,
                onValueChange = { year = it },
                label = { Text("Enter Year") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Month input
            TextField(
                value = month,
                onValueChange = { month = it },
                label = { Text("Enter Month") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val apiKey = "PzCNkrm4dtZGYZS4EyBajyzbFROa4hbM109iqME5" // Replace with your actual API key
                    val startDate = "${year}-${month}-01"
                    val endDate = "${year}-${month}-${YearMonth.of(year.toInt(), month.toInt()).lengthOfMonth()}"

                    coroutineScope.launch {
                        try {
                            val cmeResultList = runCMEFetch(startDate, endDate, apiKey)
                            result = if (cmeResultList != null) {
                                processAndFormatCMEList(cmeResultList)
                            } else {
                                "Error fetching CME results"
                            }
                        } catch (e: Exception) {
                            result = "Error: ${e.message ?: "Unknown error"}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Fetch CME Data")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(result)
        }
    }

    data class CMEResult(
        val activityID: String,
        val catalog: String,
        val startTime: String,
        val sourceLocation: String?,
        val activeRegionNum: Int?,
        val link: String,
        val note: String,
        val instruments: List<Instrument>,
        val cmeAnalyses: List<CMEAnalysis>,
        val linkedEvents: List<Any>?
    )

    data class Instrument(
        val displayName: String
    )

    data class CMEAnalysis(
        val time21_5: String,
        val latitude: Double,
        val longitude: Double?,
        val halfAngle: Double,
        val speed: Double,
        val type: String,
        val isMostAccurate: Boolean,
        val note: String,
        val levelOfData: Int,
        val link: String,
        val enlilList: List<Any>?
    )

    private suspend fun runCMEFetch(startDate: String, endDate: String, apiKey: String): List<CMEResult>? =
        withContext(Dispatchers.IO) {
            val url = "https://api.nasa.gov/DONKI/CME?startDate=$startDate&endDate=$endDate&api_key=$apiKey"
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
                    return@withContext Gson().fromJson(jsonString, Array<CMEResult>::class.java).toList()
                }
            }
        }

    private fun processAndFormatCMEList(cmeResultList: List<CMEResult>?): String {
        val resultStringBuilder = StringBuilder()

        if (cmeResultList != null) {
            for (cmeResult in cmeResultList) {
                val processedData = processCMEData(cmeResult.cmeAnalyses)
                resultStringBuilder.append("Event Details:\n")
                resultStringBuilder.append("Time: ${cmeResult.startTime}\n")
                resultStringBuilder.append("Location: ${cmeResult.sourceLocation ?: "N/A"}\n")
                resultStringBuilder.append("Note: ${cmeResult.note}\n\n")
                resultStringBuilder.append("CME Analysis Data:\n")
                resultStringBuilder.append(processedData)
                resultStringBuilder.append("\n\n")
            }
        }

        return resultStringBuilder.toString()
    }

    private fun processCMEData(cmeAnalyses: List<CMEAnalysis>): String {
        val processedDataList = mutableListOf<String>()

        for (cmeAnalysis in cmeAnalyses) {
            val whenValue = cmeAnalysis.time21_5
            val latitude = cmeAnalysis.latitude
            val longitude = cmeAnalysis.longitude ?: Double.NaN
            val note = cmeAnalysis.note

            val entry = "When: $whenValue, Where: Latitude $latitude, Longitude ${
                if (longitude.isNaN()) "N/A" else longitude.toString()
            }, Note: $note"

            processedDataList.add(entry)
        }

        return processedDataList.joinToString("\n")
    }
}



