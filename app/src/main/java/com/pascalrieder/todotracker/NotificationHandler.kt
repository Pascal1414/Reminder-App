package com.pascalrieder.todotracker

import android.R
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.pascalrieder.todotracker.broadcastreceiver.NotificationReceiver
import com.pascalrieder.todotracker.model.Interval
import com.pascalrieder.todotracker.model.Reminder
import java.util.Calendar

class NotificationHandler {
    /**
     * Schedules a notification for the given reminder.
     * @return true if the notification was scheduled successfully, false otherwise.
     */
    fun scheduleNotification(context: Context, reminderId: Long, reminder: Reminder): Boolean {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("reminderId", reminderId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        var triggerAt = if (reminder.interval == Interval.Daily) {
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, reminder.time.hour)
                set(Calendar.MINUTE, reminder.time.minute)
            }.timeInMillis
        } else {
            Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, reminder.weekday!!.ordinal)
                set(Calendar.HOUR_OF_DAY, reminder.time.hour)
                set(Calendar.MINUTE, reminder.time.minute)
            }.timeInMillis
        }

        if (triggerAt < System.currentTimeMillis()) {
            val intervalMillis = when (reminder.interval) {
                Interval.Daily -> AlarmManager.INTERVAL_DAY
                Interval.Weekly -> AlarmManager.INTERVAL_DAY * 7
            }

            triggerAt += intervalMillis
        }

        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                pendingIntent
            )
            return true
        }

        return false
    }

    fun cancelNotification(context: Context, reminderId: Long) {

    }
}