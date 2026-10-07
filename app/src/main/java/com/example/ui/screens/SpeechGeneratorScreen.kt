package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.generator.ScriptPersonaGenerator
import com.example.data.model.CelebrityCatalog
import com.example.ui.MimicVoiceViewModel
import com.example.ui.components.CelebrityItemCard
import com.example.ui.components.EmotionSelector
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceCard
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun SpeechGeneratorScreen(
    viewModel: MimicVoiceViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val clipboardManager = LocalClipboardManager.current

    val selectedCeleb by viewModel.selectedCelebrity.collectAsState()
    val selectedEmotion by viewModel.selectedEmotion.collectAsState()
    val generatedScript by viewModel.generatedParodyScript.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    var userIdeaText by remember {
        mutableStateOf("Biryani mein aalu hona chahiye ya nahi?")
    }

    var copyNotification by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioSurface)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .padding(bottom = 80.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = NeonAmber.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, NeonAmber)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Script Generator",
                    tint = NeonAmber,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(24.dp)
                )
            }

            Column {
                Text(
                    text = "Speech & Parody Generator",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Transform any normal topic into an epic speech",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Select Speaker Persona
        Text(
            text = "Choose Speaker Persona",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(CelebrityCatalog.celebrities) { celeb ->
                CelebrityItemCard(
                    celebrity = celeb,
                    isSelected = celeb.id == selectedCeleb.id,
                    onClick = { viewModel.selectCelebrity(celeb) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Emotion selector
        Text(
            text = "Emotion Delivery",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        EmotionSelector(
            selectedEmotion = selectedEmotion,
            onEmotionSelected = { viewModel.selectEmotion(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Preset Fun Prompts
        Text(
            text = "Quick Topic Ideas",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        val promptScrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(promptScrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ScriptPersonaGenerator.samplePrompts.forEach { sample ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = StudioSurfaceCard,
                    border = BorderStroke(1.dp, Color(0xFF2C2C40)),
                    modifier = Modifier.clickable {
                        userIdeaText = sample
                        viewModel.generateParodyScript(sample)
                    }
                ) {
                    Text(
                        text = sample,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Input prompt
        Text(
            text = "Your Topic or Sentence",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = userIdeaText,
            onValueChange = { userIdeaText = it },
            placeholder = {
                Text(
                    text = "e.g., Traffic jam, Late for meeting, Weekend plans...",
                    color = TextTertiary,
                    fontSize = 13.sp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("parody_input_field"),
            shape = RoundedCornerShape(14.dp),
            minLines = 2,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonAmber,
                unfocusedBorderColor = Color(0xFF2C2C40),
                focusedContainerColor = StudioSurfaceCard,
                unfocusedContainerColor = StudioSurfaceCard,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { viewModel.generateParodyScript(userIdeaText) },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("generate_parody_script_button")
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Stylize",
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Stylize in ${selectedCeleb.name}'s Style",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }

        // Generated Script Output Card
        if (generatedScript.isNotBlank()) {
            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                border = BorderStroke(1.dp, Color(selectedCeleb.accentColorHex).copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(selectedCeleb.avatarEmoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${selectedCeleb.name}'s Speech",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(generatedScript))
                                copyNotification = true
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy script",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = generatedScript,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Speak stylized script button
                    Button(
                        onClick = {
                            if (isPlaying) viewModel.stopAudio()
                            else viewModel.synthesizeAndPlay()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPlaying) Color(0xFFD32F2F) else NeonCyan
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("play_stylized_script_button")
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                color = Color.Black,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Synthesizing Audio...", color = Color.Black)
                        } else {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.VolumeUp,
                                contentDescription = "Speak",
                                tint = if (isPlaying) Color.White else Color.Black
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isPlaying) "Stop Audio" else "Speak In ${selectedCeleb.name}'s Voice",
                                color = if (isPlaying) Color.White else Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
