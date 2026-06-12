package com.example.gurasleep.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.gurasleep.domain.model.CircleBody
import com.example.gurasleep.domain.model.DockState
import com.example.gurasleep.ui.components.AudioCircle
import com.example.gurasleep.ui.components.PhysicsCanvas
import com.example.gurasleep.ui.theme.DeepPurple900
import com.example.gurasleep.ui.theme.Lavender
import com.example.gurasleep.ui.theme.MidnightGlow
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

        // 底部已收集列表 + 播放控制
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 100.dp, start = 20.dp, end = 20.dp)
                .onSizeChanged { dockAreaTop = it.y.toFloat() }
        ) {
            if (dockState.collectedAudioIds.isNotEmpty()) {
                // 已收集音频标签 + 播放/停止按钮
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DeepPurple900.copy(alpha = 0.8f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🎵 当前混音",
                            style = MaterialTheme.typography.labelMedium,
                            color = Lavender
                        )
                        Text(
                            text = dockState.collectedAudioIds.joinToString(", ") { id ->
                                viewModel.audioItems.find { it.id == id }?.name ?: id
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = StarWhite
                        )
                    }
                    // 播放/停止按钮
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MidnightGlow)
                            .clickable { viewModel.togglePlay() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (viewModel.isPlaying) Icons.Default.Stop
                                          else Icons.Default.PlayArrow,
                            contentDescription = if (viewModel.isPlaying) "停止" else "播放",
                            tint = StarWhite
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // 总音量滑条
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DeepPurple900.copy(alpha = 0.8f))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔊", style = MaterialTheme.typography.labelLarge)
                    Slider(
                        value = viewModel.masterVolume,
                        onValueChange = { viewModel.setMasterVolume(it) },
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = MidnightGlow,
                            activeTrackColor = MidnightGlow
                        )
                    )
                    Text(
                        text = "${(viewModel.masterVolume * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = Lavender
                    )
                }
            }
        }
    }
}
