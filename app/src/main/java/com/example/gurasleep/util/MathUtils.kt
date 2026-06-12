package com.example.gurasleep.util

import androidx.compose.ui.geometry.Offset
import kotlin.math.sqrt

/**
 * 2D 向量/物理数学工具
 */
object MathUtils {

    fun distance(a: Offset, b: Offset): Float {
        val dx = a.x - b.x
        val dy = a.y - b.y
        return sqrt(dx * dx + dy * dy)
    }

    fun normalize(v: Offset): Offset {
        val len = sqrt(v.x * v.x + v.y * v.y)
        return if (len > 0.0001f) Offset(v.x / len, v.y / len) else Offset.Zero
    }

    fun dot(a: Offset, b: Offset): Float = a.x * b.x + a.y * b.y

    /** 限制值在 [min, max] 之间 */
    fun clamp(value: Float, min: Float, max: Float): Float =
        value.coerceIn(min, max)

    /** 线性映射 value 从 [fromMin,fromMax] 到 [toMin,toMax] */
    fun mapRange(value: Float, fromMin: Float, fromMax: Float, toMin: Float, toMax: Float): Float {
        val t = clamp((value - fromMin) / (fromMax - fromMin), 0f, 1f)
        return toMin + t * (toMax - toMin)
    }

    /** 平滑阻尼插值 (lerp with damping factor) */
    fun damp(a: Float, b: Float, damping: Float, deltaTime: Float): Float {
        return a + (b - a) * (1f - damping).coerceIn(0f, 1f) * deltaTime * 60f
    }

    fun damp(a: Offset, b: Offset, damping: Float, deltaTime: Float): Offset {
        return Offset(
            damp(a.x, b.x, damping, deltaTime),
            damp(a.y, b.y, damping, deltaTime)
        )
    }
}
