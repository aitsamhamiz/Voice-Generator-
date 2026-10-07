package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Celebrity
import com.example.data.model.CelebrityCatalog
import com.example.data.model.CelebrityCategory
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StudioSurfaceCard
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VoiceProfileSelector(
    selectedCelebrity: Celebrity,
    onCelebritySelected: (Celebrity) -> Unit,
    onPreviewVoice: (Celebrity) -> Unit,
    isPlayingPreview: Boolean,
    playingCelebrityId: String?,
    modifier: Modifier = Modifier
) {
    var categoryFilter by remember { mutableStateOf<CelebrityCategory?>(null) }
    var expandedProfileId by remember { mutableStateOf<String?>(null) }

    val filteredList = remember(categoryFilter) {
        if (categoryFilter == null) CelebrityCatalog.celebrities
        else CelebrityCatalog.celebrities.filter { it.category == categoryFilter }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header & Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Voice Profiles",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Voice Profile Selector",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Acoustic modeling for 7 political & cinema legends",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            // Quick Category Chips
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                FilterChip(
                    selected = categoryFilter == null,
                    onClick = { categoryFilter = null },
                    label = { Text("All (7)", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                        selectedLabelColor = NeonCyan
                    ),
                    modifier = Modifier.testTag("filter_voice_all")
                )
                FilterChip(
                    selected = categoryFilter == CelebrityCategory.POLITICAL,
                    onClick = { categoryFilter = CelebrityCategory.POLITICAL },
                    label = { Text("🏛️ Political", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF1B5E20).copy(alpha = 0.35f),
                        selectedLabelColor = Color(0xFF81C784)
                    ),
                    modifier = Modifier.testTag("filter_voice_political")
                )
                FilterChip(
                    selected = categoryFilter == CelebrityCategory.BOLLYWOOD,
                    onClick = { categoryFilter = CelebrityCategory.BOLLYWOOD },
                    label = { Text("🎬 Film", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF880E4F).copy(alpha = 0.35f),
                        selectedLabelColor = Color(0xFFFF80AB)
                    ),
                    modifier = Modifier.testTag("filter_voice_bollywood")
                )
            }
        }

        // Voice Profile Cards List
        filteredList.forEach { celeb ->
            val isSelected = celeb.id == selectedCelebrity.id
            val isPlayingThis = isPlayingPreview && playingCelebrityId == celeb.id
            val isExpanded = expandedProfileId == celeb.id

            VoiceProfileCard(
                celebrity = celeb,
                isSelected = isSelected,
                isPlayingPreview = isPlayingThis,
                isExpanded = isExpanded,
                onSelect = { onCelebritySelected(celeb) },
                onPreview = { onPreviewVoice(celeb) },
                onToggleExpand = {
                    expandedProfileId = if (isExpanded) null else celeb.id
                }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VoiceProfileCard(
    celebrity: Celebrity,
    isSelected: Boolean,
    isPlayingPreview: Boolean,
    isExpanded: Boolean,
    onSelect: () -> Unit,
    onPreview: () -> Unit,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(celebrity.accentColorHex)
    val borderColor = if (isSelected) accentColor else Color(0xFF2C2C40)

    val backgroundBrush = if (isSelected) {
        Brush.horizontalGradient(
            listOf(
                accentColor.copy(alpha = 0.16f),
                Color(celebrity.secondaryColorHex).copy(alpha = 0.08f),
                StudioSurfaceCard
            )
        )
    } else {
        Brush.horizontalGradient(
            listOf(StudioSurfaceCard, StudioSurfaceCard)
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .testTag("voice_profile_card_${celebrity.id}")
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier
                .background(backgroundBrush)
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Emoji Avatar
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.2f))
                            .border(1.5.dp, accentColor, CircleShape)
                    ) {
                        Text(celebrity.avatarEmoji, fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Name and Voice Timbre
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = celebrity.name,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "(${celebrity.urduName})",
                                color = accentColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = celebrity.voiceProfile.timbre,
                            color = NeonAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = celebrity.title,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Audition / Preview Button
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isPlayingPreview) Color(0xFFD32F2F) else accentColor.copy(alpha = 0.2f))
                            .border(1.dp, if (isPlayingPreview) Color(0xFFD32F2F) else accentColor, CircleShape)
                            .clickable(onClick = onPreview)
                            .testTag("preview_voice_${celebrity.id}")
                    ) {
                        Icon(
                            imageVector = if (isPlayingPreview) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = "Preview voice",
                            tint = if (isPlayingPreview) Color.White else accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Selection Checkmark or Expand toggle
                    if (isSelected) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(accentColor)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = onToggleExpand,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expand specs",
                                tint = TextTertiary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Acoustic Attribute Pills
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AcousticBadge(
                        label = celebrity.voiceProfile.pitchLabel,
                        color = NeonCyan
                    )
                    AcousticBadge(
                        label = celebrity.voiceProfile.cadenceSpeedLabel,
                        color = NeonPurple
                    )
                    AcousticBadge(
                        label = celebrity.voiceProfile.resonance,
                        color = NeonEmerald
                    )
                }

                // Expandable Details (Mannerisms, Preview Quote)
                AnimatedVisibility(visible = isExpanded || isSelected) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = StudioSurfaceVariant.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, Color(0xFF2C2C40)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "🎙️ Vocal Mannerisms & Cadence:",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                celebrity.voiceProfile.mannerisms.forEach { mannerism ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 1.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(accentColor)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = mannerism,
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Preview Line: \"${celebrity.voiceProfile.samplePreviewPhrase}\"",
                                    color = TextTertiary,
                                    fontSize = 10.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AcousticBadge(
    label: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}
