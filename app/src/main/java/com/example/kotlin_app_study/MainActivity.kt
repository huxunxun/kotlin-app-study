package com.example.kotlin_app_study

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.kotlin_app_study.ads.AdManager
import com.example.kotlin_app_study.bridge.AdBridge
import com.example.kotlin_app_study.bridge.NativeBridge
import com.example.kotlin_app_study.ui.component.AdBannerView
import com.example.kotlin_app_study.ui.screen.HomeScreen
import com.example.kotlin_app_study.ui.theme.KotlinappstudyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        initBridge()
        preloadAds()

        setContent {
            KotlinappstudyTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { AdBannerView() }
                ) { innerPadding ->
                    HomeScreen(
                        modifier = Modifier.padding(innerPadding),
                        onShowInterstitial = {
                            AdManager.showInterstitial(this)
                        },
                        onShowRewarded = { onRewarded ->
                            AdManager.showRewarded(this, onRewarded = onRewarded)
                        }
                    )
                }
            }
        }
    }

    private fun initBridge() {
        AdBridge.init(this, NativeBridge)
    }

    private fun preloadAds() {
        AdManager.preloadInterstitial(this)
        AdManager.preloadRewarded(this)
        AdManager.preloadAppOpen(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        AdManager.destroyBanner()
        AdBridge.destroy()
    }
}
