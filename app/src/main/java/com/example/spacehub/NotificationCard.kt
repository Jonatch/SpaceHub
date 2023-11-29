// NotificationCard.kt
package com.example.spacehub

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun NotificationCardGroup(notifications: List<SpaceNotification>) {
    Column {
        for (notification in notifications) {
            NotificationCard(notification = notification)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NotificationCard(notification: SpaceNotification) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                // Show detailed information when card is clicked
//                showNotificationDetailsDialog(notification = notification)
            },
    ) {
        // Display preview of the event
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            val imageResource = getImageResource(notification.messageType)
            imageResource?.let {
                Image(
                    painter = it,
                    contentDescription = null,
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text("Message Type: ${notification.messageType}", style = MaterialTheme.typography.bodyMedium)
            Text("Issue Time: ${notification.messageIssueTime}")
            Text("Click for more details", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun showNotificationDetailsDialog(notification: SpaceNotification) {
    val context = LocalContext.current

    // Show a Dialog with detailed information
    MaterialTheme {
        Dialog(
            onDismissRequest = { /* Handle dialog dismissal if needed */ },
            properties = DialogProperties(dismissOnClickOutside = true)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text("Message Type: ${notification.messageType}", style = MaterialTheme.typography.bodyMedium)
                Text("Issue Time: ${notification.messageIssueTime}")
                Text("Message ID: ${notification.messageId}")
                Text("Message Body: ${notification.messageBody}")
                Text("Message URL: ${notification.messageURL}")
            }
        }
    }
}

@Composable

fun getImageResource(messageType: String): Painter? {
    val context = LocalContext.current
    val imageName = messageType.lowercase()

    return if (imageName.isNotEmpty()) {
        painterResource(id = context.resources.getIdentifier(imageName, "drawable", context.packageName))
    } else {
        null
    }
}
