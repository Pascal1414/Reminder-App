package com.pascalrieder.todotracker.broadcastreceiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pascalrieder.todotracker.AppDatabase
import com.pascalrieder.todotracker.model.ReminderCheck
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class NotificationActionReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_DONE = "ACTION_DONE"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_DONE) {
            val reminderId = intent.getLongExtra("reminderId", -1)
            CoroutineScope(Dispatchers.IO).launch {
                updateReminderStatus(context, true, reminderId)

                cancelNotification(context, reminderId)
            }
        }
    }

    private suspend fun updateReminderStatus(context: Context, done: Boolean, reminderId: Long) {
        val db = AppDatabase.getInstance(context)
        val reminderCheckDao = db.reminderCheckDao()


        val reminderCheck = ReminderCheck(
            done = done,
            dateTime = LocalDateTime.now(),
            reminderId = reminderId,
        )
        reminderCheckDao.create(reminderCheck)
    }

    private fun cancelNotification(context: Context, reminderId: Long) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(reminderId.toInt())
    }
}