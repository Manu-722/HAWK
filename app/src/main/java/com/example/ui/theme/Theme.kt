package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = White,
    onPrimary = Black,
    secondary = SleekSlate400,
    onSecondary = Black,
    background = Black,
    onBackground = White,
    surface = DarkGrey,
    onSurface = White,
    surfaceVariant = DarkBorderGrey,
    onSurfaceVariant = SleekSlate400,
    outline = SleekSlate500
  )

private val LightColorScheme =
  lightColorScheme(
    primary = Black,
    onPrimary = White,
    secondary = SleekSlate500,
    onSecondary = White,
    background = White,
    onBackground = SleekSlate950,
    surface = SleekSlate50,
    onSurface = SleekSlate950,
    surfaceVariant = SleekSlate100,
    onSurfaceVariant = SleekSlate600,
    outline = SleekSlate200
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic color to enforce strict monochrome black and white theme
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
