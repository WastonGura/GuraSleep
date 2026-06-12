package com.example.gurasleep.domain.model

import androidx.compose.ui.geometry.Offset

/**
 * 2D 物理圆形实体
 */
data class CircleBody(
    val id: String,
    var position: Offset,
    var velocity: Offset = Offset.Zero,
    val mass: Float = 1f,
    val radius: Float = 80f,          // px
    var deformation: Deformation = Deformation(),
    val audioItem: AudioItem? = null,
    var isCollected: Boolean = false
)

/**
 * 挤压形变数据
 * scaleX — 碰撞接触方向的缩放
 * scaleY — 正交方向的缩放
 * rotationRad — 形变方向角度（弧度）
 */
data class Deformation(
    val scaleX: Float = 1f,
    val scaleY: Float = 1f,
    val rotationRad: Float = 0f
) {
    companion object {
        val None = Deformation(1f, 1f, 0f)
    }
}
