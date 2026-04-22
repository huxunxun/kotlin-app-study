package com.example.kotlin_app_study.bp.ui.main.tabs

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessAlarm
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_app_study.bp.Routes
import com.example.kotlin_app_study.bp.theme.BPColors
import com.example.kotlin_app_study.bp.ui.components.BPCard

@Composable
fun SettingsTab(modifier: Modifier = Modifier, onNavigate: (String) -> Unit) {
    var showSyncDialog by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { SyncCard(onClickSync = { showSyncDialog = true }) }

            item {
                SettingsGroup(title = "设置") {
                    SettingsRow(Icons.Rounded.AccessAlarm, "提醒") { onNavigate(Routes.Reminder) }
                    SettingsDivider()
                    SettingsRow(Icons.Rounded.Person, "个人资料") { onNavigate(Routes.Profile) }
                    SettingsDivider()
                    SettingsRow(Icons.Rounded.Language, "语言") { onNavigate(Routes.Language) }
                    SettingsDivider()
                    SettingsRow(Icons.Rounded.Logout, "导出资料") { /* 静态：无外部导出 */ }
                }
            }

            item {
                SettingsGroup(title = "更多") {
                    SettingsRow(Icons.Rounded.Star, "前往Google评分") {}
                    SettingsDivider()
                    SettingsRow(Icons.Rounded.Share, "点击 分享到...") {}
                    SettingsDivider()
                    SettingsRow(Icons.Rounded.Edit, "写下反馈") { onNavigate(Routes.Feedback) }
                    SettingsDivider()
                    SettingsRow(Icons.Rounded.Description, "隐私政策") { onNavigate(Routes.Privacy) }
                    SettingsDivider()
                    SettingsRow(Icons.Rounded.PrivacyTip, "用户协议") { onNavigate(Routes.Terms) }
                }
            }

            item { Spacer(Modifier.height(60.dp)) }
        }

        FloatingAiBubble(
            onClick = { onNavigate(Routes.AiChat) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }

    if (showSyncDialog) {
        SyncMockDialog(onDismiss = { showSyncDialog = false })
    }
}

@Composable
private fun SyncCard(onClickSync: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(BPColors.SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
        }
        Spacer(Modifier.height(10.dp))
        Text(
            "数据同步&恢复",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            "请登录并备份你的数据",
            fontSize = 13.sp,
            color = BPColors.OnSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(BPColors.Primary)
                .clickable { onClickSync() },
            contentAlignment = Alignment.Center
        ) {
            Text("同步", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SettingsGroup(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            title,
            color = BPColors.OnSurfaceVariant,
            fontSize = 13.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        BPCard {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.width(14.dp))
        Text(label, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .padding(start = 52.dp)
            .fillMaxWidth()
            .height(1.dp)
            .background(BPColors.Divider.copy(alpha = 0.4f))
    )
}

@Composable
private fun SyncMockDialog(onDismiss: () -> Unit) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) { Text("好的") }
        },
        title = { Text("选择账号") },
        text = {
            Column {
                Text("以继续使用血压追踪器")
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFB870D8)),
                        contentAlignment = Alignment.Center
                    ) { Text("D", color = Color.White, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Diu Allen", fontWeight = FontWeight.Medium)
                        Text("allendiu1@gmail.com", color = BPColors.OnSurfaceVariant, fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.IosShare, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text("添加其他账号", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    )
}
