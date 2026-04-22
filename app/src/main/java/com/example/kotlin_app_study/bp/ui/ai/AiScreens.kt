package com.example.kotlin_app_study.bp.ui.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_app_study.bp.data.BPRepository
import com.example.kotlin_app_study.bp.data.HealthRisk
import com.example.kotlin_app_study.bp.data.HealthTest
import com.example.kotlin_app_study.bp.theme.BPColors
import com.example.kotlin_app_study.bp.ui.components.BPCard
import com.example.kotlin_app_study.bp.ui.components.BPTopBar

@Composable
fun AiChatScreen(onBack: () -> Unit, onHistory: () -> Unit, onStartTest: (String) -> Unit) {
    var tab by remember { mutableIntStateOf(0) }
    var showMore by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var faqOpen by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BPTopBar(title = "AI医生", onBack = onBack, actions = {
            IconButton(onClick = onHistory) {
                Icon(Icons.Rounded.History, contentDescription = "历史")
            }
        })

        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
        ) {
            // 顶部欢迎区
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                    Text("🤖", fontSize = 56.sp)
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    "欢迎使用AI医生",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 输入框
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(BPColors.SurfaceVariant)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp),
                    cursorBrush = SolidColor(BPColors.Primary),
                    modifier = Modifier.weight(1f).height(36.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send, keyboardType = KeyboardType.Text),
                    decorationBox = { inner ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (query.isEmpty()) Text("在此处输入问题", color = BPColors.OnSurfaceVariant, fontSize = 14.sp)
                            inner()
                        }
                    }
                )
                IconButton(
                    onClick = { if (query.isNotBlank()) faqOpen = query.also { query = "" } },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = null, tint = BPColors.Primary)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Tab
            Row(modifier = Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AiTabPill("推荐", selected = tab == 0, modifier = Modifier.weight(1f)) { tab = 0 }
                AiTabPill("健康检查", selected = tab == 1, modifier = Modifier.weight(1f)) { tab = 1 }
            }

            Spacer(Modifier.height(12.dp))

            if (tab == 0) {
                val recs = if (showMore) BPRepository.faqList else BPRepository.faqList.take(5)
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recs) { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { faqOpen = item.question }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("💬", fontSize = 18.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                item.question,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(">", color = BPColors.OnSurfaceVariant)
                        }
                    }
                    if (!showMore) {
                        item {
                            Text(
                                "更多",
                                color = BPColors.OnSurfaceVariant,
                                fontSize = 14.sp,
                                modifier = Modifier.fillMaxWidth().padding(top = 6.dp).clickable { showMore = true },
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(BPRepository.healthTests) { t ->
                        HealthTestCard(t, onStart = { onStartTest(t.testId) })
                    }
                }
            }
        }

        // 底部固定提示
        Text(
            "AI健康咨询由ChatGPT提供。请注意核实信息，因ChatGPT可能会出现错误。",
            color = BPColors.OnSurfaceVariant,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp).fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }

    if (faqOpen != null) {
        AlertDialog(
            onDismissRequest = { faqOpen = null },
            confirmButton = { TextButton(onClick = { faqOpen = null }) { Text("好的") } },
            title = { Text(faqOpen ?: "") },
            text = { Text("AI 健康咨询由 ChatGPT 提供。这是一个静态演示版本，没有真正的网络请求。请注意核实信息，因 ChatGPT 可能会出现错误。") }
        )
    }
}

@Composable
private fun AiTabPill(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val bg = if (selected) BPColors.PrimaryContainer else Color.Transparent
    val color = if (selected) MaterialTheme.colorScheme.onBackground else BPColors.OnSurfaceVariant
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(if (selected) "📝" else "📋", fontSize = 16.sp)
            Spacer(Modifier.width(6.dp))
            Text(label, color = color, fontSize = 15.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
        }
    }
}

@Composable
private fun HealthTestCard(t: HealthTest, onStart: () -> Unit) {
    BPCard(onClick = onStart) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(t.title, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(t.subtitle, color = BPColors.OnSurfaceVariant, fontSize = 13.sp, maxLines = 3)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⏱ ${t.duration}", color = BPColors.OnSurfaceVariant, fontSize = 13.sp)
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier.height(34.dp).clip(RoundedCornerShape(17.dp))
                        .background(BPColors.Primary).clickable { onStart() }.padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) { Text("开始", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

// ==================== Health Test runner ====================

@Composable
fun HealthTestScreen(testId: String, onBack: () -> Unit) {
    val test = BPRepository.healthTestById(testId) ?: return
    var qIdx by remember { mutableIntStateOf(0) }
    val answers: SnapshotStateList<Int> = remember { mutableStateListOf<Int>() }
    var done by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BPTopBar(title = test.title, onBack = onBack)
        if (done) {
            val risk = when {
                score <= test.lowMax -> HealthRisk.LOW
                score <= test.midMax -> HealthRisk.MID
                else -> HealthRisk.HIGH
            }
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(140.dp).clip(CircleShape).background(risk.color.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$score", color = risk.color, fontSize = 56.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(16.dp))
                Text(risk.zh, color = MaterialTheme.colorScheme.onBackground, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(
                    when (risk) {
                        HealthRisk.LOW -> "目前风险较低，请保持健康生活方式。"
                        HealthRisk.MID -> "存在一定风险，建议关注饮食、运动并定期体检。"
                        HealthRisk.HIGH -> "风险较高，请尽快咨询医生。"
                    },
                    color = BPColors.OnSurfaceVariant, fontSize = 14.sp
                )
                Spacer(Modifier.height(24.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(28.dp))
                        .background(BPColors.Primary).clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) { Text("完成", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
            }
        } else {
            val q = test.questions[qIdx]
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "${qIdx + 1} / ${test.questions.size}",
                    color = BPColors.OnSurfaceVariant, fontSize = 14.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(q.text, color = MaterialTheme.colorScheme.onBackground, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                q.options.forEach { opt ->
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).height(56.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .border(1.dp, BPColors.SurfaceVariant, RoundedCornerShape(28.dp))
                            .background(BPColors.Surface)
                            .clickable {
                                answers.add(opt.score)
                                if (qIdx == test.questions.size - 1) {
                                    score = answers.sum()
                                    done = true
                                } else {
                                    qIdx += 1
                                }
                            }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) { Text(opt.text, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp) }
                }
            }
        }
    }
}

// ==================== AI History ====================

@Composable
fun AiHistoryScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BPTopBar(title = "聊天历史", onBack = onBack)
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("📭", fontSize = 56.sp)
            Spacer(Modifier.height(8.dp))
            Text("暂无聊天历史", color = BPColors.OnSurfaceVariant)
        }
    }
}
