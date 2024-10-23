package com.example.spacehub

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spacehub.models.SpaceWeatherViewModel
import com.example.spacehub.screen.DateRangeSelectionScreen
import com.example.spacehub.screen.EventDetailScreen
import com.example.spacehub.screen.HomePage
import com.example.spacehub.screen.NotificationListV2
import com.example.spacehub.screen.NotificationType
import com.example.spacehub.screen.NotificationsList

//sealed class NavScreens(val route: String) {
//    object Home: NavScreens(route = "Home")
//    object Info: NavScreens(route = "Info")
//    object Event: NavScreens(route = "Event")
//    object DateRangeSelection: NavScreens(route = "DateRangeSelection")
//    object NotificationType : NavScreens(route = "NotificationType")
//    object NotificationListV2: NavScreens(route = "notificationListV2/{selectedTypes}/{startDate}/{endDate}")
//}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
sealed class NavScreens(val route: String) {
    object Home: NavScreens(route = "Home")
    object Info: NavScreens(route = "Info")
    object Event: NavScreens(route = "Event")
    object DateRangeSelection: NavScreens(route = "DateRangeSelection")
    object NotificationType : NavScreens(route = "NotificationType")
    object NotificationListV2: NavScreens(route = "notificationListV2/{selectedTypes}/{startDate}/{endDate}")
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SpaceHubApp() {
    val navController = rememberNavController()
    val viewModel: SpaceWeatherViewModel = viewModel() // Get the ViewModel instance
    var drawerState by remember { mutableStateOf(false) } // State to manage drawer visibility

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("SpaceHub") },
                navigationIcon = {
                    IconButton(onClick = { drawerState = !drawerState }) {
                        Icon(painter = painterResource(id = R.drawable.baseline_menu_24), contentDescription = "Menu")
                    }
                }
            )
        }
    ) { paddingValue ->
        // Main content
        NavHost(navController, startDestination = NavScreens.Home.route) {
            composable(route = NavScreens.Home.route) {
                HomePage(navController = navController)
            }
            composable(route = NavScreens.Event.route) {
                NotificationsList(navController = navController)
            }
            composable(route = NavScreens.Info.route) {
                EventDetailScreen(navController = navController)
            }
            composable(route = NavScreens.DateRangeSelection.route) {
                DateRangeSelectionScreen(navController = navController) // Pass the NavController
            }
            composable(route = NavScreens.NotificationType.route) {
                NotificationType(navController = navController, viewModel = viewModel) // Pass the NavController
            }
            composable(route = NavScreens.NotificationListV2.route) {
                NotificationListV2(navController = navController, viewModel = viewModel)
            }
        }

        // Side menu
        if (drawerState) {
            SideMenu(navController) {
                drawerState = false // Close the drawer when an item is clicked
            }
        }
    }
}

@Composable
fun SideMenu(navController: NavController, onNavigate: () -> Unit) {
    // Simple side menu layout
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 8.dp
    ) {
        Column (Modifier.padding(top = 24.dp)) {
            Text(
                text = "Home",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate(NavScreens.Home.route)
                        onNavigate()
                    }
                    .padding(16.dp)
            )
            Text(
                text = "Event",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate(NavScreens.Event.route)
                        onNavigate()
                    }
                    .padding(16.dp)
            )
            Text(
                text = "Info",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate(NavScreens.Info.route)
                        onNavigate()
                    }
                    .padding(16.dp)
            )
            Text(
                text = "Event V2",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate(NavScreens.DateRangeSelection.route)
                        onNavigate()
                    }
                    .padding(16.dp)
            )
            // Add more items as needed
        }
    }
}