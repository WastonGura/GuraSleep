package com.example.gurasleep.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gurasleep.domain.model.SleepStage
import com.example.gurasleep.ui.theme.DawnGold
import com.example.gurasleep.ui.theme.DeepPurple700
import com.example.gurasleep.ui.theme.DeepPurple800
import com.example.gurasleep.ui.theme.DeepPurple900
import com.example.gurasleep.ui.theme.Lavender
import com.example.gurasleep.ui.theme.MidnightGlow
import com.example.gurasleep.ui.theme.StarWhite
import com.example.gurasleep.viewmodel.SleepViewModel

/**
 * 「睡眠」标签页 — 定时关停 + 唤醒设置
 */
@Composable
fun SleepScreen(
    viewModel: SleepViewModel,
    modifier: Modifier = Modifier
) {
    val settings = viewModel.settings

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 60.dp, start = 24.dp, end = 24.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 标题
        Text(
            text = "🌙 睡眠设置",
            style = MaterialTheme.typography.headlineLarge,
            color = StarWhite,
            fontWeight = FontWeight.Bold
        )

        // ── 定时关停卡片 ──
        SectionCard(title = "⏱️ 定时关停") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircleButton("-") { viewModel.decreaseStopTime() }

                Spacer(Modifier.width(24.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${settings.stopAfterMinutes}",
                        style = MaterialTheme.typography.displayLarge,
                        color = StarWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "分钟",
                        style = MaterialTheme.typography.labelMedium,
                        color = Lavender
                    )
                }

                Spacer(Modifier.width(24.dp))

                CircleButton("+") { viewModel.increaseStopTime() }
            }

            Spacer(Modifier.height(12.dp))

            // 进度条
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(DeepPurple800)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(settings.stopAfterMinutes / 480f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(MidnightGlow)
                )
            }
        }

        // ── 早晨唤醒卡片 ──
        SectionCard(title = "☀️ 早晨唤醒") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TimePickerColumn(
                    value = settings.wakeUpHour,
                    range = 0..23,
                    onChange = { viewModel.setWakeUpTime(it, settings.wakeUpMinute) }
                )

                Text(
                    text = ":",
                    style = MaterialTheme.typography.displayLarge,
                    color = StarWhite,
                    fontWeight = FontWeight.Bold
                )

                TimePickerColumn(
                    value = settings.wakeUpMinute,
                    range = 0..59,
                    onChange = { viewModel.setWakeUpTime(settings.wakeUpHour, it) },
                    step = 5
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "渐增唤醒 (${settings.fadeInMinutes} 分钟)",
                style = MaterialTheme.typography.bodyMedium,
                color = Lavender
            )
        }

        // ── 睡眠检测卡片 ──
        SectionCard(title = "💤 睡眠检测") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "智能睡眠检测",
                        style = MaterialTheme.typography.titleMedium,
                        color = StarWhite
                    )
                    Text(
                        text = "使用传感器分析睡眠状态",
                        style = MaterialTheme.typography.labelMedium,
                        color = Lavender.copy(alpha = 0.7f)
                    )
                }
                Switch(
                    checked = settings.sleepDetectionEnabled,
                    onCheckedChange = { viewModel.setSleepDetection(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = StarWhite,
                        checkedTrackColor = MidnightGlow,
                        uncheckedTrackColor = DeepPurple700
                    )
                )
            }
        }

        // ── 当前睡眠阶段（睡眠中显示）──
        if (viewModel.isSleeping && viewModel.settings.sleepDetectionEnabled) {
            SectionCard(title = "📊 检测状态") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = viewModel.currentSleepStage.displayName,
                        style = MaterialTheme.typography.displayLarge,
                        color = when (viewModel.currentSleepStage) {
                            SleepStage.AWAKE -> Lavender
                            SleepStage.LIGHT -> MidnightGlow
                            SleepStage.DEEP -> StarWhite
                            SleepStage.REM -> DawnGold
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // ── 开始睡眠大按钮 ──
        Button(
            onClick = {
                if (viewModel.isSleeping) viewModel.stopSleep()
                else viewModel.startSleep()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (viewModel.isSleeping) DeepPurple700 else MidnightGlow
            )
        ) {
            Text(
                text = if (viewModel.isSleeping) "⏹ 停止睡眠"
                       else "▶ 开始睡眠",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = StarWhite
            )
            if (viewModel.countdownText.isNotEmpty()) {
                Text(
                    text = " · ${viewModel.countdownText}",
                    style = MaterialTheme.typography.titleMedium,
                    color = StarWhite.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// ── 辅助组件 ──

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(DeepPurple900.copy(alpha = 0.75f))
            .padding(20.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = StarWhite,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(16.dp))
        content()
    }
}

@Composable
private fun CircleButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(DeepPurple700)
            .border(1.dp, MidnightGlow.copy(alpha = 0.4f), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium,
            color = StarWhite,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TimePickerColumn(
    value: Int,
    range: IntRange,
    onChange: (Int) -> Unit,
    step: Int = 1
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "▲",
            style = MaterialTheme.typography.labelLarge,
            color = Lavender,
            modifier = Modifier
                .clip(CircleShape)
                .clickable {
                    var next = value + step
                    if (next > range.last) next = range.first
                    onChange(next)
                }
                .padding(8.dp)
        )

        Text(
            text = String.format("%02d", value),
            style = MaterialTheme.typography.displayLarge,
            color = StarWhite,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "▼",
            style = MaterialTheme.typography.labelLarge,
            color = Lavender,
            modifier = Modifier
                .clip(CircleShape)
                .clickable {
                    var next = value - step
                    if (next < range.first) next = range.last
                    onChange(next)
                }
                .padding(8.dp)
        )
    }
}
