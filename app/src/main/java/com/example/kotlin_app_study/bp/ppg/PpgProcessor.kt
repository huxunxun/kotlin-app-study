package com.example.kotlin_app_study.bp.ppg

import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * PPG 心率处理器（按 05 文档 §3 端上算法实现）：
 *
 *  原始帧均值 R̄
 *      │
 *      ▼ 1 阶 IIR 低通 → 跟踪 DC
 *      │
 *      ▼ 高通：原值 - DC
 *      │
 *      ▼ 1 阶 IIR 低通 → 抑制高频噪声
 *      │
 *      ▼ 二阶差分带通（最近 5 个低通输出做核 [-2,-1,0,1,2]）
 *      │
 *      ▼ 零穿过检测 → 心搏时刻
 *      │
 *      ▼ 60000 / RR → 瞬时 BPM
 *      │
 *      ▼ 滑动 5 个 BPM 中值 + EMA(α=0.4) 平滑
 *      │
 *      ▼ onBpm()  （前 3 个有效心搏不上屏）
 *
 * 同时收集所有心搏时间戳供 HRV / stress 评分使用。
 */
class PpgProcessor(
    /** 假设的采样率，CameraX ImageAnalysis ~30 FPS。可在 onFrame 中实时用 1/dt 修正。 */
    private var fps: Float = 30f,
    /** BPM 回调（已平滑） */
    private val onBpm: (Int) -> Unit = {},
    /** 是否检测到手指（HSV 红色调） */
    private val onFingerChanged: (Boolean) -> Unit = {},
    /** 测量进度 0..1，按收到的有效采样数估算 */
    private val onProgress: (Float) -> Unit = {},
    /** 检测到 1 次有效心搏（可用于震动） */
    private val onBeat: () -> Unit = {},
    /** 每一帧带通输出（已归一化到约 [-1, 1]），可用于绘制实时波形 */
    private val onSample: (Float) -> Unit = {},
) {
    // ----------- 滤波状态 -----------
    private var dc: Float = 0f                // IIR 低通跟踪 DC
    private var lp: Float = 0f                // IIR 低通输出
    private var lpInited: Boolean = false
    private val recentLp = ArrayDeque<Float>(8) // 最近 5 个低通输出，做二阶差分

    // ----------- 节拍检测 -----------
    private var lastSign: Int = 0
    private var lastBeatTimeMs: Long = 0L

    // ----------- BPM 平滑 -----------
    private val bpmWindow = ArrayDeque<Int>(8)
    private var smoothBpm: Float = 0f
    private var validCount: Int = 0
    private var fingerOn: Boolean = false

    /** 完整心搏时间戳序列（提供给 HRV / stress 用） */
    val beatTimestamps = mutableListOf<Long>()

    /** 实时报上来的平滑 BPM 序列（提供给详情页柱图） */
    val bpmHistory = mutableListOf<Int>()

    /** 自适应归一化所用的滑动幅度（最近一段时间内 |bp| 的最大值） */
    private var adaptiveAmp: Float = 0.5f

    fun reset() {
        dc = 0f; lp = 0f; lpInited = false
        recentLp.clear()
        lastSign = 0; lastBeatTimeMs = 0L
        bpmWindow.clear(); smoothBpm = 0f; validCount = 0
        beatTimestamps.clear(); bpmHistory.clear()
        fingerOn = false
        adaptiveAmp = 0.5f
    }

    /**
     * 喂一帧统计数据：整帧均值 R̄, Ḡ, B̄ + 此帧的时间戳。
     * 返回当前已平滑的 BPM（0 表示尚未稳定）。
     */
    fun onFrame(rMean: Float, gMean: Float, bMean: Float, tNowMs: Long): Int {
        // 1) 简化的"手指检测"：转 HSV 看是否在红色调，且 R 通道明显大于 G/B
        val isFinger = isFingerByHsv(rMean, gMean, bMean)
        if (isFinger != fingerOn) {
            fingerOn = isFinger
            onFingerChanged(isFinger)
            if (!isFinger) {
                // 手指离开：清空状态，避免误算
                dc = 0f; lp = 0f; lpInited = false
                recentLp.clear(); bpmWindow.clear()
                lastSign = 0; lastBeatTimeMs = 0L
                smoothBpm = 0f; validCount = 0
            }
        }
        if (!isFinger) return 0

        val x = rMean

        // 2) 高通：用 1 阶 IIR 跟踪 DC，再用原值减 DC
        // α_dc = 1 - exp(-1 / (fps * τ_dc))，05 文档：跟踪窗口 ~1.5 秒
        val tauDc = 1.5f
        val alphaDc = (1f - exp(-1f / (fps * tauDc))).coerceIn(0.001f, 0.5f)
        dc = if (dc == 0f) x else dc + alphaDc * (x - dc)
        val hp = x - dc

        // 3) 低通：截止 ~2.8 Hz，05 文档使用 exp(-2π·fc/fps)
        val fc = 2.8f
        val alphaLp = (1f - exp(-2f * Math.PI.toFloat() * fc / fps)).coerceIn(0.05f, 0.9f)
        lp = if (!lpInited) hp.also { lpInited = true } else lp + alphaLp * (hp - lp)

        // 4) 二阶差分带通：取最近 5 个低通输出 [y0..y4]，bp = y[1] + 2*y[0] - y[3] - 2*y[4]
        recentLp.addFirst(lp)
        while (recentLp.size > 5) recentLp.removeLast()
        if (recentLp.size < 5) {
            advanceProgress()
            return smoothBpm.toInt()
        }
        val y = recentLp.toList()
        val bp = y[1] + 2f * y[0] - y[3] - 2f * y[4]

        // 自适应归一化：用最近一段时间的最大幅值做参考，把波形压到 [-1, 1] 上
        val absBp = if (bp < 0f) -bp else bp
        if (absBp > adaptiveAmp) adaptiveAmp = absBp
        else adaptiveAmp = 0.97f * adaptiveAmp + 0.03f * absBp
        val normalized = (bp / max(adaptiveAmp, 0.001f)).coerceIn(-1f, 1f)
        onSample(normalized)

        // 5) 峰值检测：状态机式零穿过（由负→正才报心搏）
        val sign = when {
            bp >= 0 -> 1
            bp < 0 -> -1
            else -> 0
        }
        if (lastSign < 0 && sign > 0) {
            // 上升过零 = 一次心搏顶点
            if (lastBeatTimeMs > 0) {
                val rr = tNowMs - lastBeatTimeMs
                if (rr in 270..1500) {
                    val instant = (60000L / rr).toInt()
                    if (instant in 40..220) {
                        validCount++
                        beatTimestamps.add(tNowMs)
                        // 报告"有效心搏"事件，供震动 / 视觉脉冲使用
                        onBeat()
                        bpmWindow.addLast(instant)
                        while (bpmWindow.size > 5) bpmWindow.removeFirst()
                        // 中值
                        val sorted = bpmWindow.sorted()
                        val median = sorted[sorted.size / 2].toFloat()
                        if (validCount < 3) {
                            // 前 3 个不上屏
                        } else if (validCount == 3) {
                            smoothBpm = median
                            onBpm(smoothBpm.toInt())
                            bpmHistory.add(smoothBpm.toInt())
                        } else {
                            // EMA(α=0.4)
                            smoothBpm = 0.4f * median + 0.6f * smoothBpm
                            onBpm(smoothBpm.toInt())
                            bpmHistory.add(smoothBpm.toInt())
                        }
                    }
                }
            }
            lastBeatTimeMs = tNowMs
        }
        lastSign = sign

        advanceProgress()
        return smoothBpm.toInt()
    }

    private fun advanceProgress() {
        // 总目标 15 秒（30 fps × 15）≈ 450 帧
        val total = (fps * 15f).toInt().coerceAtLeast(60)
        val cur = (recentLp.size + validCount * 30).coerceAtMost(total)
        onProgress(cur.toFloat() / total)
    }

    /** 计算最终的指标（在测量结束时调一次） */
    fun computeStats(profileAge: Int = 35, isMale: Boolean = true): MeasurementStats {
        val bpm = if (smoothBpm > 0) smoothBpm.toInt() else (bpmWindow.lastOrNull() ?: 0)
        if (beatTimestamps.size < 2) {
            return MeasurementStats(bpm = bpm, stress = 50, hrv = null, chartData = bpmHistory.toList())
        }
        val rrMs = beatTimestamps.zipWithNext { a, b -> (b - a).toInt() }
            .filter { it in 300..2000 }
        if (rrMs.isEmpty()) {
            return MeasurementStats(bpm = bpm, stress = 50, hrv = null, chartData = bpmHistory.toList())
        }
        val meanRr = rrMs.average()
        val sdnn = sqrt(rrMs.map { (it - meanRr) * (it - meanRr) }.average())
        val rmssd = if (rrMs.size > 1) {
            val diffs = rrMs.zipWithNext { a, b -> (b - a).toDouble() }
            sqrt(diffs.map { it * it }.average())
        } else 0.0

        // stress 评分（按 05 文档 §4.3 公式简化）
        val hrMax = if (isMale) (220 - profileAge).toDouble() else (206 - 0.88 * profileAge)
        val base = bpm * 100.0 / hrMax
        // 用 sdnn 当做副交感占比的简单代理：sdnn 大→交感低→减压
        val pen = (50 - min(sdnn, 100.0)) * 0.4
        val stress = (base + pen).coerceIn(0.0, 100.0).toInt()

        return MeasurementStats(
            bpm = bpm,
            stress = stress,
            hrv = HrvStats(
                meanRrMs = meanRr,
                sdnn = sdnn,
                rmssd = rmssd
            ),
            chartData = bpmHistory.toList()
        )
    }

    /**
     * 简化版手指检测：
     *  - R̄ 明显高于 Ḡ 和 B̄
     *  - R̄ 不能太黑（> 30）也不能太白（< 245，避免曝光过度判定）
     *  - 红/绿比 R / max(G, 1) > 1.4
     */
    private fun isFingerByHsv(r: Float, g: Float, b: Float): Boolean {
        if (r < 30f || r > 245f) return false
        if (r <= g + 10f || r <= b + 10f) return false
        val rgRatio = r / max(g, 1f)
        return rgRatio >= 1.4f
    }

    fun setFps(newFps: Float) {
        if (newFps in 5f..120f) fps = newFps
    }
}

data class HrvStats(val meanRrMs: Double, val sdnn: Double, val rmssd: Double)

data class MeasurementStats(
    val bpm: Int,
    /** 0..100 */
    val stress: Int,
    val hrv: HrvStats?,
    val chartData: List<Int>
)
