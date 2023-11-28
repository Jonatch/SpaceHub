// NotificationCard.kt
package com.example.spacehub

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NotificationCardGroup(notifications: List<SpaceNotification>) {
    Column {
        for (notification in notifications) {
            NotificationCard(notification = notification)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun NotificationCard(notification: SpaceNotification) {
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
