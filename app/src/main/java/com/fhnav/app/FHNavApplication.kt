package com.fhnav.app

import android.app.Application
import android.util.Log
import com.amap.api.maps.MapsInitializer
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for FH Navigation.
 *
 * Responsibilities:
 * - Hilt application entry point (@HiltAndroidApp)
 * - AMap SDK initialization with privacy compliance
 * - Global configuration
 */
@HiltAndroidApp
class FHNavApplication : Application() {

    companion object {
        private const val TAG = "FHNavApplication"
    }

    override fun onCreate() {
        super.onCreate()

        initAMap()
    }

    /**
     * Initialize AMap SDK with privacy compliance.
     *
     * AMap requires explicit privacy consent before SDK usage.
     * This must be called before any AMap API usage.
     */
    private fun initAMap() {
        try {
            // AMap privacy compliance: set consent before SDK init
            // UpdateInterval must be set to true per AMap requirements
            MapsInitializer.updatePrivacyShow(this, true, true)
            MapsInitializer.updatePrivacyAgree(this, true)

            Log.d(TAG, "AMap SDK initialized with privacy consent")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize AMap SDK", e)
        }
    }
}
