package com.example.kotlin_app_study.bp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_app_study.bp.theme.BPColors

data class StageSegment(val label: String, val color: Color, val weight: Float)

/**
 * 横向分级条（5 段或多段，带活动指针）。
 * BP / BS / HR 通用。
 *
 * @param progress 0..1，指针落在条上的位置
 */
@Composable
fun HorizontalStageBar(
    segments: List<StageSegment>,
    progress: Float,
    modifier: Modifier = Modifier,
    barHeight: Int = 14,
    showLabels: Boolean = true
) {
    val total = segments.sumOf { it.weight.toDouble() }.toFloat()
    val density = LocalDensity.current
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((barHeight + 18).dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(barHeight.dp)
                    .align(Alignment.TopStart)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape((barHeight / 2).dp))
            ) {
                segments.forEach { seg ->
                    Box(
                        modifier = Modifier
                            .weight(seg.weight / total)
                            .fillMaxHeight()
                            .background(seg.color)
                    )
                }
            }
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((barHeight + 18).dp)
            ) {
                val x = size.width * progress.coerceIn(0f, 1f)
                val barH = with(density) { barHeight.dp.toPx() }
                val path = Path().apply {
                    moveTo(x, barH + 2f)
                    lineTo(x - 8f, barH + 14f)
                    lineTo(x + 8f, barH + 14f)
                    close()
                }
                drawPath(path, color = Color.Black.copy(alpha = 0.85f))
                drawCircle(
                    color = Color.White,
                    radius = barH / 2f - 2f,
                    center = androidx.compose.ui.geometry.Offset(x, barH / 2f),
                    style = Stroke(width = 2f)
                )
            }
        }
        if (showLabels) {
            Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                segments.forEach { seg ->
                    Text(
                        text = seg.label,
                        modifier = Modifier.weight(seg.weight / total),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}


// ==================== 折线图 ====================

data class LinePoint(val x: Float, val y: Float, val label: String = "")

@Composable
fun LineChart(
    points: List<LinePoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    fillGradient: Boolean = true,
    yMin: Float? = null,
    yMax: Float? = null,
    showDots: Boolean = true,
    height: Int = 160
) {
    if (points.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth().height(height.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("暂无数据", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
        return
    }

    val minY = yMin ?: (points.minOf { it.y } - 2f)
    val maxY = yMax ?: (points.maxOf { it.y } + 2f)
    val rangeY = (maxY - minY).coerceAtLeast(1f)

    val dotColor = lineColor
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val density = LocalDensity.current

    Canvas(modifier = modifier.fillMaxWidth().height(height.dp)) {
        val paddingLeftPx = with(density) { 28.dp.toPx() }
        val paddingRightPx = with(density) { 8.dp.toPx() }
        val paddingTopPx = with(density) { 12.dp.toPx() }
        val paddingBottomPx = with(density) { 24.dp.toPx() }
        val plotW = size.width - paddingLeftPx - paddingRightPx
        val plotH = size.height - paddingTopPx - paddingBottomPx

        // 网格 4 条横线
        for (i in 0..4) {
            val y = paddingTopPx + plotH * i / 4f
            drawLine(
                color = gridColor.copy(alpha = 0.5f),
                start = androidx.compose.ui.geometry.Offset(paddingLeftPx, y),
                end = androidx.compose.ui.geometry.Offset(size.width - paddingRightPx, y),
                strokeWidth = 1f
            )
        }

        // Y 轴标签
        val txtPaint = android.graphics.Paint().apply {
            color = labelColor.toArgb()
            textSize = with(density) { 10.sp.toPx() }
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.RIGHT
        }
        for (i in 0..4) {
            val y = paddingTopPx + plotH * i / 4f
            val v = maxY - rangeY * i / 4f
            drawContext.canvas.nativeCanvas.drawText(
                String.format("%.0f", v),
                paddingLeftPx - 4f, y + txtPaint.textSize / 3, txtPaint
            )
        }

        // 数据点
        val ptCount = points.size
        val pixelPoints = points.mapIndexed { idx, p ->
            val x = paddingLeftPx + (if (ptCount == 1) plotW / 2 else plotW * idx / (ptCount - 1))
            val y = paddingTopPx + plotH * (1 - (p.y - minY) / rangeY)
            androidx.compose.ui.geometry.Offset(x, y)
        }

        // 渐变填充
        if (fillGradient && pixelPoints.size >= 2) {
            val fillPath = Path().apply {
                moveTo(pixelPoints.first().x, paddingTopPx + plotH)
                pixelPoints.forEach { lineTo(it.x, it.y) }
                lineTo(pixelPoints.last().x, paddingTopPx + plotH)
                close()
            }
            drawPath(
                path = fillPath,
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(lineColor.copy(alpha = 0.25f), lineColor.copy(alpha = 0f))
                )
            )
        }

        // 折线
        val linePath = Path().apply {
            pixelPoints.forEachIndexed { i, off ->
                if (i == 0) moveTo(off.x, off.y) else lineTo(off.x, off.y)
            }
        }
        drawPath(linePath, color = lineColor, style = Stroke(width = 4f))

        // 圆点
        if (showDots) {
            pixelPoints.forEach { off ->
                drawCircle(color = Color.White, radius = 5f, center = off)
                drawCircle(color = dotColor, radius = 5f, center = off, style = Stroke(width = 2f))
            }
        }

        // X 轴 label：仅显示首尾 + 中间
        val xLabelPaint = android.graphics.Paint().apply {
            color = labelColor.toArgb()
            textSize = with(density) { 10.sp.toPx() }
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
        }
        if (points.isNotEmpty()) {
            val indices = if (points.size <= 4) points.indices.toList()
            else listOf(0, points.size / 3, 2 * points.size / 3, points.size - 1)
            indices.forEach { i ->
                val off = pixelPoints[i]
                drawContext.canvas.nativeCanvas.drawText(
                    points[i].label,
                    off.x, size.height - paddingBottomPx + 16f, xLabelPaint
                )
            }
        }
    }
}

private fun Color.toArgb(): Int = android.graphics.Color.argb(
    (alpha * 255).toInt(), (red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt()
)

// ==================== 心率 8 级切片圆环 ====================

/**
 * 心率结果 8 级分区圆环（NewResultSliceView 简化版）。
 * 9 个 type（0..8），活动指针指向当前 type。
 */
@Composable
fun HRSliceRing(
    activeIndex: Int,
    modifier: Modifier = Modifier,
    sizeDp: Int = 180,
    centerLabel: String = "",
    centerSubLabel: String = ""
) {
    val sliceColors = listOf(
        BPColors.LevelCrisis,
        BPColors.LevelStage2,
        BPColors.LevelStage1,
        BPColors.LevelElevated,
        BPColors.LevelNormal,
        BPColors.LevelElevated,
        BPColors.LevelStage1,
        BPColors.LevelStage2,
        BPColors.LevelCrisis
    )
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val density = LocalDensity.current

    Box(
        modifier = modifier.size(sizeDp.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(sizeDp.dp)) {
            val sweep = 240f / sliceColors.size
            val startAngle = 150f
            val strokeWidth = with(density) { 18.dp.toPx() }
            val padding = strokeWidth / 2 + 8f
            val arcSize = androidx.compose.ui.geometry.Size(
                size.width - padding * 2, size.height - padding * 2
            )
            val topLeft = androidx.compose.ui.geometry.Offset(padding, padding)
            sliceColors.forEachIndexed { idx, c ->
                val isActive = idx == activeIndex
                drawArc(
                    color = if (isActive) c else c.copy(alpha = 0.32f),
                    startAngle = startAngle + idx * sweep,
                    sweepAngle = sweep - 2f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(
                        width = if (isActive) strokeWidth * 1.4f else strokeWidth
                    )
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (centerLabel.isNotEmpty()) {
                Text(
                    text = centerLabel,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = onSurface
                )
            }
            if (centerSubLabel.isNotEmpty()) {
                Text(
                    text = centerSubLabel,
                    fontSize = 12.sp,
                    color = onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ColorDot(color: Color, sizeDp: Int = 10) {
    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .background(color)
    )
}

// ==================== 截图风格的柱状图（心率 / 血糖详情页） ====================

/** 单值柱：显示单一数值（如 BPM、血糖 mg/dL）。 */
data class BarPoint(val value: Float, val xLabel: String)

/**
 * 截图风格的柱状图：
 *  - 绿色长椭圆条 + 值显示在顶部
 *  - 左侧有 Y 轴刻度文字（虚线水平网格）
 *  - 顶部正中央显示年份
 *  - 底部仅显示首/末日期
 */
@Composable
fun ScreenshotBarChart(
    points: List<BarPoint>,
    modifier: Modifier = Modifier,
    yMin: Float,
    yMax: Float,
    yStep: Float,
    barColor: Color = BPColors.Accent,
    yearLabel: String,
    height: Int = 240,
) {
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val valueColor = MaterialTheme.colorScheme.onSurface
    val density = LocalDensity.current

    Canvas(modifier = modifier.fillMaxWidth().height(height.dp)) {
        val left = with(density) { 36.dp.toPx() }
        val right = with(density) { 12.dp.toPx() }
        val top = with(density) { 32.dp.toPx() }
        val bottom = with(density) { 28.dp.toPx() }
        val plotW = size.width - left - right
        val plotH = size.height - top - bottom
        val rangeY = (yMax - yMin).coerceAtLeast(1f)

        val ticks = ((rangeY / yStep).toInt() + 1).coerceAtLeast(2)

        val gridPaintArgb = gridColor.copy(alpha = 0.6f).toArgb()
        val labelPaint = android.graphics.Paint().apply {
            color = labelColor.toArgb()
            textSize = with(density) { 11.sp.toPx() }
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.RIGHT
        }
        val centerPaint = android.graphics.Paint().apply {
            color = labelColor.toArgb()
            textSize = with(density) { 12.sp.toPx() }
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
        }
        val valPaint = android.graphics.Paint().apply {
            color = valueColor.toArgb()
            textSize = with(density) { 14.sp.toPx() }
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
            isFakeBoldText = true
        }

        for (i in 0 until ticks) {
            val v = yMin + i * yStep
            val y = top + plotH * (1 - (v - yMin) / rangeY)
            val effect = android.graphics.DashPathEffect(floatArrayOf(8f, 8f), 0f)
            val gridPaint = android.graphics.Paint().apply {
                color = gridPaintArgb
                strokeWidth = 1.5f
                style = android.graphics.Paint.Style.STROKE
                pathEffect = effect
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawLine(left, y, size.width - right, y, gridPaint)
            drawContext.canvas.nativeCanvas.drawText(
                String.format(if (yStep < 1f) "%.1f" else "%.0f", v),
                left - 6f, y + labelPaint.textSize / 3, labelPaint
            )
        }

        drawContext.canvas.nativeCanvas.drawText(
            yearLabel,
            left + plotW / 2, top - 12f, centerPaint
        )

        val n = points.size
        if (n == 0) return@Canvas
        val barW = with(density) { 14.dp.toPx() }
        val gap = if (n == 1) 0f else (plotW - barW * n) / (n - 1).coerceAtLeast(1)
        val startX = if (n == 1) left + plotW / 2 - barW / 2 else left

        points.forEachIndexed { idx, p ->
            val x = startX + idx * (barW + gap)
            val yTop = top + plotH * (1 - (p.value - yMin) / rangeY)
            val yBottom = top + plotH
            drawRoundRect(
                color = barColor,
                topLeft = androidx.compose.ui.geometry.Offset(x, yTop),
                size = androidx.compose.ui.geometry.Size(barW, yBottom - yTop),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(barW / 2, barW / 2)
            )
            drawContext.canvas.nativeCanvas.drawText(
                if (yStep < 1f) String.format("%.1f", p.value) else p.value.toInt().toString(),
                x + barW / 2, yTop - 6f, valPaint
            )
        }
        listOf(0, n - 1).distinct().forEach { idx ->
            val x = startX + idx * (barW + gap) + barW / 2
            drawContext.canvas.nativeCanvas.drawText(
                points[idx].xLabel,
                x, size.height - 6f, centerPaint
            )
        }
    }
}

// ==================== 截图风格的"上下/最低-最高"柱图（血压详情页） ====================

data class RangeBarPoint(val low: Float, val high: Float, val xLabel: String)

/**
 * 血压详情页的"双值柱"：每天 1 根绿色长椭圆条，覆盖 [舒张, 收缩]。
 *  - 顶部数字 = high（收缩）
 *  - 底部数字 = low（舒张）
 */
@Composable
fun ScreenshotRangeBarChart(
    points: List<RangeBarPoint>,
    modifier: Modifier = Modifier,
    yMin: Float = 40f,
    yMax: Float = 160f,
    yStep: Float = 24f,
    barColor: Color = BPColors.Accent,
    yearLabel: String,
    height: Int = 280,
) {
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val valueColor = MaterialTheme.colorScheme.onSurface
    val density = LocalDensity.current

    Canvas(modifier = modifier.fillMaxWidth().height(height.dp)) {
        val left = with(density) { 36.dp.toPx() }
        val right = with(density) { 12.dp.toPx() }
        val top = with(density) { 36.dp.toPx() }
        val bottom = with(density) { 32.dp.toPx() }
        val plotW = size.width - left - right
        val plotH = size.height - top - bottom
        val rangeY = (yMax - yMin).coerceAtLeast(1f)

        val effect = android.graphics.DashPathEffect(floatArrayOf(8f, 8f), 0f)
        val gridPaint = android.graphics.Paint().apply {
            color = gridColor.copy(alpha = 0.6f).toArgb()
            strokeWidth = 1.5f
            style = android.graphics.Paint.Style.STROKE
            pathEffect = effect
            isAntiAlias = true
        }
        val labelPaint = android.graphics.Paint().apply {
            color = labelColor.toArgb()
            textSize = with(density) { 11.sp.toPx() }
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.RIGHT
        }
        val centerPaint = android.graphics.Paint().apply {
            color = labelColor.toArgb()
            textSize = with(density) { 12.sp.toPx() }
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
        }
        val valPaint = android.graphics.Paint().apply {
            color = valueColor.toArgb()
            textSize = with(density) { 14.sp.toPx() }
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
            isFakeBoldText = true
        }

        val ticks = ((rangeY / yStep).toInt() + 1).coerceAtLeast(2)
        for (i in 0 until ticks) {
            val v = yMin + i * yStep
            val y = top + plotH * (1 - (v - yMin) / rangeY)
            drawContext.canvas.nativeCanvas.drawLine(left, y, size.width - right, y, gridPaint)
            drawContext.canvas.nativeCanvas.drawText(
                String.format("%.0f", v),
                left - 6f, y + labelPaint.textSize / 3, labelPaint
            )
        }

        drawContext.canvas.nativeCanvas.drawText(
            yearLabel, left + plotW / 2, top - 12f, centerPaint
        )

        val n = points.size
        if (n == 0) return@Canvas
        val barW = with(density) { 14.dp.toPx() }
        val gap = if (n == 1) 0f else (plotW - barW * n) / (n - 1).coerceAtLeast(1)
        val startX = if (n == 1) left + plotW / 2 - barW / 2 else left

        points.forEachIndexed { idx, p ->
            val x = startX + idx * (barW + gap)
            val yTop = top + plotH * (1 - (p.high - yMin) / rangeY)
            val yBot = top + plotH * (1 - (p.low - yMin) / rangeY)
            drawRoundRect(
                color = barColor,
                topLeft = androidx.compose.ui.geometry.Offset(x, yTop),
                size = androidx.compose.ui.geometry.Size(barW, yBot - yTop),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(barW / 2, barW / 2)
            )
            drawContext.canvas.nativeCanvas.drawText(
                p.high.toInt().toString(),
                x + barW / 2, yTop - 6f, valPaint
            )
            drawContext.canvas.nativeCanvas.drawText(
                p.low.toInt().toString(),
                x + barW / 2, yBot + valPaint.textSize + 2f, valPaint
            )
        }

        listOf(0, n - 1).distinct().forEach { idx ->
            val x = startX + idx * (barW + gap) + barW / 2
            drawContext.canvas.nativeCanvas.drawText(
                points[idx].xLabel,
                x, size.height - 4f, centerPaint
            )
        }
    }
}
