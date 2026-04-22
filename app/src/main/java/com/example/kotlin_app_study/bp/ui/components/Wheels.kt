package com.example.kotlin_app_study.bp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_app_study.bp.theme.BPColors
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 3 行式垂直滚轮（截图 BP/BS Add 页同款）：
 *  - prev（上方淡色）
 *  - current（高亮，可选大字）
 *  - next（下方淡色）
 *  - 上下拖拽切换数值
 */
@Composable
fun ThreeRowWheelColumn(
    range: IntRange,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    /** 当前值的字号 */
    centerFontSize: Int = 36,
    sideFontSize: Int = 22,
    /** 中间高亮色 */
    centerColor: Color = MaterialTheme.colorScheme.onSurface,
    sideColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
    rowHeightDp: Int = 40,
) {
    val items = remember(range) { range.toList() }
    val idx = items.indexOf(value).coerceAtLeast(0)
    var dragAcc by remember(value) { mutableStateOf(0f) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (label != null) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        Box(
            modifier = Modifier
                .height((rowHeightDp * 3).dp)
                .pointerInput(items, value) {
                    detectVerticalDragGestures(
                        onDragEnd = { dragAcc = 0f },
                        onDragCancel = { dragAcc = 0f },
                    ) { _, dy ->
                        dragAcc -= dy
                        val threshold = rowHeightDp.toFloat()
                        if (abs(dragAcc) >= threshold) {
                            val steps = (dragAcc / threshold).toInt()
                            dragAcc -= steps * threshold
                            val newIdx = (idx + steps).coerceIn(0, items.size - 1)
                            if (newIdx != idx) onValueChange(items[newIdx])
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Column {
                val prev = if (idx > 0) items[idx - 1].toString() else ""
                val cur = items[idx].toString()
                val next = if (idx < items.size - 1) items[idx + 1].toString() else ""

                Text(
                    text = prev,
                    fontSize = sideFontSize.sp,
                    color = sideColor,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().height(rowHeightDp.dp).padding(2.dp)
                )
                Text(
                    text = cur,
                    fontSize = centerFontSize.sp,
                    color = centerColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().height(rowHeightDp.dp)
                )
                Text(
                    text = next,
                    fontSize = sideFontSize.sp,
                    color = sideColor,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().height(rowHeightDp.dp).padding(2.dp)
                )
            }
        }
    }
}

/**
 * 浮点 3 行滚轮（血糖整数 80 / 80.5 / 81 等）。
 * 内部按 step 单位的整数索引滑动。
 */
@Composable
fun ThreeRowWheelFloat(
    min: Float,
    max: Float,
    step: Float,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    centerFontSize: Int = 56,
    sideFontSize: Int = 28,
    rowHeightDp: Int = 56,
    formatter: (Float) -> String = { String.format("%.1f", it) },
) {
    val totalSteps = ((max - min) / step).toInt()
    val curIdx = (((value - min) / step).roundToInt()).coerceIn(0, totalSteps)
    var dragAcc by remember(value) { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .height((rowHeightDp * 3).dp)
            .pointerInput(min, max, step) {
                detectVerticalDragGestures(
                    onDragEnd = { dragAcc = 0f },
                    onDragCancel = { dragAcc = 0f },
                ) { _, dy ->
                    dragAcc -= dy
                    val threshold = rowHeightDp.toFloat()
                    if (abs(dragAcc) >= threshold) {
                        val steps = (dragAcc / threshold).toInt()
                        dragAcc -= steps * threshold
                        val newIdx = (curIdx + steps).coerceIn(0, totalSteps)
                        if (newIdx != curIdx) onValueChange(min + newIdx * step)
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val prev = if (curIdx > 0) formatter(min + (curIdx - 1) * step) else ""
            val cur = formatter(min + curIdx * step)
            val next = if (curIdx < totalSteps) formatter(min + (curIdx + 1) * step) else ""
            Text(
                prev,
                fontSize = sideFontSize.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.height(rowHeightDp.dp)
            )
            Text(
                cur,
                fontSize = centerFontSize.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.height(rowHeightDp.dp)
            )
            Text(
                next,
                fontSize = sideFontSize.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.height(rowHeightDp.dp)
            )
        }
    }
}

/**
 * 截图血压 Add 页：3 列垂直 3 行滚轮，居中行有一条横向"绿色长椭圆"高亮带跨过 3 列。
 */
@Composable
fun BPThreeColumnWheel(
    sysRange: IntRange,
    diaRange: IntRange,
    pulseRange: IntRange,
    sys: Int,
    dia: Int,
    pulse: Int,
    onChange: (sys: Int, dia: Int, pulse: Int) -> Unit,
    modifier: Modifier = Modifier,
    barColor: Color = BPColors.Accent,
) {
    val rowHeight = 44
    Box(
        modifier = modifier.fillMaxWidth().height((rowHeight * 3).dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .height(rowHeight.dp)
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(22.dp))
                .background(barColor)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThreeRowWheelColumn(
                range = sysRange, value = sys,
                onValueChange = { onChange(it, dia, pulse) },
                centerFontSize = 28, sideFontSize = 20, rowHeightDp = rowHeight,
                centerColor = MaterialTheme.colorScheme.onSurface,
                sideColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.width(80.dp)
            )
            ThreeRowWheelColumn(
                range = diaRange, value = dia,
                onValueChange = { onChange(sys, it, pulse) },
                centerFontSize = 28, sideFontSize = 20, rowHeightDp = rowHeight,
                centerColor = MaterialTheme.colorScheme.onSurface,
                sideColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.width(80.dp)
            )
            ThreeRowWheelColumn(
                range = pulseRange, value = pulse,
                onValueChange = { onChange(sys, dia, it) },
                centerFontSize = 28, sideFontSize = 20, rowHeightDp = rowHeight,
                centerColor = MaterialTheme.colorScheme.onSurface,
                sideColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.width(80.dp)
            )
        }
    }
}

/**
 * 5 列日期/时间滚轮（年 / 月 / 日 / 时 / 分），上下灰色行带下划线分隔（截图 BP Add 页）。
 */
@Composable
fun DateTimeFiveWheel(
    year: Int, month: Int, day: Int, hour: Int, minute: Int,
    onChange: (year: Int, month: Int, day: Int, hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ThreeRowWheelColumn(
            range = 1900..2100, value = year,
            onValueChange = { onChange(it, month, day, hour, minute) },
            centerFontSize = 18, sideFontSize = 14,
            modifier = Modifier.width(64.dp)
        )
        ThreeRowWheelColumn(
            range = 1..12, value = month,
            onValueChange = { onChange(year, it, day, hour, minute) },
            centerFontSize = 18, sideFontSize = 14,
            modifier = Modifier.width(56.dp)
        ) // 数字月，外面可以再标"月"
        ThreeRowWheelColumn(
            range = 1..31, value = day,
            onValueChange = { onChange(year, month, it, hour, minute) },
            centerFontSize = 18, sideFontSize = 14,
            modifier = Modifier.width(56.dp)
        )
        ThreeRowWheelColumn(
            range = 0..23, value = hour,
            onValueChange = { onChange(year, month, day, it, minute) },
            centerFontSize = 18, sideFontSize = 14,
            modifier = Modifier.width(56.dp)
        )
        ThreeRowWheelColumn(
            range = 0..59, value = minute,
            onValueChange = { onChange(year, month, day, hour, it) },
            centerFontSize = 18, sideFontSize = 14,
            modifier = Modifier.width(56.dp)
        )
    }
}
