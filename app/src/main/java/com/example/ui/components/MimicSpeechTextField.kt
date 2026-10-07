package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Celebrity
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StudioSurfaceCard
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun MimicSpeechTextField(
    text: String,
    onTextChanged: (String) -> Unit,
    selectedCelebrity: Celebrity,
    onSpeakClick: () -> Unit,
    isPlaying: Boolean,
    isProcessing: Boolean,
    modifier: Modifier = Modifier,
    onRandomPhraseClick: (() -> Unit)? = null,
    onStylizeClick: (() -> Unit)? = null
) {
    val clipboardManager = LocalClipboardManager.current
    val accentColor = Color(selectedCelebrity.accentColorHex)

    val wordCount = remember(text) {
        if (text.isBlank()) 0
        else text.trim().split("\\s+".toRegex()).size
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("mimic_speech_text_field_container"),
        shape = RoundedCornerShape(20.dp),
        color = StudioSurfaceCard,
        border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.2f))
                            .border(1.dp, accentColor, CircleShape)
                    ) {
                        Text(selectedCelebrity.avatarEmoji, fontSize = 16.sp)
                    }

                    Column {
                        Text(
                            text = "Speech Text for ${selectedCelebrity.name}",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Timbre: ${selectedCelebrity.voiceProfile.timbre}",
                            color = NeonAmber,
                            fontSize = 11.sp
                        )
                    }
                }

                // Header Utility Actions (Paste & Clear)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            val clipData = clipboardManager.getText()
                            if (clipData != null && clipData.text.isNotBlank()) {
                                onTextChanged(clipData.text)
                            }
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("btn_paste_speech")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = "Paste",
                            tint = NeonCyan,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    if (text.isNotEmpty()) {
                        IconButton(
                            onClick = { onTextChanged("") },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("btn_clear_speech")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = TextTertiary,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // The Core Text Input Box
            OutlinedTextField(
                value = text,
                onValueChange = onTextChanged,
                placeholder = {
                    Text(
                        text = "Type text to speak in ${selectedCelebrity.name}'s voice...\n(Supports Urdu, Roman Urdu, Hindi, & English)",
                        color = TextTertiary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                },
                minLines = 3,
                maxLines = 7,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mimic_speech_input_field"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = Color(0xFF2C2C42),
                    focusedContainerColor = StudioSurfaceVariant,
                    unfocusedContainerColor = StudioSurfaceVariant,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = accentColor
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata & Action Tool Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Character & Word Counter
                Text(
                    text = "$wordCount words • ${text.length} chars",
                    color = TextTertiary,
                    fontSize = 11.sp
                )

                // Quick Tools
                val toolScroll = rememberScrollState()
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Dramatic Pause helper
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = StudioSurfaceVariant,
                        border = BorderStroke(1.dp, Color(0xFF2C2C40)),
                        modifier = Modifier
                            .clickable {
                                onTextChanged(if (text.endsWith(" ")) "$text... " else "$text ... ")
                            }
                            .testTag("btn_add_pause")
                    ) {
                        Text(
                            text = "+ Pause (...)",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                        )
                    }

                    // Random Iconic Line
                    if (onRandomPhraseClick != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = StudioSurfaceVariant,
                            border = BorderStroke(1.dp, NeonAmber.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .clickable(onClick = onRandomPhraseClick)
                                .testTag("btn_random_phrase")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Random",
                                    tint = NeonAmber,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Quote",
                                    color = NeonAmber,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Stylize Parody Button
                    if (onStylizeClick != null && text.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NeonPurple.copy(alpha = 0.18f),
                            border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .clickable(onClick = onStylizeClick)
                                .testTag("btn_stylize_text")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Stylize",
                                    tint = NeonPurple,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Stylize",
                                    color = NeonPurple,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Speak Button
            Button(
                onClick = onSpeakClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_speak_speech_field"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPlaying) Color(0xFFD32F2F) else accentColor
                )
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Synthesizing Speech...",
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
                        text = "Stop Audio",
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
                        text = "Speak in ${selectedCelebrity.name}'s Voice",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
