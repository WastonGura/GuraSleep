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
 * 「睡眠」标签页 — 占位，后续里程碑接入设置面板
 */
@Composable
fun SleepScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "💤 睡眠",
            style = MaterialTheme.typography.headlineMedium,
            color = Lavender
        )
    }
}
