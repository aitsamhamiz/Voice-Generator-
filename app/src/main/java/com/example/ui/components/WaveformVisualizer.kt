package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import kotlin.math.sin

@Composable
fun WaveformVisualizer(
    amplitude: Float,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 32,
    height: Dp = 64.dp,
    primaryColor: Color = NeonCyan,
    secondaryColor: Color = NeonPurple
) {
    val transition = rememberInfiniteTransition(label = "wave_anim")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val smoothedAmp = remember { Animatable(0.05f) }
    LaunchedEffect(amplitude, isActive) {
        val target = if (isActive) amplitude.coerceIn(0.12f, 1.0f) else 0.06f
        smoothedAmp.animateTo(
            targetValue = target,
            animationSpec = tween(durationMillis = 80)
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val width = size.width
        val canvasHeight = size.height
        val totalSpacing = width / barCount
        val barWidth = (totalSpacing * 0.55f).coerceAtLeast(3f)

        val gradient = Brush.verticalGradient(
            colors = listOf(
                secondaryColor,
                primaryColor,
                NeonAmber
            ),
            startY = 0f,
            endY = canvasHeight
        )

        for (i in 0 until barCount) {
            val normalizedX = i.toFloat() / barCount
            // Sine modulation for natural fluid wave movement
            val waveMod = if (isActive) {
                0.5f + 0.5f * sin(normalizedX * 12.0f + phase)
            } else {
                0.2f + 0.2f * sin(normalizedX * 6.0f)
            }

            // Bell curve emphasis towards middle
            val bell = sin(normalizedX * 3.14159f)
            val computedHeight = (canvasHeight * smoothedAmp.value * waveMod * (0.4f + 0.6f * bell))
                .coerceIn(4f, canvasHeight * 0.95f)

            val x = i * totalSpacing + (totalSpacing - barWidth) / 2
            val topY = (canvasHeight - computedHeight) / 2

            drawRoundRect(
                brush = gradient,
                topLeft = Offset(x, topY),
                size = Size(barWidth, computedHeight),
                cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
            )
        }
    }
}
