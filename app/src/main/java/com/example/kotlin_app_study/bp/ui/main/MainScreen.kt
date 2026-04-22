package com.example.kotlin_app_study.bp.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_app_study.bp.ads.AdBanner
import com.example.kotlin_app_study.bp.theme.BPColors
import com.example.kotlin_app_study.bp.ui.main.tabs.HomeTab
import com.example.kotlin_app_study.bp.ui.main.tabs.NewsTab
import com.example.kotlin_app_study.bp.ui.main.tabs.SettingsTab

private data class TabItem(val title: String, val icon: ImageVector)

private val tabs = listOf(
    TabItem("主页面", Icons.Rounded.Home),
    TabItem("资讯", Icons.Rounded.MenuBook),
    TabItem("设置", Icons.Rounded.Settings),
)

@Composable
fun MainScreen(onNavigate: (String) -> Unit) {
    var index by remember { mutableIntStateOf(0) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                AdBanner(modifier = Modifier.fillMaxWidth())
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 0.dp,
                ) {
                    tabs.forEachIndexed { i, item ->
                        NavigationBarItem(
                            selected = index == i,
                            onClick = { index = i },
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title, fontSize = 12.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onBackground,
                                selectedTextColor = MaterialTheme.colorScheme.onBackground,
                                unselectedIconColor = BPColors.OnSurfaceVariant,
                                unselectedTextColor = BPColors.OnSurfaceVariant,
                                indicatorColor = androidx.compose.ui.graphics.Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        when (index) {
            0 -> HomeTab(modifier = Modifier.padding(padding), onNavigate = onNavigate)
            1 -> NewsTab(modifier = Modifier.padding(padding), onNavigate = onNavigate)
            2 -> SettingsTab(modifier = Modifier.padding(padding), onNavigate = onNavigate)
        }
    }
}
