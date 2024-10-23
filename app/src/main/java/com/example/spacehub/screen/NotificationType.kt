package com.example.spacehub.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spacehub.NavScreens
import com.example.spacehub.R
import com.example.spacehub.SpaceNotification
import com.example.spacehub.models.SpaceWeatherViewModel
import com.example.spacehub.runNotificationsFetch
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationType(navController: NavController, viewModel: SpaceWeatherViewModel) {
    val notificationTypeOptions = listOf("FLR", "SEP", "CME", "GST", "IPS", "MPC", "RBE", "Report")
    val selectedTypes = remember { mutableStateListOf<String>() }
    var message by remember { mutableStateOf("") }

    // Access startDate and endDate from the ViewModel
    val startDate = viewModel.startDate?.toString() ?: "defaultStartDate"
    val endDate = viewModel.endDate?.toString() ?: "defaultEndDate"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Notification Types", color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(bottom = 16.dp) // Add bottom padding for the button
        ) {
            items(notificationTypeOptions) { option ->
                val isSelected = selectedTypes.contains(option)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isSelected) {
                                selectedTypes.remove(option)
                            } else {
                                selectedTypes.add(option)
                            }
                        }
                        .padding(8.dp),
//                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option,
                            color = if (isSelected) Color.White else Color.Black
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (isSelected) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_check), // Replace with your checkmark drawable
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (selectedTypes.isEmpty()) {
                            message = "Please select at least one notification type."
                        } else {
                            // Update the ViewModel with selected notification types
                            viewModel.selectedNotificationTypes = selectedTypes.toList()
                            viewModel.startDate = startDate // Store startDate in ViewModel
                            viewModel.endDate = endDate // Store endDate in ViewModel

                            // Navigate to the NotificationListV2 screen
                            navController.navigate(NavScreens.NotificationListV2.route)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Text("Fetch Notifications Data")
                }

                // Display message if no notification types are selected
                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
