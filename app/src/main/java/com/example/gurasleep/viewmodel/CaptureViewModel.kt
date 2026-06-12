package com.example.gurasleep.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.gurasleep.domain.model.DockState
import com.example.gurasleep.domain.model.DockTab

/**
 * 捕获页状态管理
 */
class CaptureViewModel {

    // ── Dock 状态 ──
    var dockState by mutableStateOf(DockState())
        private set

    // ── Dock 交互 ──
    fun selectTab(tab: DockTab) {
        dockState = dockState.copy(activeTab = tab)
    }

    fun setHovering(hovering: Boolean, intensity: Float = 0f) {
        dockState = dockState.copy(isHovering = hovering, hoverIntensity = intensity)
    }
}
