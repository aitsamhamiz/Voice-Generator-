package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioEffectType
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StudioSurfaceCard
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun EffectSelectorGrid(
    selectedEffect: AudioEffectType,
    onEffectSelected: (AudioEffectType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val effects = AudioEffectType.values()
        // Render in rows of 2 for easy scrolling inside parent
        for (i in effects.indices step 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EffectItem(
                    effect = effects[i],
                    isSelected = effects[i] == selectedEffect,
                    onClick = { onEffectSelected(effects[i]) },
                    modifier = Modifier.weight(1f)
                )

                if (i + 1 < effects.size) {
                    EffectItem(
                        effect = effects[i + 1],
                        isSelected = effects[i + 1] == selectedEffect,
                        onClick = { onEffectSelected(effects[i + 1]) },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun EffectItem(
    effect: AudioEffectType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) NeonCyan else Color(0xFF2C2C3E)
    val backgroundBrush = if (isSelected) {
        Brush.horizontalGradient(
            listOf(Color(0xFF003844), Color(0xFF1B2335))
        )
    } else {
        Brush.horizontalGradient(
            listOf(StudioSurfaceCard, StudioSurfaceCard)
        )
    }

    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("effect_item_${effect.name}"),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(backgroundBrush)
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = effect.iconEmoji,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = effect.title,
                        color = if (isSelected) NeonCyan else TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = effect.subtitle,
                        color = TextSecondary,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
