package com.example.kotlin_app_study.bp.ui.knowledge

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_app_study.bp.ads.AdBanner
import com.example.kotlin_app_study.bp.ads.rememberActivity
import com.example.kotlin_app_study.bp.ads.showRewarded
import com.example.kotlin_app_study.bp.data.BPRepository
import com.example.kotlin_app_study.bp.data.BPLevel
import com.example.kotlin_app_study.bp.theme.BPColors
import com.example.kotlin_app_study.bp.ui.components.BPCard
import com.example.kotlin_app_study.bp.util.currentStatusBarHeight

@Composable
fun KnowledgeDetailScreen(id: Long, onBack: () -> Unit) {
    val article = BPRepository.knowledgeById(id) ?: return
    val activity = rememberActivity()
    val statusBarTop = currentStatusBarHeight()
    var unlocked by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // 彩色 hero：背景延伸到屏顶；返回箭头按状态栏真实高度向下偏移
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(article.bannerColor)
                .height(160.dp + statusBarTop)
                .padding(top = statusBarTop)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.padding(top = 8.dp, start = 4.dp)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "返回", tint = Color.White)
            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    article.title,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(article.emoji, fontSize = 56.sp)
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                BPCard {
                    Text(
                        article.intro,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            item {
                BPCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // 当文章是"血压正常范围"，附上彩色等级图例
                        if (article.id == 1L) {
                            BPLevel.entries.forEachIndexed { idx, l ->
                                Row(verticalAlignment = Alignment.Top) {
                                    Box(
                                        modifier = Modifier.padding(top = 6.dp).size(14.dp).clip(CircleShape).background(l.color)
                                    )
                                    Spacer(Modifier.padding(start = 12.dp))
                                    Column {
                                        Text(l.zh, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        Text(l.advice, color = BPColors.OnSurfaceVariant, fontSize = 13.sp)
                                        if (idx != BPLevel.entries.size - 1) Spacer(Modifier.height(10.dp))
                                    }
                                }
                            }
                        } else {
                            article.sections.forEachIndexed { idx, s ->
                                Text(s.heading, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(6.dp))
                                Text(s.body, color = BPColors.OnSurfaceVariant, fontSize = 14.sp)
                                if (idx != article.sections.size - 1) Spacer(Modifier.height(14.dp))
                            }
                        }
                    }
                }
            }
            item {
                BPCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            if (unlocked) "💎 已解锁专家深度建议" else "💎 看一段广告即可解锁专家深度建议",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(10.dp))
                        if (unlocked) {
                            Text(
                                "针对您的健康档案，建议每周 3 次有氧运动，单次 30 分钟以上；睡前 2 小时禁水分以减少夜起；定期监测家庭自测血压并记录到 App，以便长期对比。",
                                color = BPColors.OnSurfaceVariant,
                                fontSize = 14.sp
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(22.dp))
                                    .background(BPColors.Primary)
                                    .clickable {
                                        showRewarded(
                                            activity = activity,
                                            onReward = { unlocked = true }
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                androidx.compose.foundation.layout.Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Rounded.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.size(6.dp))
                                    Text(
                                        "看广告解锁",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(20.dp)) }
            item { AdBanner(modifier = Modifier.fillMaxWidth()) }
            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}
