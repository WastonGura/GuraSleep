package com.example.gurasleep.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gurasleep.domain.model.DockState
import com.example.gurasleep.domain.model.DockTab
import com.example.gurasleep.ui.theme.DeepPurple700
import com.example.gurasleep.ui.theme.DeepPurple800
import com.example.gurasleep.ui.theme.DeepPurple900
import com.example.gurasleep.ui.theme.Lavender
import com.example.gurasleep.ui.theme.MidnightGlow
import com.example.gurasleep.ui.theme.StarWhite

/**
 * 悬浮 Dock 栏
 *
 * 状态驱动：根据 dockState 动态改变外观（空闲/磁吸/激活）
 */
@Composable
fun DockBar(
    dockState: DockState,
    onTabSelected: (DockTab) -> Unit,
    collectedCount: Int = dockState.collectedAudioIds.size,
    modifier: Modifier = Modifier
) {
    val hoverScale by animateFloatAsState(
        targetValue = if (dockState.isHovering) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
        label = "dockHoverScale"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = dockState.hoverIntensity,
        animationSpec = tween(150),
        label = "glowAlpha"
    )

    val borderColor by animateColorAsState(
        targetValue = MidnightGlow.copy(alpha = glowAlpha),
        animationSpec = tween(150),
        label = "borderColor"
    )

    val bgAlpha = if (dockState.isHovering) 0.92f else 0.75f

    Box(
        modifier = modifier
            .scale(hoverScale)
            .shadow(
                elevation = (12 + 8 * dockState.hoverIntensity).dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = MidnightGlow.copy(alpha = glowAlpha * 0.6f),
                spotColor = MidnightGlow.copy(alpha = glowAlpha * 0.8f)
            )
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DeepPurple800.copy(alpha = bgAlpha),
                        DeepPurple900.copy(alpha = bgAlpha)
                    )
                )
            )
            .border(
                width = (1.5 + 1.5 * glowAlpha).dp,
                color = borderColor,
                shape = RoundedCornerShape(28.dp)
            )
            .blur(radius = (0.5f * glowAlpha).dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            DockTabItem(
                label = "捕获",
                isActive = dockState.activeTab == DockTab.CAPTURE,
                badgeCount = if (dockState.activeTab == DockTab.CAPTURE) collectedCount else null,
                glowAlpha = glowAlpha,
                onClick = { onTabSelected(DockTab.CAPTURE) }
            )

            // 分隔线
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(28.dp)
                    .background(Lavender.copy(alpha = 0.2f))
            )

            DockTabItem(
                label = "睡眠",
                isActive = dockState.activeTab == DockTab.SLEEP,
                badgeCount = null,
                glowAlpha = glowAlpha,
                onClick = { onTabSelected(DockTab.SLEEP) }
            )
        }
    }
}

@Composable
private fun DockTabItem(
    label: String,
    isActive: Boolean,
    badgeCount: Int?,
    glowAlpha: Float,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isActive) DeepPurple700.copy(alpha = 0.7f) else Color.Transparent,
        animationSpec = tween(200),
        label = "tabBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isActive) StarWhite else Lavender.copy(alpha = 0.7f),
        animationSpec = tween(200),
        label = "tabText"
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
        if (badgeCount != null && badgeCount > 0) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(MidnightGlow.copy(alpha = 0.9f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeCount.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = StarWhite,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
