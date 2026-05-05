package com.fhnav.app.util

object Constants {
    // Supabase
    const val SUPABASE_URL = BuildConfig.SUPABASE_URL
    const val SUPABASE_ANON_KEY = BuildConfig.SUPABASE_ANON_KEY

    // Navigation
    const val DEFAULT_SPEED_LIMIT = 120 // km/h
    const val SPEED_ALERT_BUFFER = 5 // km/h over limit before alert
    const val LOCATION_UPDATE_INTERVAL = 2000L // ms
    const val LOCATION_MIN_DISTANCE = 5f // meters

    // Voice
    const val TTS_SPEECH_RATE = 1.1f
    const val TTS_PITCH = 1.0f

    // Database
    const val DATABASE_NAME = "fhnav_database"
    const val MAX_SEARCH_HISTORY = 50

    // Map
    const val DEFAULT_LATITUDE = 39.9042  // Beijing
    const val DEFAULT_LONGITUDE = 116.4074
    const val DEFAULT_ZOOM = 15f

    // Navigation
    const val ARRIVAL_THRESHOLD_METERS = 30
    const val REROUTE_THRESHOLD_METERS = 100

    // Phrase categories
    const val PHASE_CATEGORY_GREETING = "greeting"
    const val PHASE_CATEGORY_WARNING = "warning"
    const val PHASE_CATEGORY_DIRECTION = "direction"
    const val PHASE_CATEGORY_SPEED = "speed"
    const val PHASE_CATEGORY_EMERGENCY = "emergency"
    const val PHASE_CATEGORY_CUSTOM = "custom"

    // Deep links
    const val DEEP_LINK_SCHEME = "fhnav"
    const val DEEP_LINK_HOST = "app"

    // Notification
    const val NOTIFICATION_CHANNEL_LOCATION = "fhnav_location_channel"
    const val NOTIFICATION_CHANNEL_VOICE = "fhnav_voice_channel"
    const val NOTIFICATION_ID_LOCATION = 1001
    const val NOTIFICATION_ID_PHRASE = 1002
}
