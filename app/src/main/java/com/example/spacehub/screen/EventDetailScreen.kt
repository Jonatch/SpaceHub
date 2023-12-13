package com.example.spacehub.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spacehub.R
import com.example.spacehub.ui.theme.SpaceHubTheme

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EventDetailScreen(navController: NavController) {
    val eventNames = remember {
        listOf(
            "FLR",
            "SEP",
            "CME",
            "GST",
            "IPS",
            "MPC",
            "RBE",
            "Report"
        )
    }

    SpaceHubTheme {
        Scaffold(
            modifier = Modifier.background(Color.Black),
            topBar = {
                TopAppBar(
                    title = { Text("Space Events", color = MaterialTheme.colorScheme.primary) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Handle info action */ }) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null)
                        }
                    }
                )
            }
        )
        {
            LazyColumn(
                modifier = Modifier
                    .paint(painterResource(id = R.drawable.night_background), contentScale = ContentScale.FillBounds)
                    .background(Color.Transparent)
                    .padding(top = 72.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                    }
                }
                items(eventNames) { eventName ->
                    EventDetails(eventName) {
                        navController.navigate("eventDetail/$eventName")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun EventDetails(eventName: String, onClick: () -> Unit) {
    // Display details for a specific event
    val eventImage = getImageResource(eventName)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (eventImage != null) {
                Image(
                    painter = eventImage,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.Transparent)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display event name
            Text(
                text = eventName,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Display event description
            Text(
                text = getEventExplanation(eventName),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.Start)
            )
        }
    }
}


@Composable
fun getImageResource(eventName: String): Painter? {
    // Retrieve event image based on the event name
    val context = LocalContext.current
    val imageName = eventName.lowercase()

    return if (imageName.isNotEmpty()) {
        painterResource(id = context.resources.getIdentifier(imageName, "drawable", context.packageName))
    } else {
        null
    }
}

@Composable
fun getEventExplanation(eventName: String): String {
    return when (eventName) {
        "FLR" -> "A solar flare is a sudden, intense burst of energy on the Sun's surface, releasing a tremendous amount of radiation. Solar flares can impact radio communications and navigation systems."
        "SEP" -> "Solar energetic particles are high-energy charged particles ejected from the Sun during solar flares and CMEs. These particles can pose a threat to astronauts in space and affect satellite operations."
        "CME" -> "A coronal mass ejection is a massive burst of solar wind and magnetic fields rising above the solar corona or being released into space. It can cause geomagnetic storms on Earth and disrupt satellite and communication systems."
        "GST" -> "A geomagnetic storm occurs when the solar wind interacts with Earth's magnetic field. It can cause beautiful auroras in the polar regions but can also affect power grids and communication systems."
        "IPS" -> "An interplanetary shock is a disturbance in the solar wind that can lead to changes in the Earth's magnetosphere. These shocks are often associated with solar flares and CMEs."
        "MPC" -> "A magneto pause crossing occurs when a spacecraft passes through the boundary separating Earth's magnetosphere from the solar wind. It provides valuable data about the interactions between the solar wind and Earth's magnetic field."
        "report" -> "A report is a document of all the different space weather phenomenon that are being observed over a reporting period."
        "RBE" -> "Radiation belt enhancement refers to an increase in the concentration of energetic charged particles in Earth's radiation belts. This enhancement can be caused by various solar events and has implications for satellite operations."
        else -> "This is a brief explanation of the event."
    }
}
