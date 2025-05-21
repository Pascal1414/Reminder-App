package com.pascalrieder.reminder_app

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import com.pascalrieder.reminder_app.broadcastreceiver.AlarmReceiver
import com.pascalrieder.reminder_app.model.Interval
import com.pascalrieder.reminder_app.model.Reminder
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import kotlin.collections.isNotEmpty

class NotificationHandler {
    /**
     * Schedules a notification for the given reminder.
     * @return true if the notification was scheduled successfully, false otherwise.
     */
    suspend fun scheduleNotification(
        context: Context,
        reminderId: Long,
        reminder: Reminder
    ): Boolean {

        if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return false
        }

        val pendingIntent = getPendingIntent(context, reminderId, reminder.name)

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

        val db = AppDatabase.getInstance(context)
        val today = LocalDate.now()
        val startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay =
            today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
        val notifications =
            db.notificationDao().getByReminderIdBetweenTimes(reminderId, startOfDay, endOfDay)

        // Adding a time buffer of 10 min to the current time, because the alarm manager is not 100% accurate
        // Checking if there is already a notification for the same reminder today prevents multiple notifications
        if (triggerAt < (System.currentTimeMillis() - (10 * 60 * 1000)) || notifications.isNotEmpty()) {
            Log.i(
                "NotificationHandler",
                "Notification for reminderID: $reminderId is rescheduled. ${
                    if (triggerAt < (System.currentTimeMillis() - (10 * 60 * 1000))) "Trigger time is in the past"
                    else "Notification already exists"
                }"
            )
            val intervalMillis = when (reminder.interval) {
                Interval.Daily -> AlarmManager.INTERVAL_DAY
                Interval.Weekly -> AlarmManager.INTERVAL_DAY * 7
            }

            triggerAt += intervalMillis
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (!alarmManager.canScheduleExactAlarms()) {
            return false
        }

        Log.i(
            "NotificationHandler",
            "Scheduling alarm for reminderId: $reminderId, reminderName: ${reminder.name}, triggerAt: ${
                Instant.ofEpochMilli(triggerAt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            }"
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAt,
            pendingIntent
        )
        return true
    }

    fun cancelNotification(context: Context, reminderId: Long, reminderName: String) {
        Log.i(
            "NotificationHandler",
            "Canceling alarm for reminderId: $reminderId, reminderName: $reminderName"
        )
        val pendingIntent = getPendingIntent(context, reminderId, reminderName)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    fun getPendingIntent(context: Context, reminderId: Long, reminderName: String): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("reminderId", reminderId)
        }

        return PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}