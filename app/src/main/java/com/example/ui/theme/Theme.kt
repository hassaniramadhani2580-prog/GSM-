package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GsmColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color(0xFF001F24),
    primaryContainer = CyberCyanDark,
    onPrimaryContainer = CyberCyan,
    secondary = TechAmber,
    onSecondary = Color(0xFF241A00),
    secondaryContainer = TechAmberDim,
    onSecondaryContainer = TechAmber,
    tertiary = MatrixGreen,
    onTertiary = Color(0xFF00220F),
    tertiaryContainer = MatrixGreenDark,
    onTertiaryContainer = MatrixGreen,
    background = GsmBackground,
    onBackground = TextPrimary,
    surface = GsmSurface,
    onSurface = TextPrimary,
    surfaceVariant = GsmSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = GsmCardBorder,
    error = DangerNeon,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Keep consistent cybernetic GSM identity
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GsmColorScheme,
        typography = Typography,
        content = content
    )
}
