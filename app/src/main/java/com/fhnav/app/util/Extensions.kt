package com.fhnav.app.util

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

// String extensions
fun String.isValidPhone(): Boolean {
    val cleaned = replace(Regex("[^0-9+]"), "")
    return cleaned.length >= 10 && cleaned.length <= 15
}

fun String.isValidOtp(): Boolean {
    return length == 6 && all { it.isDigit() }
}

// Long extensions (timestamp/duration)
fun Long.toFormattedDuration(): String {
    val hours = TimeUnit.SECONDS.toHours(this)
    val minutes = TimeUnit.SECONDS.toMinutes(this) % 60
    val seconds = this % 60
    return when {
        hours > 0 -> String.format("%dh %02dmin", hours, minutes)
        minutes > 0 -> String.format("%dmin %02ds", minutes, seconds)
        else -> "${seconds}s"
    }
}

fun Long.toFormattedDistance(): String {
    return if (this >= 1000) {
        String.format("%.1f km", this / 1000.0)
    } else {
        "$this m"
    }
}

fun Long.toFormattedTime(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.toFormattedDateTime(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}

// Float extensions
fun Float.kmhToMs(): Float = this / 3.6f
fun Float.msToKmh(): Float = this * 3.6f

// Double extensions
fun Double.formatCoordinates(): String {
    return String.format("%.6f", this)
}

// Flow extensions
fun <T> Flow<T>.asResult(): Flow<Result<T>> {
    return map<T, Result<T>> { Result.success(it) }
        .catch { emit(Result.failure(it)) }
}

// Context extensions
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.showToast(resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, resId, duration).show()
}
