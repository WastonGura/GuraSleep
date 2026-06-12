package com.example.gurasleep.domain.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

/**
 * 定时关停 & 早晨唤醒调度器
 */
class AlarmScheduler(private val context: Context) {

    companion object {
        const val ACTION_STOP_AUDIO = "com.example.gurasleep.STOP_AUDIO"
        const val ACTION_WAKE_UP = "com.example.gurasleep.WAKE_UP"
        private const val REQUEST_STOP = 1001
        private const val REQUEST_WAKE = 1002
    }

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /** 定时关停：delayMinutes 分钟后触发 */
    fun scheduleStop(delayMinutes: Int) {
        val triggerTime = System.currentTimeMillis() + delayMinutes * 60_000L
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_STOP_AUDIO
        }
        val pending = PendingIntent.getBroadcast(
            context, REQUEST_STOP, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pending)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pending)
        }
    }

    fun cancelStop() {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_STOP_AUDIO
        }
        val pending = PendingIntent.getBroadcast(
            context, REQUEST_STOP, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pending)
    }

    /** 早晨唤醒 */
    fun scheduleWakeUp(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_WAKE_UP
        }
        val pending = PendingIntent.getBroadcast(
            context, REQUEST_WAKE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pending
            )
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pending)
        }
    }

    fun cancelWakeUp() {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_WAKE_UP
        }
        val pending = PendingIntent.getBroadcast(
            context, REQUEST_WAKE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pending)
    }
}
