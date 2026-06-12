package com.example.gurasleep.domain.model

import androidx.compose.ui.graphics.Color

/**
 * 音频条目 — 每个对应一个可交互的圆形
 */
data class AudioItem(
    val id: String,
    val name: String,
    val icon: String,               // emoji 字符
    val rawResId: Int,              // R.raw.xxx
    val color: Color,
    val category: AudioCategory = AudioCategory.NATURE
)

enum class AudioCategory(val displayName: String) {
    RAIN("雨声"),
    OCEAN("海浪"),
    NATURE("自然"),
    URBAN("城市"),
    AMBIENT("氛围"),
    INSTRUMENT("器乐")
}
