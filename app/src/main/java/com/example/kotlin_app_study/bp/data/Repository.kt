package com.example.kotlin_app_study.bp.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 内存版仓库。应用进程存活期间保持状态。
 * 启动时注入 Samples 数据，便于看到趋势图与历史。
 */
object BPRepository {

    private val now = System.currentTimeMillis()

    // ---------- UserProfile ----------
    private val _profile = MutableStateFlow(UserProfile(gender = Gender.MALE, age = 35))
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()
    fun updateProfile(transform: (UserProfile) -> UserProfile) {
        _profile.value = transform(_profile.value)
    }

    // ---------- AppPreferences ----------
    private val _preferences = MutableStateFlow(AppPreferences())
    val preferences: StateFlow<AppPreferences> = _preferences.asStateFlow()
    fun updatePreferences(transform: (AppPreferences) -> AppPreferences) {
        _preferences.value = transform(_preferences.value)
    }

    // ---------- BP ----------
    private val _bp = MutableStateFlow(Samples.seedBP(now))
    val bpRecords: StateFlow<List<BPRecord>> = _bp.asStateFlow()
    private var bpId = (_bp.value.maxOfOrNull { it.id } ?: 0L) + 1

    fun addBP(systolic: Int, diastolic: Int, pulse: Int, timestamp: Long, note: String): BPRecord {
        val record = BPRecord(bpId++, timestamp, systolic, diastolic, pulse, note)
        _bp.value = (listOf(record) + _bp.value).sortedByDescending { it.timestamp }
        return record
    }

    fun deleteBP(id: Long) { _bp.value = _bp.value.filterNot { it.id == id } }
    fun bpById(id: Long): BPRecord? = _bp.value.firstOrNull { it.id == id }

    // ---------- HR ----------
    private val _hr = MutableStateFlow(Samples.seedHR(now))
    val hrRecords: StateFlow<List<HRRecord>> = _hr.asStateFlow()
    private var hrId = (_hr.value.maxOfOrNull { it.id } ?: 0L) + 1

    fun addHR(
        bpm: Int,
        timestamp: Long = System.currentTimeMillis(),
        stress: Int? = null,
        chartData: List<Int> = emptyList(),
        note: String = ""
    ): HRRecord {
        val record = HRRecord(hrId++, timestamp, bpm, stress, chartData, note)
        _hr.value = (listOf(record) + _hr.value).sortedByDescending { it.timestamp }
        return record
    }

    fun deleteHR(id: Long) { _hr.value = _hr.value.filterNot { it.id == id } }
    fun hrById(id: Long): HRRecord? = _hr.value.firstOrNull { it.id == id }
    fun lastHr(): HRRecord? = _hr.value.firstOrNull()

    /**
     * 最近一次 HR 测量的完整统计（含 HRV / 压力 / 整段曲线），供测量结果页展示。
     * 用 var + 普通可空引用，避免引入 PpgProcessor 的具体类型到 data 层。
     */
    private val _lastMeasurement = MutableStateFlow<LastMeasurement?>(null)
    val lastMeasurement: StateFlow<LastMeasurement?> = _lastMeasurement.asStateFlow()
    fun setLastMeasurement(m: LastMeasurement?) { _lastMeasurement.value = m }

    // ---------- BS ----------
    private val _bs = MutableStateFlow(Samples.seedBS(now))
    val bsRecords: StateFlow<List<BSRecord>> = _bs.asStateFlow()
    private var bsId = (_bs.value.maxOfOrNull { it.id } ?: 0L) + 1

    fun addBS(mgDl: Float, period: BSPeriod, timestamp: Long, note: String = ""): BSRecord {
        val record = BSRecord(bsId++, timestamp, mgDl, period, note)
        _bs.value = (listOf(record) + _bs.value).sortedByDescending { it.timestamp }
        return record
    }
    fun deleteBS(id: Long) { _bs.value = _bs.value.filterNot { it.id == id } }
    fun bsById(id: Long): BSRecord? = _bs.value.firstOrNull { it.id == id }

    // ---------- Reminder ----------
    private val _reminders = MutableStateFlow(Samples.seedReminders())
    val reminders: StateFlow<List<ReminderItem>> = _reminders.asStateFlow()
    private var reminderId = (_reminders.value.maxOfOrNull { it.id } ?: 0L) + 1

    fun addReminder(item: ReminderItem) {
        _reminders.value = _reminders.value + item.copy(id = reminderId++)
    }
    fun updateReminder(item: ReminderItem) {
        _reminders.value = _reminders.value.map { if (it.id == item.id) item else it }
    }
    fun toggleReminder(id: Long, enabled: Boolean) {
        _reminders.value = _reminders.value.map {
            if (it.id == id) it.copy(enabled = enabled) else it
        }
    }
    fun deleteReminder(id: Long) { _reminders.value = _reminders.value.filterNot { it.id == id } }
    fun reminderById(id: Long): ReminderItem? = _reminders.value.firstOrNull { it.id == id }

    // ---------- AI Chat ----------
    private val _chatSessions = MutableStateFlow<List<AiChatSession>>(emptyList())
    val chatSessions: StateFlow<List<AiChatSession>> = _chatSessions.asStateFlow()
    private var chatId = 1L
    private var msgId = 1L
    fun newMessageId(): Long = msgId++
    fun newSessionId(): Long = chatId++
    fun saveOrUpdateSession(session: AiChatSession) {
        val all = _chatSessions.value
        _chatSessions.value = if (all.any { it.id == session.id }) {
            all.map { if (it.id == session.id) session else it }
        } else all + session
        _chatSessions.value = _chatSessions.value.sortedByDescending { it.lastMessageTime }
    }
    fun deleteSession(id: Long) { _chatSessions.value = _chatSessions.value.filterNot { it.id == id } }
    fun sessionById(id: Long): AiChatSession? = _chatSessions.value.firstOrNull { it.id == id }

    // ---------- Knowledge ----------
    val knowledgeArticles: List<KnowledgeArticle> get() = Samples.knowledgeArticles
    fun knowledgeById(id: Long) = Samples.knowledgeArticles.firstOrNull { it.id == id }

    // ---------- Health Tests ----------
    val healthTests: List<HealthTest> get() = Samples.healthTests
    fun healthTestById(id: String) = Samples.healthTests.firstOrNull { it.testId == id }

    // ---------- FAQ ----------
    val faqList: List<FaqItem> get() = Samples.faqList
}
