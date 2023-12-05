// HomePage.kt
package com.example.spacehub.screen

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spacehub.R
import com.example.spacehub.ui.theme.SpaceHubTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomePage(navController: NavController) {
    val events = listOf("Event Details", "Notifications List")

    SpaceHubTheme {
        Scaffold(
            modifier = Modifier.background(Color.Black),
            topBar = {
                TopAppBar(
                    title = { Text("Space Hub", color = MaterialTheme.colorScheme.primary) },
                    actions = {
                        IconButton(onClick = { /* Handle info action */ }) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null)
                        }
                    }
                )
            }
        )
        {
            // Use LazyColumn with StarryBackground
            LazyColumn(
                modifier = Modifier
                    .paint(painterResource(id = R.drawable.night_background), contentScale = ContentScale.FillBounds)
                    .background(Color.Transparent)
                    .padding(top = 128.dp, start = 16.dp, end = 16.dp)
                    .fillMaxSize()
            ) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Display welcome message and app summary
                        Text(
                            text = "Welcome to Space Hub!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 64.dp)
                        )
                        Text(
                            text = "Explore space events and notifications.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
                items(events) { event ->
                    // Display options to navigate to Event Details or Notifications List
                    HomeOption(event, navController)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun HomeOption(option: String, navController: NavController) {
    // Display options to navigate to Event Details or Notifications List
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { navigateToScreen(option, navController) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Display option name
            Text(
                text = option,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.Start)
            )
        }
    }
}

fun navigateToScreen(option: String, navController: NavController) {
    // Navigate to the selected screen based on the option
    when (option) {
        "Event Details" -> navController.navigate("eventDetail")
        "Notifications List" -> navController.navigate("notificationsList")
        // Add more options if needed
    }
}
