package com.pascalrieder.reminder_app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import com.pascalrieder.reminder_app.AppDatabase
import com.pascalrieder.reminder_app.R
import com.pascalrieder.reminder_app.dao.ReminderDao.Companion.toReminder
import com.pascalrieder.reminder_app.model.Reminder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.core.graphics.toColorInt

class WidgetProvider : AppWidgetProvider() {
    companion object {
        const val TOGGLE_ACTION = "com.pascalrieder.TOGGLE_ACTION"

        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            reminder: Reminder?
        ) {
            if (reminder == null)
                return

            val views = RemoteViews(context.packageName, R.layout.widget)

            // Update views
            views.setTextViewText(R.id.widget_check_reminder_text, reminder.id.toString())

            if (reminder.isDone())
                views.setInt(R.id.widget_reminder, "setBackgroundColor", "#2196F3".toColorInt())

            setOnClickReceiver(context, views)

            // Has to be called after setting up the view
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun setOnClickReceiver(
            context: Context,
            views: RemoteViews,
        ) {

            val intent = Intent(context, WidgetProvider::class.java).apply {
                action = TOGGLE_ACTION
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            views.setOnClickPendingIntent(R.id.widget_reminder, pendingIntent)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->

            // Get Reminder Id
            val reminderId = WidgetConfigActivity.getReminderId(context, appWidgetId)

            if (reminderId == null) {
                updateWidget(context, appWidgetManager, appWidgetId, null)
                return@forEach
            }

            // Get Reminder
            CoroutineScope(Dispatchers.IO).launch {
                val reminder =
                    AppDatabase.getInstance(context).reminderDao().getById(reminderId)?.toReminder()

                updateWidget(context, appWidgetManager, appWidgetId, reminder)
            }
        }
    }


    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == TOGGLE_ACTION) {
            // TODO: Toggle the reminder state
        }
    }
}