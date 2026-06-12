package com.example.gurasleep.domain.scheduler

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

/**
 * 前台音频播放 Service
 *  保证后台播放不会被系统杀死
 */
class AudioService : Service() {

    companion object {
        const val CHANNEL_ID = "gurasleep_audio"
        const val NOTIFICATION_ID = 2001
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val openIntent = Intent(this, com.example.gurasleep.MainActivity::class.java)
        val pending = PendingIntent.getActivity(
            this, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("GuraSleep")
            .setContentText("白噪音正在播放...")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pending)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        when (intent?.action) {
            AlarmScheduler.ACTION_STOP_AUDIO -> {
                sendBroadcast(Intent("com.example.gurasleep.STOP_AUDIO_TRIGGERED"))
            }
            AlarmScheduler.ACTION_WAKE_UP -> {
                sendBroadcast(Intent("com.example.gurasleep.WAKE_UP_TRIGGERED"))
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
