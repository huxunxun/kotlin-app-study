package com.example.kotlin_app_study.bp.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TimeFormat {

    private val fullFmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private val dateFmt = SimpleDateFormat("MM-dd", Locale.getDefault())
    private val dateLongFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())
    /** 截图样式："4月21日,15:52" */
    private val cnDateTimeFmt = SimpleDateFormat("M月d日,HH:mm", Locale.CHINA)

    fun formatFull(ts: Long): String = fullFmt.format(Date(ts))
    fun formatDate(ts: Long): String = dateFmt.format(Date(ts))
    fun formatLongDate(ts: Long): String = dateLongFmt.format(Date(ts))
    fun formatTime(ts: Long): String = timeFmt.format(Date(ts))
    fun formatCnDateTime(ts: Long): String = cnDateTimeFmt.format(Date(ts))

    fun friendly(ts: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - ts
        return when {
            diff < 60 * 1000 -> "刚刚"
            diff < 60 * 60 * 1000 -> "${diff / 60_000} 分钟前"
            diff < 24 * 60 * 60 * 1000 -> "${diff / 3_600_000} 小时前"
            diff < 7 * 24 * 60 * 60 * 1000 -> "${diff / 86_400_000} 天前"
            else -> formatDate(ts)
        }
    }

    fun toCalendar(ts: Long): Calendar = Calendar.getInstance().apply { timeInMillis = ts }

    fun calendarToTimestamp(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
        return Calendar.getInstance().apply {
            set(year, month - 1, day, hour, minute, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
