package com.example.vinilos_grupo11.performance

import android.os.Build
import android.os.SystemClock
import android.util.Log

/**
 * Utility for measuring user-story execution time on physical devices.
 *
 * Filter Logcat with tag "VinilosPerf" to capture metrics.
 * Format: METRIC | story=<name> | device=<model> | duration=<ms>ms | result=<ok|error>
 */
object PerformanceTracker {

    private const val TAG = "VinilosPerf"

    private val deviceLabel: String by lazy {
        "${Build.MANUFACTURER} ${Build.MODEL} (API ${Build.VERSION.SDK_INT})"
    }

    fun beginSection(story: String): Long {
        val start = SystemClock.elapsedRealtime()
        Log.d(TAG, "BEGIN | story=$story | device=$deviceLabel")
        return start
    }

    fun endSection(story: String, startTime: Long, success: Boolean = true) {
        val elapsed = SystemClock.elapsedRealtime() - startTime
        val result = if (success) "ok" else "error"
        Log.i(TAG, "METRIC | story=$story | device=$deviceLabel | duration=${elapsed}ms | result=$result")
    }
}
