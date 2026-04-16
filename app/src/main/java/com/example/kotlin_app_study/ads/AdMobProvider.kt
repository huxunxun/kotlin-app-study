package com.example.kotlin_app_study.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdMobProvider : AdProvider {

    override val providerName: String = "AdMob"

    private val tag = "AdMob"
    private var bannerAdView: AdView? = null
    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var appOpenAd: AppOpenAd? = null

    override fun initialize(context: Context, onComplete: () -> Unit) {
        MobileAds.initialize(context) {
            Log.d(tag, "AdMob SDK initialized")
            onComplete()
        }
    }

    // ============ Banner ============

    override fun loadBanner(activity: Activity, container: ViewGroup, adUnitId: String) {
        destroyBanner()
        val logTag = tag
        bannerAdView = AdView(activity).apply {
            setAdSize(AdSize.BANNER)
            this.adUnitId = adUnitId
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    Log.d(logTag, "Banner loaded")
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e(logTag, "Banner failed: $error")
                }
            }
        }
        container.removeAllViews()
        container.addView(bannerAdView)
        bannerAdView?.loadAd(AdRequest.Builder().build())
    }

    override fun destroyBanner() {
        bannerAdView?.destroy()
        bannerAdView = null
    }

    // ============ Interstitial ============

    override fun loadInterstitial(activity: Activity, adUnitId: String, onLoaded: () -> Unit, onFailed: (String) -> Unit) {
        InterstitialAd.load(
            activity,
            adUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    Log.d(tag, "Interstitial loaded")
                    onLoaded()
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    Log.e(tag, "Interstitial failed: ${error.toString()}")
                    onFailed(error.toString())
                }
            }
        )
    }

    override fun showInterstitial(activity: Activity, onDismissed: () -> Unit) {
        val ad = interstitialAd ?: run {
            onDismissed()
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                onDismissed()
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                interstitialAd = null
                onDismissed()
            }
        }
        ad.show(activity)
    }

    override fun isInterstitialReady(): Boolean = interstitialAd != null

    // ============ Rewarded ============

    override fun loadRewarded(activity: Activity, adUnitId: String, onLoaded: () -> Unit, onFailed: (String) -> Unit) {
        RewardedAd.load(
            activity,
            adUnitId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    Log.d(tag, "Rewarded loaded")
                    onLoaded()
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    Log.e(tag, "Rewarded failed: ${error.toString()}")
                    onFailed(error.toString())
                }
            }
        )
    }

    override fun showRewarded(activity: Activity, onRewarded: () -> Unit, onDismissed: () -> Unit) {
        val ad = rewardedAd ?: run {
            onDismissed()
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                onDismissed()
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                rewardedAd = null
                onDismissed()
            }
        }
        ad.show(activity) {
            Log.d(tag, "User earned reward: ${it.amount} ${it.type}")
            onRewarded()
        }
    }

    override fun isRewardedReady(): Boolean = rewardedAd != null

    // ============ App Open ============

    override fun loadAppOpen(activity: Activity, adUnitId: String, onLoaded: () -> Unit, onFailed: (String) -> Unit) {
        AppOpenAd.load(
            activity,
            adUnitId,
            AdRequest.Builder().build(),
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    Log.d(tag, "App Open loaded")
                    onLoaded()
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    appOpenAd = null
                    Log.e(tag, "App Open failed: ${error.toString()}")
                    onFailed(error.toString())
                }
            }
        )
    }

    override fun showAppOpen(activity: Activity, onDismissed: () -> Unit) {
        val ad = appOpenAd ?: run {
            onDismissed()
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                onDismissed()
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                appOpenAd = null
                onDismissed()
            }
        }
        ad.show(activity)
    }

    override fun isAppOpenReady(): Boolean = appOpenAd != null

    override fun destroy() {
        destroyBanner()
        interstitialAd = null
        rewardedAd = null
        appOpenAd = null
    }
}
