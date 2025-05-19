package com.pascalrieder.todotracker.broadcastreceiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.pascalrieder.todotracker.MainActivity
import com.pascalrieder.todotracker.R


class NotificationReceiver : BroadcastReceiver() {
    companion object {
        const val CHANNEL_ID = "Reminder Channel"
    }

    override fun onReceive(context: Context, intent: Intent) {

        val notificationId = intent.getIntExtra("reminderId", -1)

        val intentMainActivity = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_IMMUTABLE)

        var notification =
            NotificationCompat.Builder(context, CHANNEL_ID).setContentTitle("Reminder")
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentText("Much longer text that cannot fit one line...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(pendingIntent)
                .build()

        val channel = getChannel(context)

        val notificationManager = getSystemService(context, NotificationManager::class.java)!!
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(notificationId, notification)
    }

    private fun getChannel(context: Context): NotificationChannel {
        val name = "Reminder channel"
        val description = "Channel for reminder notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(CHANNEL_ID, name, importance)

        channel.description = description

        return channel
    }
}