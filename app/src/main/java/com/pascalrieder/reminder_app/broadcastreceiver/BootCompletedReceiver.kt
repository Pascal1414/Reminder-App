package com.pascalrieder.reminder_app.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.pascalrieder.reminder_app.AppDatabase
import com.pascalrieder.reminder_app.NotificationHandler
import com.pascalrieder.reminder_app.dao.ReminderDao.Companion.toReminders
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            Log.i("BootCompletedReceiver", "Boot completed, rescheduling notifications")
            // Reschedule all reminders
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getInstance(context)
                val reminders = db.reminderDao().getAll().toReminders()

                reminders.forEach { reminder ->
                    NotificationHandler().scheduleNotification(
                        context,
                        reminder.id,
                        reminder
                    )
                }
            }
        }
    }
}