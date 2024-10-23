package com.example.spacehub.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spacehub.SpaceNotification
import com.example.spacehub.models.SpaceWeatherViewModel
import com.example.spacehub.runNotificationsFetch
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationListV2(navController: NavController, viewModel: SpaceWeatherViewModel) {
    val selectedTypes by remember { mutableStateOf(viewModel.selectedNotificationTypes) }
    val startDate by remember { mutableStateOf(viewModel.startDate) }
    val endDate by remember { mutableStateOf(viewModel.endDate) }

    var isLoading by remember { mutableStateOf(true) }
    var notificationsList by remember { mutableStateOf<List<SpaceNotification>?>(null) }
    var resultMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Fetch the notifications when the screen is loaded
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            resultMessage = "Fetching notifications..."
            notificationsList = startDate?.let { endDate?.let { it1 ->
                fetchNotifications(selectedTypes, it,
                    it1
                )
            } }

            if (notificationsList.isNullOrEmpty()) {
                resultMessage = "No notifications found."
            } else {
                resultMessage = "Notifications fetched successfully."
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            // Show loading animation
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Show the notifications
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                notificationsList?.let { notifications ->
                    items(notifications) { notification ->
                        NotificationItem(notification)
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(resultMessage, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: SpaceNotification) {
    // Use Card to contain notification content
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Add elevation for better separation
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title of the notification
            Text(
                text = notification.messageID,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp) // Add spacing below the title
            )

            // Timestamp of the notification
            Text(
                text = notification.messageIssueTime,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp) // Add spacing below the timestamp
            )

            // Message body of the notification
            Text(
                text = notification.messageBody,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp) // Add spacing below the message body
            )
        }
    }
}


// This function simulates fetching notifications, replace with actual API call
suspend fun fetchNotifications(selectedTypes: List<String>, startDate: String, endDate: String): List<SpaceNotification>? {
    val apiKey = "PzCNkrm4dtZGYZS4EyBajyzbFROa4hbM109iqME5" // Replace with your actual API key
    val fetchedNotifications = mutableListOf<SpaceNotification>()

    try {
        for (type in selectedTypes) {
            // Call the runNotificationsFetch for each selected type
            val notifications = runNotificationsFetch(startDate, endDate, type, apiKey)
            notifications?.let { fetchedNotifications.addAll(it) }
        }
    } catch (e: Exception) {
        println("Error fetching notifications: ${e.message}")
        return null
    }

    return if (fetchedNotifications.isNotEmpty()) fetchedNotifications else null
}
