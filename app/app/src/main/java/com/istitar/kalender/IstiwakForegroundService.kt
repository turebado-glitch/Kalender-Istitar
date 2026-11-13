package com.istitar.kalender

import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.content.ContextCompat

class IstiwakForegroundService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private var lastDayKey: String = ""

    private val minuteTickRunnable = object : Runnable {
        override fun run() {
            updateOngoingStatusBar()
            val nowMs = System.currentTimeMillis()
            val delayToNextMinute = 60_000L - (nowMs % 60_000L) + 150L
            handler.postDelayed(this, delayToNextMinute)
        }
    }

    private val timeChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateOngoingStatusBar()
        }
    }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.ensureChannels(this)
        lastDayKey = IstitarCalculator.dayKey()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
            addAction(Intent.ACTION_DATE_CHANGED)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(timeChangeReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(timeChangeReceiver, filter)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationHelper.buildStatusNotification(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NotificationHelper.ONGOING_STATUS_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NotificationHelper.ONGOING_STATUS_NOTIFICATION_ID, notification)
        }
        handler.removeCallbacks(minuteTickRunnable)
        handler.post(minuteTickRunnable)
        return START_STICKY
    }

    private fun updateOngoingStatusBar() {
        val currentDayKey = IstitarCalculator.dayKey()
        if (currentDayKey != lastDayKey) {
            lastDayKey = currentDayKey
            NotificationHelper.rescheduleNativeAlarmsForToday(this)
        }
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(
            NotificationHelper.ONGOING_STATUS_NOTIFICATION_ID,
            NotificationHelper.buildStatusNotification(this)
        )
    }

    override fun onDestroy() {
        handler.removeCallbacks(minuteTickRunnable)
        try {
            unregisterReceiver(timeChangeReceiver)
        } catch (_: Exception) {
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        fun startOrUpdate(context: Context) {
            val intent = Intent(context, IstiwakForegroundService::class.java)
            try {
                ContextCompat.startForegroundService(context, intent)
            } catch (_: Exception) {
            }
        }
    }
}
