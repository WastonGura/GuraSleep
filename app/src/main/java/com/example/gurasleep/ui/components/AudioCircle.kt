package com.example.gurasleep.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.gurasleep.domain.model.CircleBody
import com.example.gurasleep.ui.theme.StarWhite

/**
 * 可交互的音频圆形
 */
@Composable
fun AudioCircle(
    circle: CircleBody,
    onDragStart: () -> Unit = {},
    onDrag: (dx: Float, dy: Float) -> Unit = {},
    onDragEnd: () -> Unit = {},
    isCollected: Boolean = false,
    modifier: Modifier = Modifier
) {
    val audio = circle.audioItem ?: return
    val def = circle.deformation
    val radiusDp = with(LocalDensity.current) {
        circle.radius.toDp()
    }

    // 形变动画
    val animScaleX by animateFloatAsState(def.scaleX, tween(200), label = "sx")
    val animScaleY by animateFloatAsState(def.scaleY, tween(200), label = "sy")
    val animRotation by animateFloatAsState(def.rotationRad, tween(200), label = "rot")

    Box(
        modifier = modifier
            .size(radiusDp * 2)
            .graphicsLayer {
                scaleX = animScaleX
                scaleY = animScaleY
                rotationZ = Math.toDegrees(animRotation.toDouble()).toFloat()
            }
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                ambientColor = audio.color.copy(alpha = 0.4f),
                spotColor = audio.color.copy(alpha = 0.6f)
            )
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        audio.color.copy(alpha = 0.7f),
                        audio.color.copy(alpha = 0.95f)
                    )
                )
            )
            .border(
                width = if (isCollected) 2.dp else 1.dp,
                color = if (isCollected) StarWhite.copy(alpha = 0.6f)
                        else audio.color.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { onDragStart() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.x, dragAmount.y)
                    },
                    onDragEnd = { onDragEnd() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = audio.icon,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                textAlign = TextAlign.Center
            )
            Text(
                text = audio.name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = StarWhite.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
