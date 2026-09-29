package com.example.data.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.startapp.sdk.adsbase.Ad
import com.startapp.sdk.adsbase.StartAppAd
import com.startapp.sdk.adsbase.StartAppSDK
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener
import com.startapp.sdk.adsbase.adlisteners.AdEventListener
import com.startapp.sdk.adsbase.adlisteners.VideoListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object StartIoAdManager {
    private const val TAG = "StartIoAdManager"
    
    // Default Start.io App ID (can be updated or set by developer in settings/gradle)
    var appId: String = "208765432"
    var isTestAdsEnabled: Boolean = true
    private var isInitialized = false

    private var currentRewardedAd: StartAppAd? = null

    private val _isAdLoading = MutableStateFlow(false)
    val isAdLoading: StateFlow<Boolean> = _isAdLoading.asStateFlow()

    private val _isAdLoaded = MutableStateFlow(false)
    val isAdLoaded: StateFlow<Boolean> = _isAdLoaded.asStateFlow()

    init {
        try {
            val buildConfigId = com.example.BuildConfig.STARTIO_APP_ID
            if (!buildConfigId.isNullOrBlank() && buildConfigId != "208765432") {
                appId = buildConfigId
            }
        } catch (_: Throwable) {}
    }

    fun initialize(context: Context, customAppId: String? = null, testMode: Boolean = true) {
        if (isInitialized && customAppId == null) return
        if (!customAppId.isNullOrBlank()) {
            appId = customAppId.trim()
        }
        isTestAdsEnabled = testMode
        try {
            // Initialize Start.io In-App SDK with return ads disabled
            StartAppSDK.init(context, appId, false)
            // Enable test ads for development & testing
            StartAppSDK.setTestAdsEnabled(isTestAdsEnabled)
            isInitialized = true
            Log.d(TAG, "Start.io SDK initialized successfully with App ID: $appId (TestMode: $isTestAdsEnabled)")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Start.io SDK: ${e.message}")
        }
    }

    fun updateConfiguration(context: Context, newAppId: String, testMode: Boolean) {
        val clean = newAppId.trim()
        if (clean.isNotBlank()) {
            appId = clean
        }
        isTestAdsEnabled = testMode
        try {
            StartAppSDK.init(context, appId, false)
            StartAppSDK.setTestAdsEnabled(isTestAdsEnabled)
            preloadRewardedVideo(context)
            Log.d(TAG, "Start.io configuration reloaded with App ID: $appId, TestMode: $isTestAdsEnabled")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to reconfigure Start.io: ${e.message}")
        }
    }

    fun preloadRewardedVideo(context: Context) {
        try {
            if (!isInitialized) {
                initialize(context)
            }
            _isAdLoading.value = true
            val ad = StartAppAd(context)
            ad.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, object : AdEventListener {
                override fun onReceiveAd(ad: Ad) {
                    _isAdLoading.value = false
                    _isAdLoaded.value = true
                    currentRewardedAd = ad as? StartAppAd ?: currentRewardedAd
                    Log.d(TAG, "Start.io Rewarded Video loaded successfully!")
                }

                override fun onFailedToReceiveAd(ad: Ad?) {
                    _isAdLoading.value = false
                    _isAdLoaded.value = false
                    Log.w(TAG, "Start.io failed to receive rewarded video: ${ad?.errorMessage}")
                }
            })
            currentRewardedAd = ad
        } catch (e: Exception) {
            _isAdLoading.value = false
            _isAdLoaded.value = false
            Log.e(TAG, "Exception while preloading rewarded ad: ${e.message}")
        }
    }

    /**
     * Shows the Start.io Rewarded Video Ad.
     * When completed, invokes onRewardEarned() so the user gets their +1 Heart ❤️.
     * If the ad fails or cannot be displayed, invokes onAdFailed().
     */
    fun showRewardedVideo(
        activity: Activity,
        onRewardEarned: () -> Unit,
        onAdDismissed: () -> Unit,
        onAdFailed: (String) -> Unit
    ) {
        val ad = currentRewardedAd
        if (ad == null) {
            // Ad not preloaded, attempt immediate load and fallback
            Log.w(TAG, "Rewarded Ad is null. Preloading for next time.")
            preloadRewardedVideo(activity)
            onAdFailed("Ad is loading or unavailable. You can watch simulated ad.")
            return
        }

        var rewardAwarded = false

        ad.setVideoListener(object : VideoListener {
            override fun onVideoCompleted() {
                Log.d(TAG, "Start.io Rewarded Video completed! Granting reward ❤️")
                rewardAwarded = true
                onRewardEarned()
            }
        })

        val displayed = try {
            ad.showAd(object : AdDisplayListener {
                override fun adHidden(ad: Ad?) {
                    Log.d(TAG, "Start.io Ad hidden/closed")
                    _isAdLoaded.value = false
                    // Preload the next rewarded video
                    preloadRewardedVideo(activity)
                    onAdDismissed()
                }

                override fun adDisplayed(ad: Ad?) {
                    Log.d(TAG, "Start.io Ad displayed on screen")
                }

                override fun adClicked(ad: Ad?) {
                    Log.d(TAG, "Start.io Ad clicked")
                }

                override fun adNotDisplayed(ad: Ad?) {
                    Log.w(TAG, "Start.io Ad could not be displayed: ${ad?.errorMessage}")
                    _isAdLoaded.value = false
                    preloadRewardedVideo(activity)
                    onAdFailed(ad?.errorMessage ?: "Ad could not be shown.")
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error displaying Start.io Ad: ${e.message}")
            false
        }

        if (!displayed) {
            _isAdLoaded.value = false
            preloadRewardedVideo(activity)
            onAdFailed("Start.io ad not ready to show.")
        }
    }
}
