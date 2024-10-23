package com.example.spacehub.models

//import com.example.spacehub.network.SpaceWeatherAPI
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


class SpaceWeatherViewModel : ViewModel() {
    var startDate: String? by mutableStateOf(null)
    var endDate: String? by mutableStateOf(null)
    var selectedNotificationTypes: List<String> = emptyList()

}