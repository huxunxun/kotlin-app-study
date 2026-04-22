package com.example.kotlin_app_study.bp.ui.hr

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kotlin_app_study.bp.data.BPRepository
import com.example.kotlin_app_study.bp.data.Grading
import com.example.kotlin_app_study.bp.data.HRLevel
import com.example.kotlin_app_study.bp.theme.BPColors
import com.example.kotlin_app_study.bp.ui.components.BPCard
import com.example.kotlin_app_study.bp.ui.components.BPTopBar
import com.example.kotlin_app_study.bp.util.TimeFormat

/**
 * HR 测量结果页：
 *  - 大字 BPM + 心率等级
 *  - 压力 / HRV(SDNN/RMSSD) 三宫格
 *  - 整段平滑 BPM 折线
 *  - "完成" 按钮回详情页
 */
@Composable
fun HRResultScreen(onDone: () -> Unit) {
    val measurement by BPRepository.lastMeasurement.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BPTopBar(title = "测量结果", onBack = onDone)

        if (measurement == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("尚无测量数据", color = BPColors.OnSurfaceVariant)
            }
            return
        }

        val m = measurement!!
        val level = Grading.hrLevel(m.bpm)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // ===== 顶部大字 BPM =====
            BPCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.Favorite,
                            contentDescription = null,
                            tint = Color(0xFFE34A4A),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "${m.bpm}",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 80.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "BPM",
                            color = BPColors.OnSurfaceVariant,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 28.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    LevelBadge(level)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        TimeFormat.formatCnDateTime(m.timestamp),
                        color = BPColors.OnSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }

            // ===== 三宫格：压力 / SDNN / RMSSD =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatBox(
                    title = "压力",
                    value = "${m.stress}",
                    unit = "%",
                    color = stressColor(m.stress),
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    title = "SDNN",
                    value = m.sdnn?.let { "%.0f".format(it) } ?: "--",
                    unit = "ms",
                    color = BPColors.LevelNormal,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    title = "RMSSD",
                    value = m.rmssd?.let { "%.0f".format(it) } ?: "--",
                    unit = "ms",
                    color = BPColors.LevelNormal,
                    modifier = Modifier.weight(1f)
                )
            }

            // ===== 整段 BPM 折线 =====
            BPCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "心率走势",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))
                    if (m.chartData.isEmpty()) {
                        Text("数据不足", color = BPColors.OnSurfaceVariant, fontSize = 13.sp)
                    } else {
                        BpmLineChart(
                            points = m.chartData,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        // ===== 底部"完成"按钮 =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(BPColors.Primary)
                .clickable { onDone() },
            contentAlignment = Alignment.Center
        ) {
            Text("完成", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LevelBadge(level: HRLevel) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(level.color.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(level.color)
        )
        Spacer(Modifier.width(6.dp))
        Text(level.zh, color = level.color, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun StatBox(
    title: String,
    value: String,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    BPCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, color = color, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(2.dp))
                Text(unit, color = BPColors.OnSurfaceVariant, fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
            }
            Spacer(Modifier.height(4.dp))
            Text(title, color = BPColors.OnSurfaceVariant, fontSize = 12.sp)
        }
    }
}

private fun stressColor(stress: Int): Color = when {
    stress < 35 -> BPColors.LevelNormal
    stress < 70 -> BPColors.LevelElevated
    else -> BPColors.LevelStage2
}

@Composable
private fun BpmLineChart(points: List<Int>, modifier: Modifier = Modifier) {
    val lineColor = BPColors.Primary
    val gridColor = BPColors.OnSurfaceVariant.copy(alpha = 0.18f)
    val maxV = (points.maxOrNull() ?: 100).coerceAtLeast(80)
    val minV = (points.minOrNull() ?: 60).coerceAtMost(60)
    val span = (maxV - minV).coerceAtLeast(1)

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val padTop = 8f
        val padBottom = 8f
        val usableH = h - padTop - padBottom

        // 4 条横向网格线
        for (i in 0..3) {
            val y = padTop + usableH * i / 3f
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1f
            )
        }

        if (points.size < 2) return@Canvas
        val stepX = w / (points.size - 1).toFloat()
        val path = Path()
        points.forEachIndexed { i, v ->
            val x = i * stepX
            val ratio = (v - minV).toFloat() / span.toFloat()
            val y = padTop + usableH * (1f - ratio)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path = path, color = lineColor, style = Stroke(width = 4f))
    }
}
