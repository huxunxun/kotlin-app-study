package com.example.kotlin_app_study.bp.data

import androidx.compose.ui.graphics.Color
import com.example.kotlin_app_study.bp.theme.BPColors

// ==================== 通用枚举 ====================

enum class Gender(val zh: String) {
    MALE("男性"),
    FEMALE("女性"),
    NON_BINARY("非二元性别"),
    OTHER("其他");
}

// ==================== 用户画像（极简：只有性别 + 年龄，截图所示） ====================

data class UserProfile(
    val gender: Gender = Gender.MALE,
    val age: Int = 35,
    val language: String = "zh_CN"
)

// ==================== 血压 BP ====================

data class BPRecord(
    val id: Long,
    val timestamp: Long,
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int,
    val note: String = ""
)

enum class BPLevel(val zh: String, val color: Color, val advice: String) {
    LOW("低血压", BPColors.LevelLow, "收缩压<90 或舒张压<60"),
    NORMAL("正常", BPColors.LevelNormal, "收缩压90-119，舒张压60-79"),
    ELEVATED("偏高", BPColors.LevelElevated, "收缩压120-129，舒张压60-79"),
    STAGE1("高血压·一级", BPColors.LevelStage1, "收缩压130-139或舒张压80-89"),
    STAGE2("高血压·二级", BPColors.LevelStage2, "收缩压140-180或舒张压90-120"),
    CRISIS("重度高血压", BPColors.LevelCrisis, "收缩压>180或舒张压>120");
}

// ==================== 心率 HR ====================

/** PPG 测量结束时的完整结果（供测量结果页展示）。 */
data class LastMeasurement(
    val bpm: Int,
    val stress: Int,
    val sdnn: Double?,
    val rmssd: Double?,
    val chartData: List<Int>,
    val timestamp: Long = System.currentTimeMillis()
)

data class HRRecord(
    val id: Long,
    val timestamp: Long,
    val bpm: Int,
    /** 0..100，PPG 测出的压力评分；手动录入为 null */
    val stress: Int? = null,
    /** PPG 实时采集的瞬时 BPM 序列，用作详情页折线图 */
    val chartData: List<Int> = emptyList(),
    val note: String = ""
)

enum class HRLevel(val zh: String, val color: Color) {
    LOW("过低", BPColors.LevelStage2),
    NORMAL("正常", BPColors.LevelNormal),
    HIGH("偏高", BPColors.LevelStage1),
    VERY_HIGH("过高", BPColors.LevelCrisis);
}

// ==================== 血糖 BS ====================

data class BSRecord(
    val id: Long,
    val timestamp: Long,
    /** 单位 mg/dL，截图所示 */
    val mgDl: Float,
    val period: BSPeriod,
    val note: String = ""
)

enum class BSPeriod(val zh: String) {
    DEFAULT("默认"),
    FASTING("空腹"),
    BEFORE_MEAL("餐前"),
    AFTER_MEAL("餐后"),
    BEFORE_SLEEP("睡前");
}

enum class BSLevel(val zh: String, val color: Color, val range: String) {
    LOW("偏低", BPColors.LevelLow, "<72"),
    NORMAL("正常", BPColors.LevelNormal, "72~99"),
    PRE("偏高", BPColors.LevelElevated, "100~125"),
    HIGH("偏高·糖尿病前期", BPColors.LevelStage1, "≥126");
}

// ==================== AI 医生 ====================

data class AiChatSession(
    val id: Long,
    val title: String,
    val createdAt: Long,
    val lastMessageTime: Long,
    val messages: List<AiMessage>
)

sealed class AiMessage {
    abstract val id: Long
    abstract val timestamp: Long

    data class UserText(override val id: Long, override val timestamp: Long, val text: String) : AiMessage()
    data class BotText(override val id: Long, override val timestamp: Long, val text: String) : AiMessage()
    data class BotPlaceholder(override val id: Long, override val timestamp: Long) : AiMessage()
    data class BotHealthTestCard(
        override val id: Long,
        override val timestamp: Long,
        val test: HealthTest
    ) : AiMessage()
    data class BotHealthTestRunner(
        override val id: Long,
        override val timestamp: Long,
        val test: HealthTest
    ) : AiMessage()
    data class BotHealthTestResult(
        override val id: Long,
        override val timestamp: Long,
        val test: HealthTest,
        val score: Int,
        val risk: HealthRisk,
        val advice: String
    ) : AiMessage()
}

data class FaqItem(val question: String, val answer: String)

data class HealthTest(
    val testId: String,
    val title: String,
    val subtitle: String,
    val duration: String, // "1分钟"
    val questions: List<TestQuestion>,
    val lowMax: Int,    // ≤ lowMax => LOW
    val midMax: Int     // (lowMax, midMax] => MID, > midMax => HIGH
)

data class TestQuestion(
    val text: String,
    val options: List<TestOption>
)

data class TestOption(val text: String, val score: Int)

enum class HealthRisk(val zh: String, val color: Color) {
    LOW("低风险", BPColors.LevelNormal),
    MID("中等风险", BPColors.LevelStage1),
    HIGH("高风险", BPColors.LevelCrisis);
}

// ==================== 知识库（彩色横幅卡） ====================

data class KnowledgeArticle(
    val id: Long,
    val title: String,
    val bannerColor: Color,
    val emoji: String,
    val intro: String,
    val sections: List<KnowledgeSection>
)

data class KnowledgeSection(val heading: String, val body: String)

// ==================== 提醒 ====================

data class ReminderItem(
    val id: Long,
    val type: ReminderType,
    val hour: Int,
    val minute: Int,
    /** 1..7 = 周一..周日；空集合 = 不重复 */
    val weekDays: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7),
    val enabled: Boolean = true
)

enum class ReminderType(val zh: String) {
    HR("心率"),
    BP("血压"),
    BS("血糖");
}

// ==================== 偏好设置 ====================

data class AppPreferences(
    val notificationEnabled: Boolean = true,
)
