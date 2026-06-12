package com.example.gurasleep.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.gurasleep.domain.model.CircleBody
import com.example.gurasleep.domain.model.DockState
import com.example.gurasleep.ui.components.AudioCircle
import com.example.gurasleep.ui.components.PhysicsCanvas
import com.example.gurasleep.ui.theme.Lavender
import com.example.gurasleep.ui.theme.StarWhite
import com.example.gurasleep.viewmodel.CaptureViewModel
import kotlin.math.roundToInt

/**
 * 「捕获」标签页 — 音频圆形漂浮区域
 */
@Composable
fun CaptureScreen(
    viewModel: CaptureViewModel,
    dockState: DockState,
    onCirclesUpdate: (List<CircleBody>) -> Unit,
    modifier: Modifier = Modifier
) {
    val circles = viewModel.circles

    var dockAreaTop by remember { mutableStateOf(0f) }

    Box(modifier = modifier.fillMaxSize()) {
        // 背景物理画布
        PhysicsCanvas(
            circles = circles,
            onFrame = { updated, _ ->
                viewModel.updateCircles(updated)
            },
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { size ->
                    viewModel.canvasWidth = size.width.toFloat()
                    viewModel.canvasHeight = size.height.toFloat()
                }
        )

        // 音频圆形（覆盖在画布上方）
        circles.forEach { circle ->
            if (!circle.isCollected) {
                AudioCircle(
                    circle = circle,
                    onDragStart = { viewModel.onDragStart(circle.id) },
                    onDrag = { dx, dy -> viewModel.onDrag(circle.id, dx, dy) },
                    onDragEnd = { viewModel.onDragEnd(circle.id, dockAreaTop) },
                    isCollected = false,
                    modifier = Modifier.offset {
                        IntOffset(
                            (circle.position.x - circle.radius).roundToInt(),
                            (circle.position.y - circle.radius).roundToInt()
                        )
                    }
                )
            }
        }

        // 顶部信息栏
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 24.dp, end = 24.dp)
        ) {
            Text(
                text = "🌙 GuraSleep",
                style = MaterialTheme.typography.headlineLarge,
                color = StarWhite,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "拖拽音频圆形收集到 Dock 栏",
                style = MaterialTheme.typography.bodyMedium,
                color = Lavender.copy(alpha = 0.7f)
            )
        }

        // 底部已收集区域占位（Milestone 4 加入播放控制）
        Column(
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 100.dp, start = 20.dp, end = 20.dp)
                .onSizeChanged { dockAreaTop = it.y.toFloat() }
        ) {
            if (dockState.collectedAudioIds.isNotEmpty()) {
                Text(
                    text = "🎵 已收集: ${dockState.collectedAudioIds.size} 个音频",
                    style = MaterialTheme.typography.labelMedium,
                    color = Lavender
                )
            }
        }
    }
}
