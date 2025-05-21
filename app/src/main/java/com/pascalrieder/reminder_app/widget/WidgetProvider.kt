package com.pascalrieder.reminder_app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import com.pascalrieder.reminder_app.R

class WidgetProvider : AppWidgetProvider() {
    companion object {
        const val TOGGLE_ACTION = "com.pascalrieder.TOGGLE_ACTION"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, R.layout.widget)

            val reminderId = WidgetConfigActivity.getReminderId(context, appWidgetId)
            views.setTextViewText(R.id.widget_check_reminder_text, reminderId.toString())

            setupToggleReceiver(context, views)

            // Has to be called after setting up the view
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun setupToggleReceiver(
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

        views.setOnClickPendingIntent(R.id.widget_check_reminder, pendingIntent)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == TOGGLE_ACTION) {
            // TODO: Toggle the reminder state
        }
    }
}