package com.example.kotlin_app_study.bp.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val DarkColors = darkColorScheme(
    primary = BPColors.Primary,
    onPrimary = BPColors.OnSurface,
    primaryContainer = BPColors.PrimaryContainer,
    onPrimaryContainer = BPColors.OnSurface,
    secondary = BPColors.Accent,
    onSecondary = BPColors.OnSurface,
    tertiary = BPColors.Warning,
    onTertiary = BPColors.OnSurface,
    error = BPColors.Danger,
    background = BPColors.Background,
    onBackground = BPColors.OnSurface,
    surface = BPColors.Surface,
    onSurface = BPColors.OnSurface,
    surfaceVariant = BPColors.SurfaceVariant,
    onSurfaceVariant = BPColors.OnSurfaceVariant,
    outlineVariant = BPColors.Divider,
)

private val BPTypography = Typography(
    displayLarge = TextStyle(fontSize = 56.sp, fontWeight = FontWeight.Black),
    displayMedium = TextStyle(fontSize = 40.sp, fontWeight = FontWeight.Black),
    headlineLarge = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),
    bodyMedium = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
    bodySmall = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
    labelLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
)

@Composable
fun BloodPressureTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = BPTypography,
        content = content
    )
}
