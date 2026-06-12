package com.example.gurasleep.domain.model

/**
 * 睡眠设置（后续接入 DataStore 持久化）
 */
data class SleepSettings(
    val stopAfterMinutes: Int = 45,
    val wakeUpHour: Int = 7,
    val wakeUpMinute: Int = 30,
    val fadeInMinutes: Int = 10,           // 唤醒音量渐增时长
    val sleepDetectionEnabled: Boolean = true
)

/**
 * 睡眠检测阶段
 */
enum class SleepStage(val displayName: String) {
    AWAKE("清醒"),
    LIGHT("浅睡"),
    DEEP("深睡"),
    REM("REM")
}
