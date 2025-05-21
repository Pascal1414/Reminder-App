package com.pascalrieder.reminder_app.broadcastreceiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.pascalrieder.reminder_app.AppDatabase
import com.pascalrieder.reminder_app.MainActivity
import com.pascalrieder.reminder_app.NotificationHandler
import com.pascalrieder.reminder_app.R
import com.pascalrieder.reminder_app.dao.ReminderDao.Companion.toReminder
import com.pascalrieder.reminder_app.model.Notification
import com.pascalrieder.reminder_app.model.Reminder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime


class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val CHANNEL_ID = "Reminder Channel"
    }

    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            val notificationId = intent.getLongExtra("reminderId", -1)

            Log.i("AlarmReceiver", "Alarm received for reminderId: $notificationId")

            val reminder = getReminder(context, notificationId.toLong())

            if (reminder == null) {
                Log.i("AlarmReceiver", "Invalid reminder id: $notificationId")
                return@launch
            }

            if (!reminder.isDone()) {
                Log.i(
                    "AlarmReceiver",
                    "Reminder is not done, showing notification for reminderId: $notificationId"
                )

                val intentMainActivity = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val contentIntent =
                    PendingIntent.getActivity(
                        context,
                        0,
                        intentMainActivity,
                        PendingIntent.FLAG_IMMUTABLE
                    )

                val doneClickIntent =
                    Intent(context, NotificationActionReceiver::class.java).apply {
                        action = NotificationActionReceiver.Companion.ACTION_DONE
                        putExtra("reminderId", reminder.id)
                    }
                val doneClickPendingIntent: PendingIntent =
                    PendingIntent.getBroadcast(
                        context,
                        0,
                        doneClickIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )

                var notification =
                    NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle(reminder.name)
                        .setSmallIcon(R.drawable.ic_alarm)
                        .setContentText("Reminder for ${reminder.name}")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(contentIntent)
                        .setOngoing(true)
                        .addAction(R.drawable.ic_check, "Done", doneClickPendingIntent)
                        .build()

                val channel = getChannel(context)

                val notificationManager =
                    getSystemService(context, NotificationManager::class.java)!!
                notificationManager.createNotificationChannel(channel)

                notificationManager.notify(reminder.id.toInt(), notification)

                addNotification(context, reminder.id)

            } else {
                addNotification(context, reminder.id, true)
                Log.i("AlarmReceiver", "Reminder is already done, not showing notification")
            }

            NotificationHandler().scheduleNotification(
                context,
                reminder.id,
                reminder
            )
        }
    }

    private suspend fun getReminder(context: Context, reminderId: Long): Reminder? {
        val db = AppDatabase.getInstance(context)
        return db.reminderDao().getById(reminderId)?.toReminder()
    }

    private suspend fun addNotification(
        context: Context,
        reminderId: Long,
        wasAlreadyDone: Boolean = false
    ) {
        val db = AppDatabase.getInstance(context)
        val notification = Notification(
            reminderId = reminderId,
            shownAt = LocalDateTime.now(),
            wasAlreadyDone = wasAlreadyDone
        )
        db.notificationDao().create(notification)
    }

    private fun getChannel(context: Context): NotificationChannel {
        val name = "Reminder channel"
        val description = "Channel for reminder notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(CHANNEL_ID, name, importance)

        channel.description = description

        return channel
    }
}