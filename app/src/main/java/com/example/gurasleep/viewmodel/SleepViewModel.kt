package com.example.gurasleep.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.gurasleep.domain.detector.SleepDetector
import com.example.gurasleep.domain.model.SleepSettings
import com.example.gurasleep.domain.model.SleepStage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 睡眠页状态管理
 */
class SleepViewModel : ViewModel() {

    // 由外部注入（MainActivity 持有 Context 构造）
    var sleepDetector: SleepDetector? = null

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    var settings by mutableStateOf(SleepSettings())
        private set

    var isSleeping by mutableStateOf(false)
        private set

    var remainingSeconds by mutableStateOf(0)
        private set

    var sleepDetected by mutableStateOf(false)
        private set

    var currentSleepStage by mutableStateOf(SleepStage.AWAKE)
        private set

    var countdownText by mutableStateOf("")
        private set

    // ── 定时关停 ──
    fun setStopAfterMinutes(minutes: Int) {
        settings = settings.copy(stopAfterMinutes = minutes.coerceIn(1, 480))
    }

    fun increaseStopTime(by: Int = 5) {
        setStopAfterMinutes(settings.stopAfterMinutes + by)
    }

    fun decreaseStopTime(by: Int = 5) {
        setStopAfterMinutes(settings.stopAfterMinutes - by)
    }

    // ── 唤醒时间 ──
    fun setWakeUpTime(hour: Int, minute: Int) {
        settings = settings.copy(
            wakeUpHour = hour.coerceIn(0, 23),
            wakeUpMinute = minute.coerceIn(0, 59)
        )
    }

    fun setFadeInMinutes(minutes: Int) {
        settings = settings.copy(fadeInMinutes = minutes.coerceIn(1, 60))
    }

    // ── 睡眠检测开关 ──
    fun setSleepDetection(enabled: Boolean) {
        settings = settings.copy(sleepDetectionEnabled = enabled)
    }

    // ── 开始 / 停止睡眠 ──
    fun startSleep() {
        isSleeping = true
        remainingSeconds = settings.stopAfterMinutes * 60
        updateCountdownText()

        if (settings.sleepDetectionEnabled) {
            sleepDetector?.start()
            scope.launch {
                sleepDetector?.sleepStage?.collect { stage ->
                    updateSleepStage(stage)
                }
            }
        }
    }

    fun stopSleep() {
        isSleeping = false
        remainingSeconds = 0
        countdownText = ""
        sleepDetector?.stop()
        scope.cancel()
    }

    /** 每秒调用一次 */
    fun tick() {
        if (!isSleeping) return
        if (remainingSeconds > 0) {
            remainingSeconds--
            updateCountdownText()
        }
        if (remainingSeconds <= 0) {
            stopSleep()
        }
    }

    fun updateSleepStage(stage: SleepStage) {
        currentSleepStage = stage
        sleepDetected = stage != SleepStage.AWAKE
    }

    private fun updateCountdownText() {
        val h = remainingSeconds / 3600
        val m = (remainingSeconds % 3600) / 60
        val s = remainingSeconds % 60
        countdownText = if (h > 0) {
            String.format("%d:%02d:%02d", h, m, s)
        } else {
            String.format("%02d:%02d", m, s)
        }
    }
}
