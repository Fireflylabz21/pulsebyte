package com.fireflylabz.taptap.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.fireflylabz.taptap.MainActivity

object BreakingNewsNotificationHelper {
    private const val CHANNEL_ID = "breaking_news_channel"
    private const val CHANNEL_NAME = "Breaking News Alerts"
    private const val NOTIFICATION_ID = 1001
    private const val PREFS_NAME = "pulse_notifications_prefs"
    private const val KEY_NOTIFIED_IDS = "notified_cluster_ids"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for breaking news stories"
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendBreakingNewsNotification(context: Context, sourceUrl: String, title: String) {
        if (sourceUrl.isBlank()) return

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val notifiedSet = prefs.getStringSet(KEY_NOTIFIED_IDS, emptySet()) ?: emptySet()

        if (notifiedSet.contains(sourceUrl)) {
            return
        }

        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("sourceUrl", sourceUrl)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            sourceUrl.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("🚨 BREAKING NEWS")
            .setContentText(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(title))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            try {
                notificationManager.notify(NOTIFICATION_ID, builder.build())

                val newSet = notifiedSet.toMutableSet()
                newSet.add(sourceUrl)
                prefs.edit().putStringSet(KEY_NOTIFIED_IDS, newSet).apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
