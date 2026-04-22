package com.example.kotlin_app_study.bp

object Routes {
    const val Main = "main"

    // 心率
    const val HRDetail = "hr/detail"
    const val HRMeasure = "hr/measure"
    const val HRResult = "hr/result"

    // 血压
    const val BPDetail = "bp/detail"
    const val BPAdd = "bp/add"
    const val BPHistory = "bp/history"

    // 血糖
    const val BSDetail = "bs/detail"
    const val BSAdd = "bs/add"
    const val BSHistory = "bs/history"

    // 知识
    const val KnowledgeDetail = "knowledge/detail/{id}"
    fun knowledgeDetail(id: Long) = "knowledge/detail/$id"

    // AI
    const val AiChat = "ai/chat"
    const val AiHistory = "ai/history"

    // 提醒
    const val Reminder = "reminder"

    // 设置子页
    const val Profile = "settings/profile"
    const val Language = "settings/language"
    const val Feedback = "settings/feedback"
    const val Privacy = "settings/privacy"
    const val Terms = "settings/terms"
}
