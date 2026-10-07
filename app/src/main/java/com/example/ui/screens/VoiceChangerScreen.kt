package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.CelebrityCatalog
import com.example.ui.MimicVoiceViewModel
import com.example.ui.components.CelebrityItemCard
import com.example.ui.components.EffectSelectorGrid
import com.example.ui.components.PitchSpeedSliders
import com.example.ui.components.WaveformVisualizer
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceCard
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun VoiceChangerScreen(
    viewModel: MimicVoiceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val isRecording by viewModel.isRecording.collectAsState()
    val recordingAmp by viewModel.recordingAmplitude.collectAsState()
    val recordingDurationMs by viewModel.recordingDurationMs.collectAsState()

    val selectedCeleb by viewModel.selectedCelebrity.collectAsState()
    val selectedEffect by viewModel.selectedEffect.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    val pitchOffset by viewModel.pitchOffsetSemitones.collectAsState()
    val speedMultiplier by viewModel.speedMultiplier.collectAsState()
    val ambianceVolume by viewModel.ambianceVolume.collectAsState()

    val isPlaying by viewModel.isPlaying.collectAsState()
    val playbackAmp by viewModel.playbackAmplitude.collectAsState()
    val rawMicPcm by viewModel.rawMicPcm.collectAsState()
    val currentPcm by viewModel.currentPcmBuffer.collectAsState()

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            viewModel.startMicRecording()
        }
    }

    // Pulsing circle animation for record button
    val pulseScale = remember { Animatable(1.0f) }
    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (true) {
                pulseScale.animateTo(1.22f, animationSpec = tween(500, easing = LinearEasing))
                pulseScale.animateTo(1.0f, animationSpec = tween(500, easing = LinearEasing))
            }
        } else {
            pulseScale.snapTo(1.0f)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioSurface)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .padding(bottom = 80.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Live Voice Changer",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Speak into mic & transform into political & film voices",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Surface(
                color = Color(0xFF1E2838),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, NeonCyan)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "DSP",
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SOLA DSP",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Center Studio Recording Booth Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
            border = BorderStroke(
                1.5.dp,
                if (isRecording) Color(0xFFD32F2F) else Color(0xFF2C2C40)
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Status Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isRecording -> Color(0xFFD32F2F)
                                        isPlaying -> NeonEmerald
                                        else -> TextTertiary
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when {
                                isRecording -> "RECORDING LIVE MIC"
                                isPlaying -> "PLAYING MODULATED AUDIO"
                                rawMicPcm != null -> "AUDIO CAPTURED"
                                else -> "MIC READY"
                            },
                            color = when {
                                isRecording -> Color(0xFFD32F2F)
                                isPlaying -> NeonEmerald
                                else -> TextSecondary
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (isRecording) {
                        val seconds = (recordingDurationMs / 1000)
                        val millis = (recordingDurationMs % 1000) / 100
                        Text(
                            text = "%02d.%ds".format(seconds, millis),
                            color = Color(0xFFD32F2F),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Waveform Visualizer
                WaveformVisualizer(
                    amplitude = if (isRecording) recordingAmp else playbackAmp,
                    isActive = isRecording || isPlaying,
                    height = 64.dp,
                    primaryColor = if (isRecording) Color(0xFFD32F2F) else NeonCyan,
                    secondaryColor = NeonPurple
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Big Glowing Microphone Button
                Box(contentAlignment = Alignment.Center) {
                    // Outer pulsing ring when recording
                    if (isRecording) {
                        Box(
                            modifier = Modifier
                                .size(92.dp)
                                .scale(pulseScale.value)
                                .clip(CircleShape)
                                .background(Color(0xFFD32F2F).copy(alpha = 0.25f))
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        if (isRecording) Color(0xFFEF5350) else NeonCyan,
                                        if (isRecording) Color(0xFFB71C1C) else Color(0xFF007A8A)
                                    )
                                )
                            )
                            .clickable {
                                if (isRecording) {
                                    viewModel.stopMicRecording()
                                } else {
                                    if (hasPermission) {
                                        viewModel.startMicRecording()
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                            }
                            .testTag("record_mic_button")
                    ) {
                        Icon(
                            imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = if (isRecording) "Stop Recording" else "Record Mic",
                            tint = Color.Black,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isRecording) "Tap to Stop & Apply FX" else "Tap Mic to Record Your Voice",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = statusMessage,
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                // Play / Save buttons when audio is available
                if (currentPcm != null && !isRecording) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                if (isPlaying) viewModel.stopAudio()
                                else viewModel.playCurrentBuffer()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlaying) Color(0xFFD32F2F) else NeonEmerald
                            ),
                            modifier = Modifier.testTag("play_recorded_audio_button")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = "Play Modulated Audio",
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isPlaying) "Stop" else "Listen With FX",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { viewModel.saveCurrentClip("Mic FX: ${selectedCeleb.name}") },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StudioSurfaceVariant
                            ),
                            border = BorderStroke(1.dp, NeonPurple),
                            modifier = Modifier.testTag("save_mic_clip_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = "Save",
                                tint = NeonPurple
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Save Clip",
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Target Persona Selection
        Text(
            text = "Target Persona (Vocal Profile)",
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Your mic recording will shift pitch & tone to match this figure",
            color = TextSecondary,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(CelebrityCatalog.celebrities) { celeb ->
                CelebrityItemCard(
                    celebrity = celeb,
                    isSelected = celeb.id == selectedCeleb.id,
                    onClick = { viewModel.selectCelebrity(celeb) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Audio Effects Grid
        Text(
            text = "Voice FX & Acoustic Environment",
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        EffectSelectorGrid(
            selectedEffect = selectedEffect,
            onEffectSelected = { viewModel.selectEffect(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Real-time Modulation Sliders
        PitchSpeedSliders(
            pitchSemitones = pitchOffset,
            onPitchChanged = { viewModel.setPitchOffset(it) },
            speedMultiplier = speedMultiplier,
            onSpeedChanged = { viewModel.setSpeedMultiplier(it) },
            ambianceVolume = ambianceVolume,
            onAmbianceChanged = { viewModel.setAmbianceVolume(it) }
        )
    }
}
