package com.example.kotlin_app_study.bp.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kotlin_app_study.bp.data.BPRepository
import com.example.kotlin_app_study.bp.data.Gender
import com.example.kotlin_app_study.bp.theme.BPColors
import com.example.kotlin_app_study.bp.ui.components.BPCard
import com.example.kotlin_app_study.bp.ui.components.BPTopBar
import com.example.kotlin_app_study.bp.ui.components.ThreeRowWheelColumn

// ==================== Profile Edit ====================

@Composable
fun ProfileEditScreen(onBack: () -> Unit) {
    val profile by BPRepository.profile.collectAsStateWithLifecycle()
    var genderDialog by remember { mutableStateOf(false) }
    var ageDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BPTopBar(title = "个人资料", onBack = onBack)
        Column(modifier = Modifier.padding(16.dp)) {
            BPCard {
                Column {
                    SettingsRow("性别", profile.gender.zh, onClick = { genderDialog = true })
                    DividerLine()
                    SettingsRow("年龄", "${profile.age}", onClick = { ageDialog = true })
                }
            }
        }
    }

    if (genderDialog) {
        AlertDialog(
            onDismissRequest = { genderDialog = false },
            confirmButton = { TextButton(onClick = { genderDialog = false }) { Text("好的") } },
            title = { Text("性别") },
            text = {
                Column {
                    Gender.entries.forEach { g ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable {
                                BPRepository.updateProfile { it.copy(gender = g) }
                                genderDialog = false
                            }.padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(g.zh, modifier = Modifier.weight(1f))
                            if (g == profile.gender) Icon(Icons.Rounded.Check, contentDescription = null, tint = BPColors.Primary)
                        }
                    }
                }
            }
        )
    }

    if (ageDialog) {
        var newAge by remember { mutableStateOf(profile.age) }
        AlertDialog(
            onDismissRequest = { ageDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    BPRepository.updateProfile { it.copy(age = newAge) }
                    ageDialog = false
                }) { Text("保存") }
            },
            dismissButton = { TextButton(onClick = { ageDialog = false }) { Text("取消") } },
            title = { Text("年龄") },
            text = {
                ThreeRowWheelColumn(
                    range = 1..120, value = newAge, onValueChange = { newAge = it },
                    centerFontSize = 36, sideFontSize = 22, rowHeightDp = 48,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }
}

@Composable
private fun SettingsRow(label: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Text(value, color = BPColors.OnSurfaceVariant, fontSize = 14.sp)
        Icon(
            Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = BPColors.OnSurfaceVariant
        )
    }
}

@Composable
private fun DividerLine() {
    Box(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().height(1.dp).background(BPColors.Divider.copy(alpha = 0.4f)))
}

// ==================== Language Set ====================

@Composable
fun LanguageSetScreen(onBack: () -> Unit) {
    val profile by BPRepository.profile.collectAsStateWithLifecycle()
    val langs = listOf(
        "zh_CN" to "简体中文", "zh_TW" to "繁体中文", "en" to "English",
        "es" to "Español", "fr" to "Français", "de" to "Deutsch",
        "it" to "Italiano", "pt" to "Português", "ru" to "Русский",
        "ja" to "日本語", "ko" to "한국어", "ar" to "العربية",
        "tr" to "Türkçe", "vi" to "Tiếng Việt", "th" to "ภาษาไทย",
        "in_ID" to "Bahasa Indonesia", "ms" to "Bahasa Melayu",
        "nl" to "Nederlands", "sv" to "Svenska", "fi" to "Suomi",
        "nb" to "Norsk", "pl" to "Polski", "cs" to "Čeština", "fa" to "فارسی"
    )
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BPTopBar(title = "语言", onBack = onBack)
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            items(langs) { (code, name) ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clickable {
                            BPRepository.updateProfile { it.copy(language = code) }
                            onBack()
                        }
                        .padding(vertical = 14.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(name, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f), fontSize = 16.sp)
                    if (code == profile.language) {
                        Icon(Icons.Rounded.Check, contentDescription = null, tint = BPColors.Primary)
                    }
                }
            }
        }
    }
}

// ==================== Feedback ====================

@Composable
fun FeedbackScreen(onBack: () -> Unit) {
    var input by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BPTopBar(title = "写下反馈", onBack = onBack)
        Column(modifier = Modifier.padding(16.dp)) {
            BPCard {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    BasicTextField(
                        value = input,
                        onValueChange = { input = it },
                        cursorBrush = SolidColor(BPColors.Primary),
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp),
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        decorationBox = { inner ->
                            if (input.isEmpty()) Text("请说出你的看法和建议...", color = BPColors.OnSurfaceVariant, fontSize = 14.sp)
                            inner()
                        }
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(24.dp))
                    .background(if (input.isNotBlank()) BPColors.Primary else BPColors.SurfaceVariant)
                    .clickable(enabled = input.isNotBlank()) { sent = true; input = "" },
                contentAlignment = Alignment.Center
            ) { Text("提交", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            if (sent) {
                Spacer(Modifier.height(20.dp))
                Text("✅ 感谢反馈，我们会认真查看。", color = BPColors.OnSurfaceVariant, fontSize = 14.sp)
            }
        }
    }
}

// ==================== Privacy / Terms ====================

@Composable
fun PrivacyScreen(onBack: () -> Unit) = LongTextScreen(onBack, "隐私政策", privacyText)

@Composable
fun TermsScreen(onBack: () -> Unit) = LongTextScreen(onBack, "用户协议", termsText)

@Composable
private fun LongTextScreen(onBack: () -> Unit, title: String, body: String) {
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BPTopBar(title = title, onBack = onBack)
        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            item {
                BPCard {
                    Text(
                        body,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

private val privacyText = """
我们非常重视您的隐私和数据安全。本应用为纯静态演示版本，不会上传任何健康数据到服务器。

1. 数据本地存储：所有血压、心率、血糖、提醒等记录都保存在您的设备本地内存中，进程退出后清除。
2. 相机权限：仅用于在心率测量界面通过 PPG 算法计算瞬时心率，相机帧不会被保存或上传。
3. 第三方共享：本演示版不接入任何第三方 SDK，不向广告或数据分析服务发送信息。
4. 您的权利：您可以随时清除应用数据、卸载应用以删除全部本地数据。
""".trimIndent()

private val termsText = """
欢迎使用本血压追踪 Demo（以下简称"本应用"）。

1. 服务说明：本应用为静态演示版本，提供血压、心率、血糖记录与可视化。所有计算（含心率 PPG 算法）均在您本机完成，不连接任何在线服务。
2. 责任声明：本应用提供的任何健康指标解读仅供参考，不能替代专业医疗建议。如有不适请及时就医。
3. 风险免责：心率 PPG 测量结果受手机机型、环境光线、手指放置位置影响，可能存在误差。
4. 协议变更：因本应用仅为离线演示版本，本协议如有更新将随版本更新一起发布。
""".trimIndent()
