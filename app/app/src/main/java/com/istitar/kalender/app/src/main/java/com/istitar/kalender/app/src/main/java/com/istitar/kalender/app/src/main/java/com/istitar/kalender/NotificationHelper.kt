package com.istitar.kalender

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationHelper  {
    const val CHANNEL_STATUS_ID = "istiwak_status_channel"
    const val ONGOING_STATUS_NOTIFICATION_ID = 1001

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val statusChannel = NotificationChannel(
                CHANNEL_STATUS_ID,
                "Status Kalender Istitar",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Menampilkan informasi waktu istiwak dan jadwal harian secara langsung."
                setShowBadge(false)
            }
            nm.createNotificationChannel(statusChannel)
        }
    }

    fun buildStatusNotification(context: Context): Notification {
        ensureChannels(context)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "Kalender Istitar Aktif"
        val text = "Memantau waktu istiwak dan jadwal harian secara real-time."

        return NotificationCompat.Builder(context, CHANNEL_STATUS_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_menu_today)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    fun rescheduleNativeAlarmsForToday(context: Context) {
        // Placeholder untuk sinkronisasi alarm harian
    }
}
