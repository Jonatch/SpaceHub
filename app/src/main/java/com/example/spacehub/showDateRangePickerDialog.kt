import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun showDateRangePickerDialog(
    onRangeSelected: (LocalDate, LocalDate) -> Unit
) {
    var showDialog by remember { mutableStateOf(true) }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Select Date Range") },
            text = {
                CalendarView(
                    startDate = startDate,
                    endDate = endDate,
                    onDateSelected = { date ->
                        if (startDate == null) {
                            startDate = date
                        } else if (endDate == null && date.isAfter(startDate)) {
                            endDate = date
                        }
                    }
                )
            },
            confirmButton = {
                if (startDate != null && endDate != null) {
                    Button(onClick = {
                        onRangeSelected(startDate!!, endDate!!)
                        showDialog = false
                    }) {
                        Text("Confirm")
                    }
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(
    startDate: LocalDate?,
    endDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = LocalDate.of(currentMonth.year, currentMonth.month, 1).dayOfWeek.value
    val totalCells = (daysInMonth + firstDayOfMonth - 1) // Total cells (including empty start cells)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Month navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Text("<", fontSize = 24.sp)
            }

            Text(
                text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + currentMonth.year,
                style = MaterialTheme.typography.titleLarge
            )

            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Text(">", fontSize = 24.sp)
            }
        }

        // Days of the week
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                Text(text = day, fontSize = 16.sp, modifier = Modifier.padding(8.dp))
            }
        }

        // Calendar grid
        Column {
            var dayCounter = 1
            for (week in 0 until 6) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    for (day in 0 until 7) {
                        if (week == 0 && day < firstDayOfMonth - 1 || dayCounter > daysInMonth) {
                            // Empty space before the first day or after the last day of the month
                            Spacer(modifier = Modifier.size(40.dp))
                        } else {
                            val date = LocalDate.of(currentMonth.year, currentMonth.month, dayCounter)

                            // Check if this date is part of the selected range
                            val isSelected = startDate != null && endDate != null && (date.isEqual(startDate) || date.isEqual(endDate) || (date.isAfter(startDate) && date.isBefore(endDate)))
                            val isStartDate = startDate != null && date.isEqual(startDate)
                            val isEndDate = endDate != null && date.isEqual(endDate)

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        color = when {
                                            isStartDate || isEndDate -> Color.Blue
                                            isSelected -> Color.LightGray
                                            else -> Color.Transparent
                                        },
                                        shape = CircleShape
                                    )
                                    .clickable { onDateSelected(date) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = dayCounter.toString(), fontSize = 16.sp)
                            }
                            dayCounter++
                        }
                    }
                }
            }
        }
    }
}
