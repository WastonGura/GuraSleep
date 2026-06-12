package com.example.gurasleep.domain.detector

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.gurasleep.domain.model.SleepStage
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * 睡眠检测器
 *  方案：加速度传感器分析设备静止程度
 *  扩展：可接入 Google Sleep API（需 Play Services）
 */
class SleepDetector(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _sleepStage = MutableStateFlow(SleepStage.AWAKE)
    val sleepStage: StateFlow<SleepStage> = _sleepStage.asStateFlow()

    // 滑动窗口参数
    private val windowSize = 30
    private val accelWindow = mutableListOf<Float>()
    private var stableCount = 0
    private val stableThreshold = 0.15f  // 加速度方差低于此值视为静止

    var isRunning = false
        private set

    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(
                this, it,
                SensorManager.SENSOR_DELAY_NORMAL,  // 200ms, 省电
                1_000_000  // 1s 批量
            )
        }
        isRunning = true
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        isRunning = false
        _sleepStage.value = SleepStage.AWAKE
        accelWindow.clear()
        stableCount = 0
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        val magnitude = sqrt(x * x + y * y + z * z) - 9.8f  // 去除重力

        accelWindow.add(abs(magnitude))
        if (accelWindow.size > windowSize) {
            accelWindow.removeAt(0)
        }

        if (accelWindow.size >= windowSize) {
            val variance = calculateVariance(accelWindow)
            if (variance < stableThreshold) {
                stableCount++
            } else {
                stableCount = maxOf(0, stableCount - 2)
            }

            // 静止超过阈值判定为对应阶段
            val newStage = when {
                stableCount > 1500 -> SleepStage.DEEP   // ~5min
                stableCount > 600  -> SleepStage.LIGHT   // ~2min
                else               -> SleepStage.AWAKE
            }
            if (newStage != _sleepStage.value) {
                _sleepStage.value = newStage
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun calculateVariance(data: List<Float>): Float {
        val mean = data.sum() / data.size
        var sum = 0f
        for (v in data) {
            val diff = v - mean
            sum += diff * diff
        }
        return sum / data.size
    }
}
