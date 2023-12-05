// SpaceHubApp.kt
package com.example.spacehub

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spacehub.models.SpaceWeatherViewModel
import com.example.spacehub.screen.EventDetailScreen
import com.example.spacehub.screen.HomePage
import com.example.spacehub.screen.NotificationsList

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SpaceHubApp() {
    val navController = rememberNavController()
    val viewModel: SpaceWeatherViewModel = viewModel()

    NavHost(navController, startDestination = "home") {
        composable("home") {
            HomePage(navController = navController)
        }
        composable("notificationsList") {
            NotificationsList(navController = navController)
        }
        composable("eventDetail") {
            EventDetailScreen(navController = navController)
        }
    }
}
