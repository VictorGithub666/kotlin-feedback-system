package com.example.feedback_system.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// === Global Colors ===
val AppRed = Color(0xFFD32F2F)
val AppWhite = Color.White
val AppBlack = Color.Black
// Add to your existing Theme.kt
val CardBackground = Color(0xFFFFFFFF)
val CardTextColor = Color(0xFF333333)
val CardShadow = Color(0x1A000000)

// === Color Schemes ===
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = AppRed,
    secondary = AppRed,
    background = AppWhite,
    surface = AppWhite,
    onPrimary = AppWhite,
    onSecondary = AppWhite,
    onBackground = AppBlack,
    onSurface = AppBlack
)

// === Theme ===
@Composable
fun FeedbacksystemTheme(
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// === Red Button using Theme ===
@Composable
fun RedButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(text)
    }
}

// === Styled Input Field using Theme ===
@Composable
fun StyledOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFD32F2F),
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = Color(0xFFD32F2F),
            unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
            focusedTextColor = Color(0xFFD32F2F)
        )
    )
}
