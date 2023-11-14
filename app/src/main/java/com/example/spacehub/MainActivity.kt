package com.example.spacehub

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import java.io.IOException
import java.time.LocalDate
import java.util.*

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
                    NotificationsList()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    fun NotificationsList() {
        var startDate by remember { mutableStateOf(LocalDate.now()) }
        var endDate by remember { mutableStateOf(LocalDate.now()) }
        var notificationType by remember { mutableStateOf("all") } // Default to "all"
        var result by remember { mutableStateOf("") }
        var notificationsList by remember { mutableStateOf<List<Notification>?>(null) }

        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        val notificationTypeOptions = listOf("all", "FLR", "SEP", "CME", "IPS", "MPC", "GST", "RBE", "report")
        var expanded by remember { mutableStateOf(false) }

        var selectedNotificationType by remember { mutableStateOf("Select Notification Type") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Button to show Start Date DatePickerDialog
            Button(
                onClick = {
                    showDatePickerDialog(context = context, initialDate = startDate) {
                        startDate = it
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Start Date")
            }

            // Display selected start date
            Text("Selected Start Date: ${startDate.toString()}")

            Spacer(modifier = Modifier.height(16.dp))

            // Button to show End Date DatePickerDialog
            Button(
                onClick = {
                    showDatePickerDialog(context = context, initialDate = endDate) {
                        endDate = it
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select End Date")
            }

            // Display selected end date
            Text("Selected End Date: ${endDate.toString()}")

            Spacer(modifier = Modifier.height(16.dp))

            // Notification Type dropdown
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(color = Color.Gray, shape = MaterialTheme.shapes.medium)
                    .clickable { expanded = !expanded }
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(selectedNotificationType, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    notificationTypeOptions.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(option)
                            },
                            onClick = {
                                notificationType = option
                                expanded = false
                                selectedNotificationType = "Selected Notification Type: $notificationType"
                            }
                        )
                    }
                }
            }

            // Display selected notification type
            Spacer(modifier = Modifier.height(16.dp))
            Text(selectedNotificationType, color = Color.White)

            Spacer(modifier = Modifier.height(16.dp))

            // Button click handling
            Button(
                onClick = {
                    val apiKey = "PzCNkrm4dtZGYZS4EyBajyzbFROa4hbM109iqME5" // Replace with your actual API key

                    coroutineScope.launch {
                        try {
                            notificationsList = runNotificationsFetch(startDate.toString(), endDate.toString(), notificationType, apiKey)
                            result = if (notificationsList != null) {
                                "Notifications fetched successfully"
                            } else {
                                "Error fetching notifications"
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
                Text("Fetch Notifications Data")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display notifications in a scrollable column
            notificationsList?.let { notifications ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    val uniqueMessageIds = notifications.map { it.messageID }.distinct()
                    for (messageId in uniqueMessageIds) {
                        val filteredNotifications = notifications.filter { it.messageID == messageId }
                        NotificationCardGroup(notifications = filteredNotifications)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Display result message
            Text(result)
        }
    }

    @Composable
    fun NotificationCardGroup(notifications: List<Notification>) {
        Column {
            for (notification in notifications) {
                NotificationCard(notification = notification)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    @Composable
    fun NotificationCard(notification: Notification) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text("Message ID: ${notification.messageID}")
                Text("Message Type: ${notification.messageType}")
                Text("Issue Time: ${notification.messageIssueTime}")
                Text("Message Body: ${notification.messageBody}")
                Text("Message URL: ${notification.messageURL}")
            }
        }
    }

    data class Notification(
        val messageID: String,
        val messageBody: String,
        val messageType: String,
        val messageIssueTime: String,
        val messageId: Int,
        val messageURL: String
    )

    private suspend fun runNotificationsFetch(startDate: String, endDate: String, type: String, apiKey: String): List<Notification>? =
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
                    return@withContext Gson().fromJson(jsonString, Array<Notification>::class.java).toList()
                }
            }
        }

    // Function to show DatePickerDialog
    @RequiresApi(Build.VERSION_CODES.O)
    fun showDatePickerDialog(
        context: Context,
        initialDate: LocalDate,
        onDateSelected: (LocalDate) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        calendar.set(initialDate.year, initialDate.monthValue - 1, initialDate.dayOfMonth)

        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, day ->
                val selectedDate = LocalDate.of(year, month + 1, day)
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }
}
