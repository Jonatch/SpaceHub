package com.example.spacehub.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowCompat
import com.example.spacehub.R

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF001F3F), // Dark Blue
    secondary = Color(0xFF003366), // Darker Blue
    tertiary = Color(0xFF9E7FFF)  // Nebula Purple
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF001F3F), // Dark Blue
    secondary = Color(0xFF003366), // Darker Blue
    tertiary = Color(0xFF9E7FFF)  // Nebula Purple
)

// In SpaceHubTheme file
object SpaceHubColorPalette {
    val SpaceBackground = Color(0xFF000080)
    val StarColor = Color.White
}

// In the SpaceHubTheme file
@Composable
fun SpaceHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = SpaceHubColorPalette.SpaceBackground // Set background color
            ) {
                // Add starry background
                StarryBackground()
                content()
            }
        }
    )
}


@Composable
fun StarryBackground() {
    val backgroundImage = painterResource(R.drawable.night_background) // Replace with your image resource

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

