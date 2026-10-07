package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CelebrityCatalog
import com.example.data.model.CelebrityCategory
import com.example.ui.MimicVoiceViewModel
import com.example.ui.components.CelebrityItemCard
import com.example.ui.components.EffectSelectorGrid
import com.example.ui.components.EmotionSelector
import com.example.ui.components.PitchSpeedSliders
import com.example.ui.components.VoiceProfileSelector
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
fun HomeScreen(
    viewModel: MimicVoiceViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val selectedCeleb by viewModel.selectedCelebrity.collectAsState()
    val categoryFilter by viewModel.categoryFilter.collectAsState()
    val selectedEmotion by viewModel.selectedEmotion.collectAsState()
    val selectedEffect by viewModel.selectedEffect.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    val pitchOffset by viewModel.pitchOffsetSemitones.collectAsState()
    val speedMultiplier by viewModel.speedMultiplier.collectAsState()
    val ambianceVolume by viewModel.ambianceVolume.collectAsState()

    val isProcessing by viewModel.isProcessing.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val playbackAmp by viewModel.playbackAmplitude.collectAsState()
    val currentPcm by viewModel.currentPcmBuffer.collectAsState()
    val previewPlayingCelebId by viewModel.previewPlayingCelebrityId.collectAsState()

    var showVoiceProfilesList by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var saveTitleInput by remember { mutableStateOf("") }

    val filteredCelebs = remember(categoryFilter) {
        if (categoryFilter == null) CelebrityCatalog.celebrities
        else CelebrityCatalog.celebrities.filter { it.category == categoryFilter }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioSurface)
            .verticalScroll(scrollState)
            .padding(bottom = 90.dp)
    ) {
        // Hero Stage Header Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_banner),
                contentDescription = "Studio Stage Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Dark vignette overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                StudioSurface.copy(alpha = 0.8f),
                                StudioSurface
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = NeonCyan.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, NeonCyan)
                    ) {
                        Text(
                            text = "AI VOICE MIMICRY FX",
                            color = NeonCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        color = NeonPurple.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "7 ICONIC FIGURES",
                            color = NeonPurple,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Text-to-Speech Mimicry Studio",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Live Audio Equalizer Waveform
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
            border = BorderStroke(1.dp, Color(0xFF2E2E42))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isPlaying) NeonEmerald else TextTertiary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPlaying) "OUTPUT ACTIVE" else "STANDBY EQUALIZER",
                            color = if (isPlaying) NeonEmerald else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = selectedEffect.title,
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                WaveformVisualizer(
                    amplitude = playbackAmp,
                    isActive = isPlaying,
                    height = 48.dp,
                    primaryColor = Color(selectedCeleb.accentColorHex),
                    secondaryColor = NeonCyan
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = statusMessage,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Public Figures & Voice Profiles Section
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (showVoiceProfilesList) "Voice Profiles & Specs" else "Select Persona",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (showVoiceProfilesList) "7 acoustic profiles with live audition" else "Tap to switch speaker voice",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (showVoiceProfilesList) NeonCyan.copy(alpha = 0.2f) else StudioSurfaceVariant,
                    border = BorderStroke(1.dp, if (showVoiceProfilesList) NeonCyan else Color(0xFF2C2C40)),
                    modifier = Modifier
                        .clickable { showVoiceProfilesList = !showVoiceProfilesList }
                        .testTag("toggle_voice_profiles_view")
                ) {
                    Text(
                        text = if (showVoiceProfilesList) "Show Carousel" else "Voice Profiles 🎚️",
                        color = if (showVoiceProfilesList) NeonCyan else TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (showVoiceProfilesList) {
                // Detailed Voice Profile Selector Component
                VoiceProfileSelector(
                    selectedCelebrity = selectedCeleb,
                    onCelebritySelected = { viewModel.selectCelebrity(it) },
                    onPreviewVoice = { viewModel.previewVoice(it) },
                    isPlayingPreview = isPlaying,
                    playingCelebrityId = previewPlayingCelebId
                )
            } else {
                // Category Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = categoryFilter == null,
                        onClick = { viewModel.setCategoryFilter(null) },
                        label = { Text("All (7)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                            selectedLabelColor = NeonCyan
                        ),
                        modifier = Modifier.testTag("filter_all")
                    )
                    FilterChip(
                        selected = categoryFilter == CelebrityCategory.POLITICAL,
                        onClick = { viewModel.setCategoryFilter(CelebrityCategory.POLITICAL) },
                        label = { Text("🏛️ Political (4)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1B5E20).copy(alpha = 0.4f),
                            selectedLabelColor = Color(0xFF81C784)
                        ),
                        modifier = Modifier.testTag("filter_political")
                    )
                    FilterChip(
                        selected = categoryFilter == CelebrityCategory.BOLLYWOOD,
                        onClick = { viewModel.setCategoryFilter(CelebrityCategory.BOLLYWOOD) },
                        label = { Text("🎬 Bollywood (3)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF880E4F).copy(alpha = 0.4f),
                            selectedLabelColor = Color(0xFFFF80AB)
                        ),
                        modifier = Modifier.testTag("filter_bollywood")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Carousel of Celebrities
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(filteredCelebs) { celeb ->
                        CelebrityItemCard(
                            celebrity = celeb,
                            isSelected = celeb.id == selectedCeleb.id,
                            onClick = { viewModel.selectCelebrity(celeb) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Active Persona Spotlight Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
            border = BorderStroke(1.dp, Color(selectedCeleb.accentColorHex).copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(selectedCeleb.accentColorHex).copy(alpha = 0.2f))
                    ) {
                        Text(selectedCeleb.avatarEmoji, fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = selectedCeleb.name,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "(${selectedCeleb.urduName})",
                                color = Color(selectedCeleb.accentColorHex),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = selectedCeleb.tagLine,
                            color = NeonAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = selectedCeleb.description,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Emotion Synthesis Selector
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Emotion Synthesis",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            EmotionSelector(
                selectedEmotion = selectedEmotion,
                onEmotionSelected = { viewModel.selectEmotion(it) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Signature Catchphrases Quick Soundboard
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Signature Slogans & Dialogues",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedCeleb.signaturePhrases) { phrase ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = StudioSurfaceCard,
                        border = BorderStroke(1.dp, Color(0xFF2C2C40)),
                        modifier = Modifier
                            .clickable { viewModel.selectSignaturePhrase(phrase) }
                            .testTag("phrase_${phrase.title}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play phrase",
                                tint = Color(selectedCeleb.accentColorHex),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = phrase.title,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = phrase.emotion.displayName,
                                    color = TextTertiary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Custom Speech Input Box
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Text to Mimic",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Row {
                    IconButton(
                        onClick = {
                            val randomPhrase = selectedCeleb.signaturePhrases.randomOrNull()
                            if (randomPhrase != null) {
                                viewModel.updateInputText(randomPhrase.romanUrdu)
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Pick Random Quote",
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = inputText,
                onValueChange = { viewModel.updateInputText(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_speech_text"),
                shape = RoundedCornerShape(14.dp),
                minLines = 3,
                maxLines = 6,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color(0xFF2C2C40),
                    focusedContainerColor = StudioSurfaceCard,
                    unfocusedContainerColor = StudioSurfaceCard,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                placeholder = {
                    Text(
                        text = "Enter text in Urdu, Hindi, or English to mimic in ${selectedCeleb.name}'s voice...",
                        color = TextTertiary,
                        fontSize = 13.sp
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Audio Effects Grid
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Audio Effect Preset",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            EffectSelectorGrid(
                selectedEffect = selectedEffect,
                onEffectSelected = { viewModel.selectEffect(it) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Real-time Modulation Sliders
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            PitchSpeedSliders(
                pitchSemitones = pitchOffset,
                onPitchChanged = { viewModel.setPitchOffset(it) },
                speedMultiplier = speedMultiplier,
                onSpeedChanged = { viewModel.setSpeedMultiplier(it) },
                ambianceVolume = ambianceVolume,
                onAmbianceChanged = { viewModel.setAmbianceVolume(it) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Main Action Mimic Button & Save Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (isPlaying) viewModel.stopAudio()
                    else viewModel.synthesizeAndPlay()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("mimic_voice_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPlaying) Color(0xFFD32F2F) else NeonCyan
                )
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Synthesizing...",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                } else if (isPlaying) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Stop Speech",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Speak",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Mimic in ${selectedCeleb.name}'s Voice",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Save audio button
            if (currentPcm != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = StudioSurfaceCard,
                    border = BorderStroke(1.dp, NeonPurple),
                    modifier = Modifier
                        .clickable { viewModel.saveCurrentClip() }
                        .height(52.dp)
                        .testTag("save_audio_clip_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = "Save Clip",
                            tint = NeonPurple
                        )
                    }
                }
            }
        }
    }
}
