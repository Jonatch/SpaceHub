package com.example.spacehub.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spacehub.models.SpaceWeatherViewModel
import showDateRangePickerDialog
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateRangeSelectionScreen(navController: NavController) {
    val spaceWeatherViewModel: SpaceWeatherViewModel = viewModel()

    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    var showDateRangePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Select Date Range", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // Button to trigger the date range picker dialog
        Button(onClick = { showDateRangePicker = true }) {
            val dateRangeText = if (startDate != null && endDate != null) {
                "Selected: $startDate to $endDate"
            } else {
                "Select Date Range"
            }
            Text(dateRangeText)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to confirm the date range and navigate
        Button(
            onClick = {
                if (startDate != null && endDate != null && endDate!! >= startDate!!) {
                    spaceWeatherViewModel.startDate = startDate.toString()
                    spaceWeatherViewModel.endDate = endDate.toString()

                    navController.navigate("notificationType")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirm Date Range")
        }
    }

    // Display the custom date range picker dialog when triggered
    if (showDateRangePicker) {
        showDateRangePickerDialog { selectedStartDate, selectedEndDate ->
            startDate = selectedStartDate
            endDate = selectedEndDate
            showDateRangePicker = false // Close the picker after selection
        }
    }
}
