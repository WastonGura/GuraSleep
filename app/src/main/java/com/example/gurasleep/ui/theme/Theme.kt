package com.example.gurasleep.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

// 深紫色暗色主题（睡眠场景强制暗色）
val GuraDarkColorScheme = darkColorScheme(
    primary = DeepPurple400,
    onPrimary = StarWhite,
    primaryContainer = DeepPurple700,
    onPrimaryContainer = Lavender,
    secondary = DeepPurple300,
    onSecondary = DeepPurple950,
    secondaryContainer = DeepPurple600,
    onSecondaryContainer = Lavender,
    tertiary = DawnGold,
    onTertiary = DeepPurple950,
    tertiaryContainer = Color(0xFF5C3D1E),
    onTertiaryContainer = DawnGold,
    error = Color(0xFFFF6B6B),
    onError = DeepPurple950,
    errorContainer = Color(0xFF5C1A1A),
    onErrorContainer = Color(0xFFFFCDD2),
    background = DeepPurple950,
    onBackground = StarWhite,
    surface = DeepPurple900,
    onSurface = StarWhite,
    surfaceVariant = DeepPurple800,
    onSurfaceVariant = Lavender,
    outline = DeepPurple500,
    outlineVariant = DeepPurple700,
    inverseSurface = StarWhite,
    inverseOnSurface = DeepPurple950,
    inversePrimary = DeepPurple700,
    surfaceTint = MidnightGlow
)

@Composable
fun GuraSleepTheme(
    // 睡眠 App 始终使用暗色，忽略系统明亮主题
    forceDark: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            dynamicDarkColorScheme(LocalContext.current)
        }
        else -> GuraDarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = GuraTypography,
        content = content
    )
}
