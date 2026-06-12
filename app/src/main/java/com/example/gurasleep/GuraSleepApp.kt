package com.example.gurasleep

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.gurasleep.ui.theme.DeepPurple950
import com.example.gurasleep.ui.theme.GuraSleepTheme
import com.example.gurasleep.ui.theme.StarWhite

/**
 * 顶层 Composable — 骨架占位，后续里程碑接入 Dock 导航与页面
 */
@Composable
fun GuraSleepApp() {
    GuraSleepTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepPurple950),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🌙 GuraSleep",
                style = MaterialTheme.typography.headlineLarge,
                color = StarWhite,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
