package com.example.kotlin_app_study.bp.ui.bp

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kotlin_app_study.bp.data.BPLevel
import com.example.kotlin_app_study.bp.data.BPRepository
import com.example.kotlin_app_study.bp.data.Grading
import com.example.kotlin_app_study.bp.theme.BPColors
import com.example.kotlin_app_study.bp.ui.components.BPCard
import com.example.kotlin_app_study.bp.ui.components.BPThreeColumnWheel
import com.example.kotlin_app_study.bp.ui.components.BPTopBar
import com.example.kotlin_app_study.bp.ui.components.DateTimeFiveWheel
import com.example.kotlin_app_study.bp.ui.components.RangeBarPoint
import com.example.kotlin_app_study.bp.ui.components.ScreenshotRangeBarChart
import com.example.kotlin_app_study.bp.util.TimeFormat
import java.util.Calendar

@Composable
fun BPDetailScreen(onBack: () -> Unit, onAdd: () -> Unit) {
    val list by BPRepository.bpRecords.collectAsStateWithLifecycle()
    val sysMin = list.minOfOrNull { it.systolic } ?: 0
    val sysMax = list.maxOfOrNull { it.systolic } ?: 0
    val diaMin = list.minOfOrNull { it.diastolic } ?: 0
    val diaMax = list.maxOfOrNull { it.diastolic } ?: 0
    val year = Calendar.getInstance().get(Calendar.YEAR).toString()

    val bars = list.take(14).reversed().map { r ->
        val cal = TimeFormat.toCalendar(r.timestamp)
        RangeBarPoint(
            r.diastolic.toFloat(), r.systolic.toFloat(),
            "${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}"
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BPTopBar(title = "血压", onBack = onBack, actions = {
            Text("历史", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp,
                modifier = Modifier.padding(end = 16.dp).clickable {})
        })
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    DualStatColumn("收缩压", sysMin, sysMax, modifier = Modifier.weight(1f))
                    DualStatColumn("舒张压", diaMin, diaMax, modifier = Modifier.weight(1f))
                }
            }
            item {
                if (bars.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
                        Text("暂无血压数据", color = BPColors.OnSurfaceVariant)
                    }
                } else {
                    ScreenshotRangeBarChart(
                        points = bars,
                        yMin = 40f, yMax = 160f, yStep = 24f,
                        yearLabel = year,
                    )
                }
            }
            items(list, key = { it.id }) { r ->
                BPRecordCard(
                    timeStr = TimeFormat.formatCnDateTime(r.timestamp),
                    systolic = r.systolic, diastolic = r.diastolic,
                    pulse = r.pulse,
                    level = Grading.bpLevel(r.systolic, r.diastolic)
                )
            }
            item { Spacer(Modifier.height(72.dp)) }
        }
        Box(
            modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp)
                .clip(RoundedCornerShape(28.dp)).background(BPColors.Primary)
                .clickable { onAdd() },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Add, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(6.dp))
                Text("新增", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DualStatColumn(title: String, mn: Int, mx: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(title, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        Row {
            Column(modifier = Modifier.weight(1f)) {
                Text("最小值", color = BPColors.OnSurfaceVariant, fontSize = 13.sp)
                Text("$mn", color = MaterialTheme.colorScheme.onBackground, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("最大值", color = BPColors.OnSurfaceVariant, fontSize = 13.sp)
                Text("$mx", color = MaterialTheme.colorScheme.onBackground, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun BPRecordCard(timeStr: String, systolic: Int, diastolic: Int, pulse: Int, level: BPLevel) {
    BPCard {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(timeStr, color = BPColors.OnSurfaceVariant, fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("$systolic", color = MaterialTheme.colorScheme.onBackground, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    Box(modifier = Modifier.width(36.dp).height(2.dp).background(BPColors.Accent))
                    Text("$diastolic", color = MaterialTheme.colorScheme.onBackground, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(20.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(level.color))
                        Spacer(Modifier.width(6.dp))
                        Text(level.zh, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("${pulse} BPM", color = BPColors.OnSurfaceVariant, fontSize = 13.sp)
                }
            }
        }
    }
}

// ==================== BP Add (Wheel UI) ====================

@Composable
fun BPAddScreen(onBack: () -> Unit, onSaved: () -> Unit) {
    val activity = com.example.kotlin_app_study.bp.ads.rememberActivity()
    var sys by remember { mutableIntStateOf(100) }
    var dia by remember { mutableIntStateOf(75) }
    var pulse by remember { mutableIntStateOf(70) }
    val cal = remember { Calendar.getInstance() }
    var year by remember { mutableIntStateOf(cal.get(Calendar.YEAR)) }
    var month by remember { mutableIntStateOf(cal.get(Calendar.MONTH) + 1) }
    var day by remember { mutableIntStateOf(cal.get(Calendar.DAY_OF_MONTH)) }
    var hour by remember { mutableIntStateOf(cal.get(Calendar.HOUR_OF_DAY)) }
    var minute by remember { mutableIntStateOf(cal.get(Calendar.MINUTE)) }

    val level = Grading.bpLevel(sys, dia)

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BPTopBar(title = "新纪录", onBack = onBack, actions = {
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(BPColors.Primary)
                    .clickable {
                        BPRepository.addBP(
                            systolic = sys, diastolic = dia, pulse = pulse,
                            timestamp = TimeFormat.calendarToTimestamp(year, month, day, hour, minute),
                            note = ""
                        )
                        com.example.kotlin_app_study.bp.ads.showInterstitial(activity) { onSaved() }
                    }
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("保存", color = Color.White, fontWeight = FontWeight.Bold)
            }
        })
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Text("收缩压", color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.width(80.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Text("舒张压", color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.width(80.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Text("脉搏", color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.width(80.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
            item {
                BPThreeColumnWheel(
                    sysRange = 60..200, diaRange = 30..130, pulseRange = 30..200,
                    sys = sys, dia = dia, pulse = pulse,
                    onChange = { s, d, p -> sys = s; dia = d; pulse = p }
                )
            }
            item {
                BPCard {
                    Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(level.color))
                            Spacer(Modifier.width(8.dp))
                            Text(level.zh, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(level.advice, color = BPColors.OnSurfaceVariant, fontSize = 13.sp)
                        Spacer(Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(BPLevel.LOW, BPLevel.NORMAL, BPLevel.ELEVATED, BPLevel.STAGE1, BPLevel.STAGE2, BPLevel.CRISIS).forEach { l ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(if (l == level) l.color else l.color.copy(alpha = 0.4f))
                                )
                            }
                        }
                    }
                }
            }
            item {
                BPCard {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("日期&时间", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.weight(1f))
                            Text("备注 ✏️", color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        DateTimeFiveWheel(year, month, day, hour, minute, onChange = { y, mo, d, h, mi ->
                            year = y; month = mo; day = d; hour = h; minute = mi
                        })
                    }
                }
            }
        }
    }
}

// ==================== BP History ====================

@Composable
fun BPHistoryScreen(onBack: () -> Unit) {
    val list by BPRepository.bpRecords.collectAsStateWithLifecycle()
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BPTopBar(title = "血压·历史", onBack = onBack)
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(list, key = { it.id }) { r ->
                BPRecordCard(
                    TimeFormat.formatCnDateTime(r.timestamp), r.systolic, r.diastolic, r.pulse,
                    Grading.bpLevel(r.systolic, r.diastolic)
                )
            }
        }
    }
}
