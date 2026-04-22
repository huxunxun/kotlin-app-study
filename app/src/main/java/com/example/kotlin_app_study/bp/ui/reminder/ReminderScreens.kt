package com.example.kotlin_app_study.bp.ui.reminder

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.kotlin_app_study.bp.data.BPRepository
import com.example.kotlin_app_study.bp.data.ReminderItem
import com.example.kotlin_app_study.bp.data.ReminderType
import com.example.kotlin_app_study.bp.theme.BPColors
import com.example.kotlin_app_study.bp.ui.components.BPCard
import com.example.kotlin_app_study.bp.ui.components.BPTopBar
import com.example.kotlin_app_study.bp.ui.components.ThreeRowWheelColumn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderListScreen(onBack: () -> Unit) {
    val list by BPRepository.reminders.collectAsStateWithLifecycle()
    val byType = list.groupBy { it.type }
    var editing by remember { mutableStateOf<ReminderItem?>(null) }
    var newType by remember { mutableStateOf<ReminderType?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BPTopBar(title = "提醒", onBack = onBack)
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(BPColors.Primary)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Notifications, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(10.dp))
                    Text("设定提醒，记录健康数据。", color = Color.White, fontSize = 14.sp)
                }
            }
            ReminderType.entries.forEach { type ->
                val items = byType[type] ?: emptyList()
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text(type.zh, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.weight(1f))
                        Icon(
                            Icons.Rounded.Add,
                            contentDescription = "添加",
                            tint = BPColors.Primary,
                            modifier = Modifier.size(22.dp).clickable { newType = type }
                        )
                    }
                }
                items.forEach { it ->
                    item {
                        BPCard(onClick = { editing = it }) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "${it.hour}:${"%02d".format(it.minute)}",
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontSize = 28.sp, fontWeight = FontWeight.Bold
                                    )
                                    Text(if (it.weekDays.size == 7) "每天" else weekLabel(it.weekDays),
                                        color = BPColors.OnSurfaceVariant, fontSize = 13.sp)
                                }
                                Spacer(Modifier.weight(1f))
                                Switch(
                                    checked = it.enabled,
                                    onCheckedChange = { v -> BPRepository.toggleReminder(it.id, v) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = BPColors.Primary,
                                        uncheckedTrackColor = BPColors.SurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (editing != null || newType != null) {
        ModalBottomSheet(
            onDismissRequest = { editing = null; newType = null },
            sheetState = sheetState,
            containerColor = BPColors.Surface
        ) {
            ReminderEditSheet(
                initType = editing?.type ?: newType!!,
                initHour = editing?.hour ?: 8,
                initMinute = editing?.minute ?: 0,
                initWeekDays = editing?.weekDays ?: setOf(1, 2, 3, 4, 5, 6, 7),
                onCancel = { editing = null; newType = null },
                onSave = { hh, mm, wd ->
                    val cur = editing
                    if (cur != null) {
                        BPRepository.updateReminder(cur.copy(hour = hh, minute = mm, weekDays = wd))
                    } else {
                        BPRepository.addReminder(
                            ReminderItem(0, newType!!, hh, mm, wd, true)
                        )
                    }
                    editing = null; newType = null
                },
                onDelete = if (editing != null) {{
                    BPRepository.deleteReminder(editing!!.id); editing = null
                }} else null
            )
        }
    }
}

@Composable
private fun ReminderEditSheet(
    initType: ReminderType,
    initHour: Int,
    initMinute: Int,
    initWeekDays: Set<Int>,
    onCancel: () -> Unit,
    onSave: (Int, Int, Set<Int>) -> Unit,
    onDelete: (() -> Unit)?
) {
    var hour by remember { mutableIntStateOf(initHour) }
    var minute by remember { mutableIntStateOf(initMinute) }
    val weekDays = remember { initWeekDays.toMutableSet() }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("${initType.zh}提醒", color = MaterialTheme.colorScheme.onBackground, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        // 7 天圆按钮
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            val days = listOf("M" to 1, "T" to 2, "W" to 3, "T" to 4, "F" to 5, "S" to 6, "S" to 7)
            days.forEach { (lbl, idx) ->
                val sel = idx in weekDays
                Box(
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp))
                        .background(if (sel) BPColors.Primary else BPColors.SurfaceVariant)
                        .clickable {
                            if (sel) weekDays.remove(idx) else weekDays.add(idx)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(lbl, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThreeRowWheelColumn(
                range = 0..23, value = hour,
                onValueChange = { hour = it },
                centerFontSize = 36, sideFontSize = 22, rowHeightDp = 48,
                modifier = Modifier.width(80.dp)
            )
            Text(":", color = MaterialTheme.colorScheme.onBackground, fontSize = 32.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp))
            ThreeRowWheelColumn(
                range = 0..59, value = minute,
                onValueChange = { minute = it },
                centerFontSize = 36, sideFontSize = 22, rowHeightDp = 48,
                modifier = Modifier.width(80.dp)
            )
        }
        Spacer(Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier.weight(1f).height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, BPColors.Primary, RoundedCornerShape(24.dp))
                    .clickable { onCancel() },
                contentAlignment = Alignment.Center
            ) { Text("取消", color = BPColors.Primary, fontWeight = FontWeight.Bold) }
            Box(
                modifier = Modifier.weight(1f).height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(BPColors.Primary)
                    .clickable { onSave(hour, minute, weekDays.toSet()) },
                contentAlignment = Alignment.Center
            ) { Text("保存", color = Color.White, fontWeight = FontWeight.Bold) }
        }
        if (onDelete != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                "删除该提醒",
                color = BPColors.Danger,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onDelete() },
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

private fun weekLabel(days: Set<Int>): String {
    val names = mapOf(1 to "一", 2 to "二", 3 to "三", 4 to "四", 5 to "五", 6 to "六", 7 to "日")
    return days.toSortedSet().joinToString("，") { "周${names[it]}" }
}
