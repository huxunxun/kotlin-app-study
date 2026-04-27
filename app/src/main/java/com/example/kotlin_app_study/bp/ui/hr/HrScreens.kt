package com.example.kotlin_app_study.bp.ui.hr

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kotlin_app_study.bp.ads.AppOpenAdManager
import com.example.kotlin_app_study.bp.ads.rememberActivity
import com.example.kotlin_app_study.bp.ads.showInterstitial
import com.example.kotlin_app_study.bp.data.BPRepository
import com.example.kotlin_app_study.bp.data.Grading
import com.example.kotlin_app_study.bp.data.HRLevel
import com.example.kotlin_app_study.bp.data.LastMeasurement
import com.example.kotlin_app_study.bp.ppg.PpgFrameAnalyzer
import com.example.kotlin_app_study.bp.ppg.PpgProcessor
import com.example.kotlin_app_study.bp.theme.BPColors
import com.example.kotlin_app_study.bp.ui.components.BPCard
import com.example.kotlin_app_study.bp.ui.components.BPTopBar
import com.example.kotlin_app_study.bp.ui.components.BarPoint
import com.example.kotlin_app_study.bp.ui.components.ScreenshotBarChart
import com.example.kotlin_app_study.bp.util.TimeFormat
import java.util.Calendar
import java.util.concurrent.Executors

/** 心率详情页（截图 04_hr 主图）：3 统计 + 柱图 + 列表 + 底部"测量"按钮。 */
@Composable
fun HRDetailScreen(onBack: () -> Unit, onMeasure: () -> Unit) {
    val list by BPRepository.hrRecords.collectAsStateWithLifecycle()
    val avg = if (list.isNotEmpty()) list.map { it.bpm }.average().toInt() else 0
    val max = list.maxOfOrNull { it.bpm } ?: 0
    val min = list.minOfOrNull { it.bpm } ?: 0
    val year = Calendar.getInstance().apply { timeInMillis = System.currentTimeMillis() }.get(Calendar.YEAR).toString()

    val barPoints = list.take(14).reversed().map { r ->
        val cal = TimeFormat.toCalendar(r.timestamp)
        BarPoint(r.bpm.toFloat(), "${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}")
    }

    // 根据实际数据动态计算 Y 轴范围，避免柱条 / 数字超出图表区域。
    // 上下各留 10 BPM 余量，并按 10 取整；最小跨度 40 BPM，保证刻度好看。
    val (yMinHr, yMaxHr, yStepHr) = run {
        if (barPoints.isEmpty()) {
            Triple(40f, 120f, 20f)
        } else {
            val dataMin = barPoints.minOf { it.value }
            val dataMax = barPoints.maxOf { it.value }
            val minR = (kotlin.math.floor((dataMin - 10f) / 10f) * 10f).coerceAtLeast(30f)
            val maxR = (kotlin.math.ceil((dataMax + 10f) / 10f) * 10f).coerceAtLeast(minR + 40f)
            val span = maxR - minR
            val step = when {
                span <= 40f -> 10f
                span <= 80f -> 20f
                span <= 120f -> 30f
                span <= 160f -> 40f
                else -> (kotlin.math.ceil(span / 4f / 10f) * 10f)
            }
            Triple(minR, maxR, step)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BPTopBar(title = "心率", onBack = onBack, actions = {
            Text(
                "历史",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clickable {}
            )
        })
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { ThreeStatRow("$avg", "$max", "$min", listOf("平均", "最大值", "最小值"), unit = "BPM") }
            item {
                if (barPoints.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("暂无心率数据", color = BPColors.OnSurfaceVariant)
                    }
                } else {
                    ScreenshotBarChart(
                        points = barPoints,
                        yMin = yMinHr, yMax = yMaxHr, yStep = yStepHr,
                        yearLabel = year,
                    )
                }
            }
            items(list, key = { it.id }) { r ->
                HRRecordCard(
                    timeStr = TimeFormat.formatCnDateTime(r.timestamp),
                    bpm = r.bpm,
                    level = Grading.hrLevel(r.bpm),
                    stress = r.stress
                )
            }
            item { Spacer(Modifier.height(72.dp)) }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(BPColors.Primary)
                .clickable { onMeasure() },
            contentAlignment = Alignment.Center
        ) {
            Text("测量", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ThreeStatRow(v1: String, v2: String, v3: String, labels: List<String>, unit: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        listOf(v1, v2, v3).forEachIndexed { idx, v ->
            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(v, color = MaterialTheme.colorScheme.onBackground, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(4.dp))
                    Text(unit, color = BPColors.OnSurfaceVariant, fontSize = 13.sp, modifier = Modifier.padding(bottom = 6.dp))
                }
                Text(labels[idx], color = BPColors.OnSurfaceVariant, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun HRRecordCard(timeStr: String, bpm: Int, level: HRLevel, stress: Int?) {
    BPCard {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(timeStr, color = BPColors.OnSurfaceVariant, fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("$bpm", color = MaterialTheme.colorScheme.onBackground, fontSize = 38.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(2.dp))
                Column {
                    Spacer(Modifier.height(8.dp))
                    Text("BPM", color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Rounded.Favorite, contentDescription = null, tint = Color(0xFFE34A4A), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(20.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(level.color)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(level.zh, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                    if (stress != null) {
                        Spacer(Modifier.height(2.dp))
                        Text("压力：${stress}%", color = BPColors.OnSurfaceVariant, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// ==================== HR Measure (Real PPG via CameraX) ====================

/** 实时波形缓存大小（≈ 6.6 秒 @ 30fps） */
private const val WAVE_BUFFER_SIZE = 200

@Composable
fun HRMeasureScreen(onBack: () -> Unit, onFinished: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = rememberActivity()

    DisposableEffect(Unit) {
        AppOpenAdManager.setEnabled(false)
        onDispose { AppOpenAdManager.setEnabled(true) }
    }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasPermission) launcher.launch(Manifest.permission.CAMERA)
    }

    // ===== 测量状态 =====
    var bpm by remember { mutableIntStateOf(0) }
    var progress by remember { mutableFloatStateOf(0f) }
    var fingerOn by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }

    // ===== 实时波形缓存：固定长度 FloatArray，用整数版本号触发重组 =====
    val waveBuffer = remember { FloatArray(WAVE_BUFFER_SIZE) }
    var waveVersion by remember { mutableIntStateOf(0) }

    // ===== 心跳脉冲（每次有效心搏触发） =====
    val heartScale = remember { Animatable(1f) }
    var beatTick by remember { mutableLongStateOf(0L) }

    // ===== 震动器（API 31+ 用 VibratorManager） =====
    val vibrator = remember(context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    val processor = remember {
        PpgProcessor(
            onBpm = { bpm = it },
            onFingerChanged = { fingerOn = it },
            onProgress = { progress = it },
            onSample = { v ->
                // 滚动写入：左移一位，最新值写到末尾
                System.arraycopy(waveBuffer, 1, waveBuffer, 0, waveBuffer.size - 1)
                waveBuffer[waveBuffer.size - 1] = v
                waveVersion++
            },
            onBeat = {
                beatTick = System.currentTimeMillis()
                // 短促 40ms 震动，模拟心跳
                runCatching {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator?.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator?.vibrate(40)
                    }
                }
            }
        )
    }
    val analyzerExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose { analyzerExecutor.shutdown() }
    }

    // 心跳触发时给心形图标做一次脉冲缩放动画
    LaunchedEffect(beatTick) {
        if (beatTick > 0L) {
            heartScale.snapTo(1.25f)
            heartScale.animateTo(
                targetValue = 1f,
                animationSpec = androidx.compose.animation.core.tween(durationMillis = 220)
            )
        }
    }

    LaunchedEffect(progress) {
        if (progress >= 1f && !finished) {
            finished = true
            val stats = processor.computeStats()
            if (stats.bpm in 40..220) {
                BPRepository.addHR(
                    bpm = stats.bpm,
                    stress = stats.stress,
                    chartData = stats.chartData
                )
                BPRepository.setLastMeasurement(
                    LastMeasurement(
                        bpm = stats.bpm,
                        stress = stats.stress,
                        sdnn = stats.hrv?.sdnn,
                        rmssd = stats.hrv?.rmssd,
                        chartData = stats.chartData
                    )
                )
            }
            showInterstitial(activity) { onFinished() }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BPTopBar(
            title = "心率测量",
            onBack = null,
            actions = {
                Text(
                    "取消",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(end = 16.dp).clickable { onBack() }
                )
            }
        )

        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ===== 心形相机预览 + 跳动脉冲 =====
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .clip(CircleShape)
                        .background(BPColors.Surface.copy(alpha = 0.6f))
                )
                HeartCameraPreview(
                    enabled = hasPermission,
                    lifecycleOwner = lifecycleOwner,
                    onAnalyzer = { ImageAnalysis.Builder()
                        .setTargetResolution(Size(640, 480))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build().apply { setAnalyzer(analyzerExecutor, PpgFrameAnalyzer(processor)) }
                    },
                    modifier = Modifier
                        .size(140.dp)
                        .scale(heartScale.value)
                )
            }

            Spacer(Modifier.height(8.dp))

            // ===== 实时 PPG 波形 =====
            BPCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.Favorite,
                            contentDescription = null,
                            tint = Color(0xFFE34A4A),
                            modifier = Modifier.size(18.dp).scale(heartScale.value)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "实时心电波形",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.weight(1f))
                        if (bpm > 0) {
                            Text(
                                "$bpm",
                                color = Color(0xFFE34A4A),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(" BPM", color = BPColors.OnSurfaceVariant, fontSize = 12.sp)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    PpgWaveformCanvas(
                        buffer = waveBuffer,
                        version = waveVersion,
                        active = fingerOn,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // ===== 进度 / 提示 =====
            BPCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "测量中(${(progress * 100).toInt()}%)",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        if (fingerOn) "保持手指不动..." else "将手指放在摄像头上",
                        color = BPColors.OnSurfaceVariant, fontSize = 14.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Favorite, contentDescription = null, tint = Color(0xFFE34A4A))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "当取景器变成红色时，说明操作正确",
                            color = BPColors.OnSurfaceVariant,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

/**
 * 实时 PPG 波形 Canvas。
 *  - 数据源：长度固定的 FloatArray，每一帧由 PpgProcessor 写入末尾；
 *  - 用 `version` 作为重组 key，避免 Compose 误判数组未变化；
 *  - `active` 为 false 时画一条平直的"心电休止"虚线。
 */
@Composable
private fun PpgWaveformCanvas(
    buffer: FloatArray,
    version: Int,
    active: Boolean,
    modifier: Modifier = Modifier
) {
    val lineColor = Color(0xFFE34A4A)
    val gridColor = BPColors.OnSurfaceVariant.copy(alpha = 0.20f)
    Canvas(modifier = modifier) {
        @Suppress("UNUSED_EXPRESSION") version
        val w = size.width
        val h = size.height
        val midY = h / 2f
        // 中线（基线）
        drawLine(
            color = gridColor,
            start = Offset(0f, midY),
            end = Offset(w, midY),
            strokeWidth = 1f
        )

        if (!active) {
            // 没有手指时只画虚线
            return@Canvas
        }

        if (buffer.size < 2) return@Canvas
        val stepX = w / (buffer.size - 1).toFloat()
        val amp = h * 0.45f
        val path = Path()
        for (i in buffer.indices) {
            val x = i * stepX
            val y = midY - buffer[i] * amp
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path = path, color = lineColor, style = Stroke(width = 3f))
    }
}

@Composable
private fun HeartCameraPreview(
    enabled: Boolean,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    onAnalyzer: () -> ImageAnalysis,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .clip(HeartShape)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        if (enabled) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    androidx.camera.view.PreviewView(ctx).apply {
                        scaleType = androidx.camera.view.PreviewView.ScaleType.FILL_CENTER
                    }
                },
                update = { previewView ->
                    val providerFuture = ProcessCameraProvider.getInstance(context)
                    providerFuture.addListener({
                        val cameraProvider = providerFuture.get()
                        val preview = androidx.camera.core.Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val analysis = onAnalyzer()
                        try {
                            cameraProvider.unbindAll()
                            val cam = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                analysis
                            )
                            cam.cameraControl.enableTorch(true)
                        } catch (_: Exception) {}
                    }, ContextCompat.getMainExecutor(context))
                }
            )
        } else {
            Text("需要相机权限", color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp)
        }
    }
}

// 简化心形 Shape：用一段 Path 画出来
private val HeartShape = androidx.compose.foundation.shape.GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    moveTo(w / 2f, h * 0.85f)
    cubicTo(w * 0.0f, h * 0.55f, w * 0.05f, h * 0.05f, w / 2f, h * 0.30f)
    cubicTo(w * 0.95f, h * 0.05f, w * 1.0f, h * 0.55f, w / 2f, h * 0.85f)
    close()
}
