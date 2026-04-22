package com.example.kotlin_app_study.bp.data

/**
 * 业务分级（参照 03 文档 + 截图）。纯函数。
 */
object Grading {

    // ==================== 血压：ACC/AHA 2017 ====================

    fun bpLevel(systolic: Int, diastolic: Int): BPLevel = when {
        systolic > 180 || diastolic > 120 -> BPLevel.CRISIS
        systolic >= 140 || diastolic >= 90 -> BPLevel.STAGE2
        systolic in 130..139 || diastolic in 80..89 -> BPLevel.STAGE1
        systolic in 120..129 && diastolic in 60..79 -> BPLevel.ELEVATED
        systolic < 90 || diastolic < 60 -> BPLevel.LOW
        else -> BPLevel.NORMAL
    }

    // ==================== 心率（成人静息 60~100） ====================

    fun hrLevel(bpm: Int): HRLevel = when {
        bpm < 50 -> HRLevel.LOW
        bpm <= 100 -> HRLevel.NORMAL
        bpm <= 120 -> HRLevel.HIGH
        else -> HRLevel.VERY_HIGH
    }

    // ==================== 血糖（mg/dL，截图所示） ====================
    // 截图：72~99 正常，<72 偏低，<126 偏高，≥126 糖尿病前期
    fun bsLevel(mgDl: Float): BSLevel = when {
        mgDl < 72f -> BSLevel.LOW
        mgDl <= 99f -> BSLevel.NORMAL
        mgDl < 126f -> BSLevel.PRE
        else -> BSLevel.HIGH
    }
}
