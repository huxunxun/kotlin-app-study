package com.example.kotlin_app_study.bp.ui.bs

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
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.kotlin_app_study.bp.data.BPRepository
import com.example.kotlin_app_study.bp.data.BSLevel
import com.example.kotlin_app_study.bp.data.BSPeriod
import com.example.kotlin_app_study.bp.data.Grading
import com.example.kotlin_app_study.bp.theme.BPColors
import com.example.kotlin_app_study.bp.ui.components.BPCard
import com.example.kotlin_app_study.bp.ui.components.BPTopBar
import com.example.kotlin_app_study.bp.ui.components.BarPoint
import com.example.kotlin_app_study.bp.ui.components.DateTimeFiveWheel
import com.example.kotlin_app_study.bp.ui.components.ScreenshotBarChart
import com.example.kotlin_app_study.bp.ui.components.ThreeRowWheelFloat
import com.example.kotlin_app_study.bp.util.TimeFormat
import java.util.Calendar

@Composable
fun BSDetailScreen(onBack: () -> Unit, onAdd: () -> Unit) {
    val list by BPRepository.bsRecords.collectAsStateWithLifecycle()
    val avg = if (list.isNotEmpty()) list.map { it.mgDl }.average().toInt() else 0
    val max = list.maxOfOrNull { it.mgDl }?.toInt() ?: 0
    val min = list.minOfOrNull { it.mgDl }?.toInt() ?: 0
    val year = Calendar.getInstance().get(Calendar.YEAR).toString()
    val bars = list.take(14).reversed().map { r ->
        val cal = TimeFormat.toCalendar(r.timestamp)
        BarPoint(r.mgDl, "${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}")
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BPTopBar(title = "血糖", onBack = onBack, actions = {
            Text("历史", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp,
                modifier = Modifier.padding(end = 16.dp).clickable {})
        })
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("$avg" to "平均", "$max" to "最大值", "$min" to "最小值").forEach { (v, l) ->
                        Column {
                            Text(v, color = MaterialTheme.colorScheme.onBackground, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                            Text(l, color = BPColors.OnSurfaceVariant, fontSize = 14.sp)
                        }
                    }
                }
            }
            item {
                if (bars.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("请添加至少一条记录以解锁统计信息", color = BPColors.OnSurfaceVariant)
                    }
                } else {
                    ScreenshotBarChart(
                        points = bars,
                        yMin = 60f, yMax = 200f, yStep = 28f, yearLabel = year
                    )
                }
            }
            items(list, key = { it.id }) { r ->
                BSRecordCard(
                    TimeFormat.formatCnDateTime(r.timestamp), r.mgDl, r.period,
                    Grading.bsLevel(r.mgDl)
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
private fun BSRecordCard(timeStr: String, mgDl: Float, period: BSPeriod, level: BSLevel) {
    BPCard {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(timeStr, color = BPColors.OnSurfaceVariant, fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(String.format("%.1f", mgDl), color = MaterialTheme.colorScheme.onBackground, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(4.dp))
                Text("mg/dL", color = BPColors.OnSurfaceVariant, fontSize = 13.sp, modifier = Modifier.padding(bottom = 6.dp))
                Spacer(Modifier.width(20.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(level.color))
                        Spacer(Modifier.width(6.dp))
                        Text(level.zh, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                    Text(period.zh, color = BPColors.OnSurfaceVariant, fontSize = 13.sp)
                }
            }
        }
    }
}

// ==================== BS Add ====================

@Composable
fun BSAddScreen(onBack: () -> Unit, onSaved: () -> Unit) {
    val activity = com.example.kotlin_app_study.bp.ads.rememberActivity()
    var mgDl by remember { mutableFloatStateOf(80f) }
    var period by remember { mutableStateOf(BSPeriod.DEFAULT) }
    val cal = remember { Calendar.getInstance() }
    var year by remember { mutableIntStateOf(cal.get(Calendar.YEAR)) }
    var month by remember { mutableIntStateOf(cal.get(Calendar.MONTH) + 1) }
    var day by remember { mutableIntStateOf(cal.get(Calendar.DAY_OF_MONTH)) }
    var hour by remember { mutableIntStateOf(cal.get(Calendar.HOUR_OF_DAY)) }
    var minute by remember { mutableIntStateOf(cal.get(Calendar.MINUTE)) }
    var menuOpen by remember { mutableStateOf(false) }

    val level = Grading.bsLevel(mgDl)

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BPTopBar(title = "新纪录", onBack = onBack, actions = {
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(BPColors.Primary)
                    .clickable {
                        BPRepository.addBS(
                            mgDl = mgDl, period = period,
                            timestamp = TimeFormat.calendarToTimestamp(year, month, day, hour, minute)
                        )
                        com.example.kotlin_app_study.bp.ads.showInterstitial(activity) { onSaved() }
                    }
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) { Text("保存", color = Color.White, fontWeight = FontWeight.Bold) }
        })

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { menuOpen = true }
                        ) {
                            Text("状态：${period.zh}", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
                            Icon(Icons.Rounded.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
                        }
                        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            BSPeriod.entries.forEach { p ->
                                DropdownMenuItem(text = { Text(p.zh) }, onClick = { period = p; menuOpen = false })
                            }
                        }
                    }
                }
            }
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        ThreeRowWheelFloat(
                            min = 30f, max = 400f, step = 0.1f,
                            value = mgDl, onValueChange = { mgDl = it },
                            centerFontSize = 56, sideFontSize = 24, rowHeightDp = 64,
                            modifier = Modifier.width(180.dp)
                        )
                        Text("mg/dL", color = BPColors.OnSurfaceVariant, fontSize = 18.sp, modifier = Modifier.padding(bottom = 16.dp))
                    }
                }
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
                        Text(level.range, color = BPColors.OnSurfaceVariant, fontSize = 13.sp)
                        Spacer(Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            BSLevel.entries.forEach { l ->
                                Box(
                                    modifier = Modifier.weight(1f).height(10.dp)
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

// ==================== BS History ====================

@Composable
fun BSHistoryScreen(onBack: () -> Unit) {
    val list by BPRepository.bsRecords.collectAsStateWithLifecycle()
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BPTopBar(title = "血糖·历史", onBack = onBack)
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(list, key = { it.id }) { r ->
                BSRecordCard(TimeFormat.formatCnDateTime(r.timestamp), r.mgDl, r.period, Grading.bsLevel(r.mgDl))
            }
        }
    }
}
