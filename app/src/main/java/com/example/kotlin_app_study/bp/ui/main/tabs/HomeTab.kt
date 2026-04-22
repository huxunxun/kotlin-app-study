package com.example.kotlin_app_study.bp.ui.main.tabs

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kotlin_app_study.bp.Routes
import com.example.kotlin_app_study.bp.data.BPRepository
import com.example.kotlin_app_study.bp.theme.BPColors
import com.example.kotlin_app_study.bp.ui.components.BPCard
import com.example.kotlin_app_study.bp.util.TimeFormat

@Composable
fun HomeTab(modifier: Modifier = Modifier, onNavigate: (String) -> Unit) {
    val bp by BPRepository.bpRecords.collectAsStateWithLifecycle()
    val bs by BPRepository.bsRecords.collectAsStateWithLifecycle()
    val hr by BPRepository.hrRecords.collectAsStateWithLifecycle()
    val articles = BPRepository.knowledgeArticles

    val lastBp = bp.firstOrNull()
    val lastBs = bs.firstOrNull()
    val lastHr = hr.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "主页面",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )
        }

        item {
            HRBigCard(
                lastTime = lastHr?.let { TimeFormat.formatTime(it.timestamp) } ?: "--:--",
                onMeasure = { onNavigate(Routes.HRMeasure) },
                onHistory = { onNavigate(Routes.HRDetail) }
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "血压",
                    valueLine = if (lastBp != null) "${lastBp.systolic}/${lastBp.diastolic}" else "--/--",
                    valueUnit = "mmHg",
                    icon = "🩺",
                    onCardClick = { onNavigate(Routes.BPDetail) },
                    onAdd = { onNavigate(Routes.BPAdd) }
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "血糖",
                    valueLine = lastBs?.let { String.format("%.1f", it.mgDl) } ?: "--",
                    valueUnit = "mg/dL",
                    icon = "🩸",
                    onCardClick = { onNavigate(Routes.BSDetail) },
                    onAdd = { onNavigate(Routes.BSAdd) }
                )
            }
        }

        item {
            Text(
                "资讯与知识",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(articles.take(6), key = { it.id }) { article ->
            KnowledgeBanner(
                title = article.title,
                emoji = article.emoji,
                color = article.bannerColor,
                onClick = { onNavigate(Routes.knowledgeDetail(article.id)) }
            )
        }
    }
}

@Composable
private fun HRBigCard(lastTime: String, onMeasure: () -> Unit, onHistory: () -> Unit) {
    BPCard {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 心形 + 指纹叠加图标占位
                Box(modifier = Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Rounded.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFE34A4A),
                        modifier = Modifier.size(72.dp)
                    )
                    Box(
                        modifier = Modifier
                            .padding(start = 18.dp, top = 22.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1B2447)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.Fingerprint,
                            contentDescription = null,
                            tint = Color(0xFF34D6C1),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(BPColors.Primary)
                        .clickable { onMeasure() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "立刻测量",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Icon(
                            Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(BPColors.SurfaceVariant)
                    .clickable { onHistory() }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "上次记录:",
                        color = BPColors.OnSurfaceVariant,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        lastTime,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.weight(1f))
                    Text("历史", color = BPColors.OnSurfaceVariant, fontSize = 14.sp)
                    Icon(
                        Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        tint = BPColors.OnSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    valueLine: String,
    valueUnit: String,
    icon: String,
    onCardClick: () -> Unit,
    onAdd: () -> Unit,
) {
    BPCard(modifier = modifier, onClick = onCardClick) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 56.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                title,
                color = BPColors.OnSurfaceVariant,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    valueLine,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    valueUnit,
                    color = BPColors.OnSurfaceVariant,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(BPColors.Primary)
                    .clickable { onAdd() },
                contentAlignment = Alignment.Center
            ) {
                Text("记录", color = MaterialTheme.colorScheme.onPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun KnowledgeBanner(title: String, emoji: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .clickable { onClick() }
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Text(emoji, fontSize = 40.sp)
        }
    }
}
