package com.example.gurasleep.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.gurasleep.ui.theme.Lavender

/**
 * 「捕获」标签页 — 占位，后续里程碑接入物理画布与音频圆
 */
@Composable
fun CaptureScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🎵 捕获",
            style = MaterialTheme.typography.headlineMedium,
            color = Lavender
        )
    }
}
