package com.example.gurasleep.domain.engine

import androidx.compose.ui.geometry.Offset
import com.example.gurasleep.domain.model.CircleBody
import com.example.gurasleep.domain.model.Deformation
import com.example.gurasleep.util.MathUtils.clamp
import com.example.gurasleep.util.MathUtils.distance
import com.example.gurasleep.util.MathUtils.dot
import com.example.gurasleep.util.MathUtils.normalize
import kotlin.math.abs
import kotlin.math.atan2

/**
 * 轻量 2D 物理引擎
 *   - 圆形碰撞检测 + 冲量响应
 *   - 挤压形变 (squash & stretch)
 *   - 边界反弹
 */
class PhysicsEngine(
    var width: Float = 1080f,
    var height: Float = 1920f,
    val gravity: Float = 0f,           // 0 = 无重力漂浮
    val damping: Float = 0.98f,
    val restitution: Float = 0.55f     // 弹性系数
) {
    /** 每帧推进 */
    fun step(deltaTime: Float, bodies: List<CircleBody>) {
        val dt = deltaTime.coerceAtMost(0.05f) // 防止大帧跳跃

        // 速度迭代（重力 + 阻尼）
        for (b in bodies) {
            b.velocity = Offset(
                b.velocity.x * damping,
                b.velocity.y * damping + gravity * dt * 60f
            )
            b.position = Offset(
                b.position.x + b.velocity.x * dt * 60f,
                b.position.y + b.velocity.y * dt * 60f
            )
        }

        // 碰撞检测 & 响应
        for (i in bodies.indices) {
            for (j in i + 1 until bodies.size) {
                resolveCollision(bodies[i], bodies[j])
            }
        }

        // 边界反弹 + 形变衰减
        for (b in bodies) {
            enforceBounds(b)
            decayDeformation(b, dt)
        }
    }

    private fun resolveCollision(a: CircleBody, b: CircleBody) {
        val dist = distance(a.position, b.position)
        val minDist = a.radius + b.radius

        if (dist >= minDist || dist < 0.01f) return

        val n = normalize(Offset(
            b.position.x - a.position.x,
            b.position.y - a.position.y
        ))

        // 相对速度在法线方向的分量
        val relVel = Offset(
            a.velocity.x - b.velocity.x,
            a.velocity.y - b.velocity.y
        )
        val vn = dot(relVel, n)

        // 正在分离则不处理
        if (vn > 0f) return

        // 冲量
        val invMassA = 1f / a.mass
        val invMassB = 1f / b.mass
        val j = -(1f + restitution) * vn / (invMassA + invMassB)

        a.velocity = Offset(
            a.velocity.x + j * invMassA * n.x,
            a.velocity.y + j * invMassA * n.y
        )
        b.velocity = Offset(
            b.velocity.x - j * invMassB * n.x,
            b.velocity.y - j * invMassB * n.y
        )

        // 位置修正（分离重叠）
        val overlap = (minDist - dist) / 2f
        a.position = Offset(
            a.position.x - overlap * n.x,
            a.position.y - overlap * n.y
        )
        b.position = Offset(
            b.position.x + overlap * n.x,
            b.position.y + overlap * n.y
        )

        // 挤压形变
        applyDeformation(a, b, n, overlap * 2f, minDist)
    }

    private fun applyDeformation(a: CircleBody, b: CircleBody, n: Offset, overlap: Float, minDist: Float) {
        val intensity = clamp(overlap / minDist, 0f, 0.35f)

        val angle = atan2(n.y, n.x)

        // 接触方向压扁，正交方向拉长
        val sx = 1f - intensity * 0.8f
        val sy = 1f + intensity * 0.5f

        a.deformation = Deformation(scaleX = sx, scaleY = sy, rotationRad = angle)
        b.deformation = Deformation(scaleX = sx, scaleY = sy, rotationRad = angle)
    }

    private fun enforceBounds(body: CircleBody) {
        val r = body.radius
        if (body.position.x - r < 0f) {
            body.position = Offset(r, body.position.y)
            body.velocity = Offset(abs(body.velocity.x) * restitution, body.velocity.y)
        } else if (body.position.x + r > width) {
            body.position = Offset(width - r, body.position.y)
            body.velocity = Offset(-abs(body.velocity.x) * restitution, body.velocity.y)
        }
        if (body.position.y - r < 0f) {
            body.position = Offset(body.position.x, r)
            body.velocity = Offset(body.velocity.x, abs(body.velocity.y) * restitution)
        } else if (body.position.y + r > height) {
            body.position = Offset(body.position.x, height - r)
            body.velocity = Offset(body.velocity.x, -abs(body.velocity.y) * restitution)
        }
    }

    private fun decayDeformation(body: CircleBody, dt: Float) {
        val d = body.deformation
        if (abs(d.scaleX - 1f) < 0.002f && abs(d.scaleY - 1f) < 0.002f) {
            if (d != Deformation.None) {
                body.deformation = Deformation.None
            }
            return
        }
        val speed = 8f * dt * 60f
        body.deformation = d.copy(
            scaleX = d.scaleX + (1f - d.scaleX) * speed,
            scaleY = d.scaleY + (1f - d.scaleY) * speed
        )
    }
}
