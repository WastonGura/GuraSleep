package com.example.gurasleep.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.compose.ui.geometry.Offset
import com.example.gurasleep.R
import com.example.gurasleep.domain.model.AudioCategory
import com.example.gurasleep.domain.model.AudioItem
import com.example.gurasleep.domain.model.CircleBody
import com.example.gurasleep.domain.model.DockState
import com.example.gurasleep.domain.model.DockTab
import com.example.gurasleep.ui.theme.FireColor
import com.example.gurasleep.ui.theme.ForestColor
import com.example.gurasleep.ui.theme.MistBlue
import com.example.gurasleep.ui.theme.OceanColor
import com.example.gurasleep.ui.theme.RainColor
import com.example.gurasleep.ui.theme.UrbanColor
import com.example.gurasleep.ui.theme.WindColor
import kotlin.random.Random

/**
 * 捕获页状态管理
 */
class CaptureViewModel : ViewModel() {

    // ── 音频库 ──
    val audioItems = listOf(
        AudioItem("rain",    "雨声",   "🌧️",  R.raw.rain,    RainColor,   AudioCategory.RAIN),
        AudioItem("ocean",   "海浪",   "🌊",  R.raw.ocean,   OceanColor,  AudioCategory.OCEAN),
        AudioItem("fire",    "篝火",   "🔥",  R.raw.fire,    FireColor,   AudioCategory.NATURE),
        AudioItem("wind",    "风铃",   "🎐",  R.raw.wind,    WindColor,   AudioCategory.AMBIENT),
        AudioItem("forest",  "森林",   "🌿",  R.raw.forest,  ForestColor, AudioCategory.NATURE),
        AudioItem("city",    "城市",   "🏙️",  R.raw.city,    UrbanColor,  AudioCategory.URBAN),
        AudioItem("cafe",    "咖啡厅", "☕",  R.raw.cafe,    MistBlue,    AudioCategory.AMBIENT),
        AudioItem("thunder", "雷声",   "⛈️",  R.raw.thunder, RainColor,   AudioCategory.RAIN)
    )

    // ── 圆形实体 ──
    var circles by mutableStateOf(emptyList<CircleBody>())
        private set

    // ── Dock 状态 ──
    var dockState by mutableStateOf(DockState())
        private set

    // 拖拽中的圆形 id
    var draggingCircleId: String? by mutableStateOf(null)
        private set

    // ── 播放状态 ──
    var isPlaying by mutableStateOf(false)
        private set
    var masterVolume by mutableStateOf(0.8f)
        private set

    // Canvas 尺寸
    var canvasWidth by mutableStateOf(1080f)
    var canvasHeight by mutableStateOf(1920f)

    init {
        // 初始化圆形：随机散布在画布上部
        circles = audioItems.mapIndexed { index, audio ->
            val x = 100f + (index * 130f) % 900f + Random.nextFloat() * 60f
            val y = 120f + (index / 2) * 170f + Random.nextFloat() * 50f
            val vx = (Random.nextFloat() - 0.5f) * 80f
            val vy = (Random.nextFloat() - 0.5f) * 80f
            CircleBody(
                id = audio.id,
                position = Offset(x, y),
                velocity = Offset(vx, vy),
                radius = 72f,
                audioItem = audio
            )
        }
    }

    // ── 拖拽事件 ──
    fun onDragStart(circleId: String) {
        draggingCircleId = circleId
    }

    fun onDrag(circleId: String, dx: Float, dy: Float) {
        circles = circles.map { c ->
            if (c.id == circleId) {
                c.copy(position = Offset(c.position.x + dx, c.position.y + dy))
            } else c
        }
    }

    fun onDragEnd(circleId: String, dockBoundsTop: Float = 0f) {
        draggingCircleId = null
        val circle = circles.find { it.id == circleId } ?: return
        if (circle.position.y > dockBoundsTop - 80f) {
            collectCircle(circleId)
        }
    }

    fun updateCircles(updated: List<CircleBody>) {
        circles = updated
    }

    // ── Dock 交互 ──
    fun selectTab(tab: DockTab) {
        dockState = dockState.copy(activeTab = tab)
    }

    fun setHovering(hovering: Boolean, intensity: Float = 0f) {
        dockState = dockState.copy(isHovering = hovering, hoverIntensity = intensity)
    }

    private fun collectCircle(circleId: String) {
        val ids = dockState.collectedAudioIds.toMutableList()
        if (!ids.contains(circleId)) {
            ids.add(circleId)
            dockState = dockState.copy(collectedAudioIds = ids)
            circles = circles.map { c ->
                if (c.id == circleId) c.copy(isCollected = true) else c
            }
        }
    }

    fun removeCollected(circleId: String) {
        val ids = dockState.collectedAudioIds.toMutableList()
        ids.remove(circleId)
        dockState = dockState.copy(collectedAudioIds = ids)
        circles = circles.map { c ->
            if (c.id == circleId) c.copy(isCollected = false) else c
        }
    }

    fun updateMasterVolume(vol: Float) {
        masterVolume = vol.coerceIn(0f, 1f)
    }

    fun togglePlay() {
        isPlaying = !isPlaying
    }
}
