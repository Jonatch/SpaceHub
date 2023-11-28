// NotificationsList.kt
package com.example.spacehub

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun NotificationsList() {
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }
    var notificationType by remember { mutableStateOf("all") } // Default to "all"
    var result by remember { mutableStateOf("") }
    var notificationsList by remember { mutableStateOf<List<SpaceNotification>?>(null) }

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
                        // Update the function call to use SpaceNotification instead of Notification
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
