package com.fhnav.app.di

import android.content.Context
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing AMap (高德) location client and related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    @Singleton
    fun provideAMapLocationClient(@ApplicationContext context: Context): AMapLocationClient {
        return AMapLocationClient(context)
    }

    @Provides
    @Singleton
    fun provideAMapLocationOption(): AMapLocationClientOption {
        return AMapLocationClientOption().apply {
            locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            isOnceLocation = false
            interval = 10_000L // 10 seconds
            isNeedAddress = true
            isMockEnable = false
            httpTimeOut = 20_000L
        }
    }
}
