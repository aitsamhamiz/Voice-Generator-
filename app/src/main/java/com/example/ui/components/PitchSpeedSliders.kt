package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StudioSurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PitchSpeedSliders(
    pitchSemitones: Int,
    onPitchChanged: (Int) -> Unit,
    speedMultiplier: Float,
    onSpeedChanged: (Float) -> Unit,
    ambianceVolume: Float,
    onAmbianceChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "🎛️ Real-time Pitch & Emotion Modulation",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            // Pitch Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Vocal Pitch (Semitones)",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    val pitchBadgeText = when {
                        pitchSemitones > 3 -> "+${pitchSemitones} st (High Orator ⚡)"
                        pitchSemitones > 0 -> "+${pitchSemitones} st (Sharp)"
                        pitchSemitones == 0 -> "0 st (Natural)"
                        pitchSemitones < -3 -> "${pitchSemitones} st (Deep Bass 🕶️)"
                        else -> "${pitchSemitones} st (Low)"
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF1E2838)
                    ) {
                        Text(
                            text = pitchBadgeText,
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Slider(
                    value = pitchSemitones.toFloat(),
                    onValueChange = { onPitchChanged(it.toInt()) },
                    valueRange = -12f..12f,
                    steps = 23,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonCyan,
                        activeTrackColor = NeonCyan,
                        inactiveTrackColor = Color(0xFF2C2C3E)
                    ),
                    modifier = Modifier.testTag("pitch_slider")
                )
            }

            // Speed Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Speech Cadence (Speed)",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    val speedBadge = "%.2fx".format(speedMultiplier)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF332042)
                    ) {
                        Text(
                            text = speedBadge,
                            color = NeonPurple,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Slider(
                    value = speedMultiplier,
                    onValueChange = onSpeedChanged,
                    valueRange = 0.5f..2.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonPurple,
                        activeTrackColor = NeonPurple,
                        inactiveTrackColor = Color(0xFF2C2C3E)
                    ),
                    modifier = Modifier.testTag("speed_slider")
                )
            }

            // Background Atmosphere Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Atmosphere Ambience (Crowd / Strings)",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF3B2A15)
                    ) {
                        Text(
                            text = "${(ambianceVolume * 100).toInt()}%",
                            color = NeonAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Slider(
                    value = ambianceVolume,
                    onValueChange = onAmbianceChanged,
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonAmber,
                        activeTrackColor = NeonAmber,
                        inactiveTrackColor = Color(0xFF2C2C3E)
                    ),
                    modifier = Modifier.testTag("ambiance_slider")
                )
            }
        }
    }
}
