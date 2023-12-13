package com.example.spacehub.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.spacehub.NotificationCardGroup
import com.example.spacehub.R
import com.example.spacehub.SpaceNotification
import com.example.spacehub.runNotificationsFetch
import com.example.spacehub.showDatePickerDialog
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun NotificationsList(navController: NavController) {
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var notificationType by remember { mutableStateOf("all") }
    var result by remember { mutableStateOf("") }
    var notificationsList by remember { mutableStateOf<List<SpaceNotification>?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val notificationTypeOptions = listOf("all", "FLR", "SEP", "CME", "IPS", "MPC", "GST", "RBE", "report")
    var expanded by remember { mutableStateOf(false) }

    var selectedNotificationType by remember { mutableStateOf("Select Notification Type") }

    // Create a boolean to track whether the buttons should be visible or not
    var buttonsVisible by remember { mutableStateOf(true) }

    // Track selected notification for the detailed dialog
    var selectedNotification by remember { mutableStateOf<SpaceNotification?>(null) }

    // Show detailed dialog when a card is clicked
    if (selectedNotification != null) {
        Dialog(onDismissRequest = { selectedNotification = null }) {
            DisplayDetailedInfo(notification = selectedNotification!!) {
                selectedNotification = null
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(painterResource(id = R.drawable.night_background), contentScale = ContentScale.FillBounds)
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Space Events", color = MaterialTheme.colorScheme.primary) },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Button to show Start Date DatePickerDialog
        Button(
            onClick = {
                showDatePickerDialog(context = context, initialDate = startDate ?: LocalDate.now()) {
                    startDate = it
                    // Reset end date if it is before the new start date
                    if (endDate != null && endDate!! < startDate!!) {
                        endDate = null
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Text(if (startDate != null) "Start Date: ${startDate.toString()}" else "Select Start Date")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to show End Date DatePickerDialog
        Button(
            onClick = {
                showDatePickerDialog(context = context, initialDate = endDate ?: LocalDate.now()) {
                    endDate = it
                    // Reset start date if it is after the new end date
                    if (startDate != null && endDate!! < startDate!!) {
                        startDate = null
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Text(if (endDate != null) "End Date: ${endDate.toString()}" else "Select End Date")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
                .background(color = MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.medium)
                .clickable { expanded = !expanded },
            contentAlignment = Alignment.Center
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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (startDate != null && endDate != null && endDate!! >= startDate!!) {
                    val apiKey = "PzCNkrm4dtZGYZS4EyBajyzbFROa4hbM109iqME5"

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

                    buttonsVisible = false
                } else {
                    result = "Please select both valid start and end dates"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Fetch Notifications Data")
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                    NotificationCardGroup(notifications = filteredNotifications, navController = navController) { clickedNotification ->
                        selectedNotification = clickedNotification
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(result)
    }
}

@Composable
fun DisplayDetailedInfo(notification: SpaceNotification, onClose: () -> Unit) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            val imageResource = com.example.spacehub.getImageResource(notification.messageType)
            imageResource?.let {
                Image(
                    painter = it,
                    contentDescription = null,
                    modifier = Modifier
                        .height(120.dp)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.background)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text("Event Type: ${notification.messageType}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))
            Text("Event ID: ${notification.messageID}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))
            Text("Issue Time: ${notification.messageIssueTime}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))
            Text("Description: ${notification.messageBody}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}

