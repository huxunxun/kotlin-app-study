package com.example.kotlin_app_study.bp

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kotlin_app_study.bp.ui.ai.AiChatScreen
import com.example.kotlin_app_study.bp.ui.ai.AiHistoryScreen
import com.example.kotlin_app_study.bp.ui.ai.HealthTestScreen
import com.example.kotlin_app_study.bp.ui.bp.BPAddScreen
import com.example.kotlin_app_study.bp.ui.bp.BPDetailScreen
import com.example.kotlin_app_study.bp.ui.bp.BPHistoryScreen
import com.example.kotlin_app_study.bp.ui.bs.BSAddScreen
import com.example.kotlin_app_study.bp.ui.bs.BSDetailScreen
import com.example.kotlin_app_study.bp.ui.bs.BSHistoryScreen
import com.example.kotlin_app_study.bp.ui.hr.HRDetailScreen
import com.example.kotlin_app_study.bp.ui.hr.HRMeasureScreen
import com.example.kotlin_app_study.bp.ui.hr.HRResultScreen
import com.example.kotlin_app_study.bp.ui.knowledge.KnowledgeDetailScreen
import com.example.kotlin_app_study.bp.ui.main.MainScreen
import com.example.kotlin_app_study.bp.ui.reminder.ReminderListScreen
import com.example.kotlin_app_study.bp.ui.settings.FeedbackScreen
import com.example.kotlin_app_study.bp.ui.settings.LanguageSetScreen
import com.example.kotlin_app_study.bp.ui.settings.PrivacyScreen
import com.example.kotlin_app_study.bp.ui.settings.ProfileEditScreen
import com.example.kotlin_app_study.bp.ui.settings.TermsScreen

@Composable
fun BPNavHost() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.Main) {

        composable(Routes.Main) { MainScreen(onNavigate = { nav.navigate(it) }) }

        // ==================== HR ====================
        composable(Routes.HRDetail) {
            HRDetailScreen(onBack = { nav.popBackStack() }, onMeasure = { nav.navigate(Routes.HRMeasure) })
        }
        composable(Routes.HRMeasure) {
            HRMeasureScreen(
                onBack = { nav.popBackStack() },
                onFinished = {
                    // 测量页 → 结果页（替换当前栈帧，避免回退又回到测量中）
                    nav.navigate(Routes.HRResult) {
                        popUpTo(Routes.HRMeasure) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.HRResult) {
            HRResultScreen(onDone = { nav.popBackStack() })
        }

        // ==================== BP ====================
        composable(Routes.BPDetail) {
            BPDetailScreen(onBack = { nav.popBackStack() }, onAdd = { nav.navigate(Routes.BPAdd) })
        }
        composable(Routes.BPAdd) {
            BPAddScreen(onBack = { nav.popBackStack() }, onSaved = { nav.popBackStack() })
        }
        composable(Routes.BPHistory) { BPHistoryScreen(onBack = { nav.popBackStack() }) }

        // ==================== BS ====================
        composable(Routes.BSDetail) {
            BSDetailScreen(onBack = { nav.popBackStack() }, onAdd = { nav.navigate(Routes.BSAdd) })
        }
        composable(Routes.BSAdd) {
            BSAddScreen(onBack = { nav.popBackStack() }, onSaved = { nav.popBackStack() })
        }
        composable(Routes.BSHistory) { BSHistoryScreen(onBack = { nav.popBackStack() }) }

        // ==================== Knowledge ====================
        composable(
            route = Routes.KnowledgeDetail,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { back ->
            val id = back.arguments?.getLong("id") ?: 0L
            KnowledgeDetailScreen(id = id, onBack = { nav.popBackStack() })
        }

        // ==================== AI ====================
        composable(Routes.AiChat) {
            AiChatScreen(
                onBack = { nav.popBackStack() },
                onHistory = { nav.navigate(Routes.AiHistory) },
                onStartTest = { id -> nav.navigate("ai/test/$id") }
            )
        }
        composable(Routes.AiHistory) { AiHistoryScreen(onBack = { nav.popBackStack() }) }
        composable(
            route = "ai/test/{testId}",
            arguments = listOf(navArgument("testId") { type = NavType.StringType })
        ) { back ->
            val tid = back.arguments?.getString("testId") ?: ""
            HealthTestScreen(testId = tid, onBack = { nav.popBackStack() })
        }

        // ==================== Reminder ====================
        composable(Routes.Reminder) { ReminderListScreen(onBack = { nav.popBackStack() }) }

        // ==================== Settings ====================
        composable(Routes.Profile) { ProfileEditScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.Language) { LanguageSetScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.Feedback) { FeedbackScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.Privacy) { PrivacyScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.Terms) { TermsScreen(onBack = { nav.popBackStack() }) }
    }
}
