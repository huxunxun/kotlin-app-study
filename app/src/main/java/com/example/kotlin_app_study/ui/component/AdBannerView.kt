package com.example.kotlin_app_study.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.FrameLayout
import com.example.kotlin_app_study.ads.AdManager
import com.example.kotlin_app_study.util.findActivity

@Composable
fun AdBannerView(modifier: Modifier = Modifier) {
    if (AdManager.isAdFree()) return

    AndroidView(
        factory = { context ->
            FrameLayout(context).also { container ->
                context.findActivity()?.let { activity ->
                    AdManager.loadBanner(activity, container)
                }
            }
        },
        modifier = modifier.fillMaxWidth()
    )
}
