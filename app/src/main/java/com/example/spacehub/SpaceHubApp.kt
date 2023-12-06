// SpaceHubApp.kt
package com.example.spacehub

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spacehub.models.SpaceWeatherViewModel
import com.example.spacehub.screen.EventDetailScreen
import com.example.spacehub.screen.HomePage
import com.example.spacehub.screen.NotificationsList


sealed class NavScreens(val route: String){
    object Home: NavScreens(route = "Home")
    object Info: NavScreens(route = "Info")
    object Event: NavScreens(route = "Event")
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpaceHubApp() {
    val navController = rememberNavController()
    val viewModel: SpaceWeatherViewModel = viewModel()
    var selectedTab by remember {
        mutableStateOf("")
    }
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_home_24),
                            contentDescription = ""
                        )
                    },
                    label = { Text("Home") },
                    selected = selectedTab == "Home",
                    onClick = {
                        selectedTab = "Home"
                        navController.navigate(NavScreens.Home.route)
                    }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_rocket_launch_24),
                            contentDescription = ""
                        )
                    },
                    label = { Text("Event") },
                    selected = selectedTab == "Event",
                    onClick = {
                        selectedTab = "Event"
                        navController.navigate(NavScreens.Event.route)
                    }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_info_24),
                            contentDescription = ""
                        )
                    },
                    label = { Text("Info") },
                    selected = selectedTab == "Info",
                    onClick = {
                        selectedTab = "Info"
                        navController.navigate(NavScreens.Info.route)
                    }
                )
            }
        }
    ) { paddingValue ->
        NavHost(navController, startDestination = "home") {
            composable(route = NavScreens.Home.route) {
                HomePage(navController = navController)
            }
            composable(route = NavScreens.Event.route) {
                NotificationsList(navController = navController)
            }
            composable(route = NavScreens.Info.route) {
                EventDetailScreen(navController = navController)
            }
        }
    }
}
