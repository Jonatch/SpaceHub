// NotificationCard.kt
package com.example.spacehub

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationCardGroup(
    notifications: List<SpaceNotification>,
    navController: NavController,
    onClick: (SpaceNotification) -> Unit
) {
    Column {
        for (notification in notifications) {
            NotificationCard(notification = notification, navController = navController) {
                // Call the onClick lambda when the card is clicked
                onClick(notification)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCard(
    notification: SpaceNotification,
    navController: NavController,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = onClick // Set the onClick listener for the card
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
