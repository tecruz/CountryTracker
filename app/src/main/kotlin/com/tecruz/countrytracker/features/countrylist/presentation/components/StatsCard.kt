package com.tecruz.countrytracker.features.countrylist.presentation.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecruz.countrytracker.R
import com.tecruz.countrytracker.core.designsystem.PrimaryGreen
import com.tecruz.countrytracker.core.designsystem.StatsGradientEnd
import com.tecruz.countrytracker.core.designsystem.StatsGradientMid
import com.tecruz.countrytracker.core.designsystem.StatsGradientStart
import com.tecruz.countrytracker.core.designsystem.StatsIconComplete
import com.tecruz.countrytracker.core.designsystem.StatsIconTotal
import com.tecruz.countrytracker.core.designsystem.StatsIconVisited
import com.tecruz.countrytracker.core.designsystem.StatsLabelColor
import com.tecruz.countrytracker.core.designsystem.StatsValueColor

@Composable
fun StatsCard(visitedCount: Int, totalCount: Int, percentage: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 8.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            StatsGradientStart,
                            StatsGradientMid,
                            StatsGradientEnd,
                        ),
                    ),
                )
                .padding(28.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatItem(
                    icon = Icons.Default.Flag,
                    value = visitedCount.toString(),
                    label = stringResource(R.string.stats_visited),
                    iconTint = StatsIconVisited,
                )

                HorizontalDivider(
                    modifier = Modifier
                        .height(60.dp)
                        .width(2.dp),
                    color = PrimaryGreen.copy(alpha = 0.2f),
                )

                StatItem(
                    icon = Icons.Default.Public,
                    value = totalCount.toString(),
                    label = stringResource(R.string.stats_total),
                    iconTint = StatsIconTotal,
                )

                HorizontalDivider(
                    modifier = Modifier
                        .height(60.dp)
                        .width(2.dp),
                    color = PrimaryGreen.copy(alpha = 0.2f),
                )

                StatItem(
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    value = "$percentage%",
                    label = stringResource(R.string.stats_complete),
                    iconTint = StatsIconComplete,
                )
            }
        }
    }
}

@Composable
fun StatItem(icon: ImageVector, value: String, label: String, iconTint: Color, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(28.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold,
            color = StatsValueColor,
            lineHeight = 40.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = StatsLabelColor,
            letterSpacing = 0.8.sp,
            textAlign = TextAlign.Center,
        )
    }
}
