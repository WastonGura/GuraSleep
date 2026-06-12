package com.example.gurasleep.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.onSizeChanged
import com.example.gurasleep.domain.engine.PhysicsEngine
import com.example.gurasleep.domain.model.CircleBody
import com.example.gurasleep.ui.theme.DeepPurple850

/**
 * 物理碰撞画布
 *  - 背景网格
 *  - 每帧推进物理引擎
 */
@Composable
fun PhysicsCanvas(
    circles: List<CircleBody>,
    onFrame: (List<CircleBody>, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val engine = remember {
        PhysicsEngine(gravity = 0f, damping = 0.995f, restitution = 0.45f)
    }

    // 帧循环 (~60fps)
    LaunchedEffect(Unit) {
        var lastTime = 0L
        while (true) {
            kotlinx.coroutines.delay(16)
            val currentTime = System.nanoTime()
            val deltaTime = if (lastTime == 0L) 0.016f
                            else ((currentTime - lastTime) / 1_000_000_000f).coerceAtMost(0.05f)
            lastTime = currentTime

            engine.step(deltaTime, circles)
            onFrame(circles, deltaTime)
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                engine.width = size.width.toFloat()
                engine.height = size.height.toFloat()
            }
    ) {
        drawBackgroundGrid()
    }
}

private fun DrawScope.drawBackgroundGrid() {
    val step = 60f
    val lineColor = DeepPurple850.copy(alpha = 0.12f)

    var x = 0f
    while (x < size.width) {
        drawLine(lineColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 0.5f)
        x += step
    }
    var y = 0f
    while (y < size.height) {
        drawLine(lineColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 0.5f)
        y += step
    }
}
