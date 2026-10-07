package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.CelebrityCategory
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StudioSurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CelebrityItemCard(
    celebrity: Celebrity,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) {
        Color(celebrity.accentColorHex)
    } else {
        Color(0xFF2C2C3E)
    }

    val glowBrush = if (isSelected) {
        Brush.horizontalGradient(
            listOf(
                Color(celebrity.accentColorHex).copy(alpha = 0.25f),
                Color(celebrity.secondaryColorHex).copy(alpha = 0.15f)
            )
        )
    } else {
        Brush.horizontalGradient(
            listOf(StudioSurfaceCard, StudioSurfaceCard)
        )
    }

    Card(
        modifier = modifier
            .width(160.dp)
            .clickable(onClick = onClick)
            .testTag("celebrity_card_${celebrity.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier
                .background(glowBrush)
                .padding(12.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar Circle with emoji
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(celebrity.accentColorHex).copy(alpha = 0.2f))
                        .border(
                            1.5.dp,
                            Color(celebrity.accentColorHex),
                            CircleShape
                        )
                ) {
                    Text(
                        text = celebrity.avatarEmoji,
                        fontSize = 26.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = celebrity.name,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = celebrity.urduName,
                    color = Color(celebrity.accentColorHex),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Category pill
                Surface(
                    color = if (celebrity.category == CelebrityCategory.POLITICAL) {
                        Color(0xFF1B5E20).copy(alpha = 0.4f)
                    } else {
                        Color(0xFF880E4F).copy(alpha = 0.4f)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (celebrity.category == CelebrityCategory.POLITICAL) "POLITICAL" else "BOLLYWOOD",
                        color = if (celebrity.category == CelebrityCategory.POLITICAL) Color(0xFF81C784) else Color(0xFFFF80AB),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
