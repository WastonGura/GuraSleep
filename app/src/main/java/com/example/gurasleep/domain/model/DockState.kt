package com.example.gurasleep.domain.model

/**
 * Dock 栏标签页
 */
enum class DockTab { CAPTURE, SLEEP }

/**
 * Dock 栏运行时状态
 */
data class DockState(
    val activeTab: DockTab = DockTab.CAPTURE,
    val collectedAudioIds: List<String> = emptyList(),
    val isHovering: Boolean = false,
    val hoverIntensity: Float = 0f,       // 0..1
    val magneticRadiusPx: Float = 180f    // Dock 磁吸感知半径
)
