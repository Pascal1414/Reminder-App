package com.pascalrieder.reminder_app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.createBitmap
import com.pascalrieder.reminder_app.AppDatabase
import com.pascalrieder.reminder_app.R
import com.pascalrieder.reminder_app.model.Reminder
import com.pascalrieder.reminder_app.model.ReminderCheck
import com.pascalrieder.reminder_app.repository.ReminderCheckRepository
import com.pascalrieder.reminder_app.repository.ReminderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime


class WidgetProvider : AppWidgetProvider() {
    companion object {
        const val TOGGLE_ACTION = "TOGGLE_ACTION"
        const val EXTRA_APPWIDGET_ID = "EXTRA_APPWIDGET_ID"

        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            reminder: Reminder?,
            isReminderDeleted: Boolean = false
        ) {
            if (reminder == null)
                return


            if (isReminderDeleted) {
                removeWidgetsReminderId(context, appWidgetId)

                // Apply initial layout
                appWidgetManager.updateAppWidget(appWidgetId, RemoteViews(context.packageName, R.layout.widget_initial_layout))
            } else {
                // Set content
                val views = RemoteViews(context.packageName, R.layout.widget)

                val colors = WidgetColors.loadFromPreferences(context)

                val lines: List<Bitmap> = reminder.name.split(" ").mapNotNull { word ->
                    if (word.isNotEmpty())
                        createBitmapWithCustomFont(context, word, colors.colorOnSurface)
                    else
                        null
                }
                val bitmap = combineLineBitmapsVertically(lines)

                views.setImageViewBitmap(R.id.widget_image_view_name, bitmap)

                if (reminder.isDone()) {
                    views.setInt(R.id.widget_reminder, "setBackgroundColor", colors.colorSurface)
                } else {
                    views.setInt(
                        R.id.widget_reminder,
                        "setBackgroundColor",
                        colors.colorSurfaceVariant
                    )
                }

                setOnClickReceiver(context, views, appWidgetId)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }

        private fun setOnClickReceiver(
            context: Context,
            views: RemoteViews,
            appWidgetId: Int
        ) {

            val intent = Intent(context, WidgetProvider::class.java).apply {
                action = TOGGLE_ACTION
                putExtra(EXTRA_APPWIDGET_ID, appWidgetId)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            views.setOnClickPendingIntent(R.id.widget_reminder, pendingIntent)
        }

        fun createBitmapWithCustomFont(context: Context, text: String, textColor: Int): Bitmap {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.textSize = 100f // A large number so that the text fills the bitmap
            paint.color = textColor
            paint.typeface = ResourcesCompat.getFont(context, R.font.doto)

            val width = paint.measureText(text).toInt()
            val height = (paint.fontMetrics.bottom - paint.fontMetrics.top).toInt()

            val bitmap = createBitmap(width, height)
            val canvas = Canvas(bitmap)
            canvas.drawText(text, 0f, -paint.fontMetrics.ascent, paint)

            return bitmap
        }

        fun combineLineBitmapsVertically(lines: List<Bitmap>): Bitmap {
            if (lines.isEmpty()) throw IllegalArgumentException("No bitmaps to combine")

            val width = lines.maxOf { it.width }
            val totalHeight = lines.sumOf { it.height }

            val result = createBitmap(width, totalHeight)
            val canvas = Canvas(result)

            var yOffset = 0
            for (line in lines) {
                canvas.drawBitmap(line, 0f, yOffset.toFloat(), null)
                yOffset += line.height
            }

            return result
        }


        const val WIDGET_PREFS = "WidgetPreferences"

        fun setWidgetsReminderId(context: Context, widgetId: Int, reminderId: Long) {
            val prefs = context.getSharedPreferences(WIDGET_PREFS, MODE_PRIVATE)
            prefs.edit { putLong("widget_$widgetId", reminderId) }
        }

        fun removeWidgetsReminderId(context: Context, widgetId: Int) {
            val prefs = context.getSharedPreferences(WIDGET_PREFS, MODE_PRIVATE)
            prefs.edit { remove("widget_$widgetId") }
        }

        fun getWidgetsReminderId(context: Context, widgetId: Int): Long? {
            val prefs = context.getSharedPreferences(WIDGET_PREFS, MODE_PRIVATE)
            val reminderId = prefs.getLong("widget_$widgetId", -1)
            return if (reminderId == -1L) null else reminderId
        }

        fun getWidgetIdsForReminder(context: Context, reminderId: Long): List<Int> {
            val prefs = context.getSharedPreferences(WIDGET_PREFS, MODE_PRIVATE)
            val keys = prefs.all.filter { it.value == reminderId }.keys
            return keys.mapNotNull { key ->
                key.removePrefix("widget_").toIntOrNull()
            }
        }

    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->

            // Get Reminder Id
            val reminderId = getWidgetsReminderId(context, appWidgetId)

            if (reminderId == null) {
                updateWidget(context, appWidgetManager, appWidgetId, null)
                return@forEach
            }

            // Get Reminder
            CoroutineScope(Dispatchers.IO).launch {
                val repository = ReminderRepository(AppDatabase.getInstance(context).reminderDao())
                val reminder = repository.getById(reminderId)

                updateWidget(context, appWidgetManager, appWidgetId, reminder)
            }
        }
    }


    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == TOGGLE_ACTION) {
            // TODO: Toggle the reminder state
            val appWidgetId = intent.getIntExtra(EXTRA_APPWIDGET_ID, -1)
            if (appWidgetId == -1) return

            // Get Reminder Id
            val reminderId = getWidgetsReminderId(context, appWidgetId)

            if (reminderId == null)
                return


            CoroutineScope(Dispatchers.IO).launch {
                val appDatabase = AppDatabase.getInstance(context)
                val reminderRepository = ReminderRepository(appDatabase.reminderDao())
                val reminderCheckRepository =
                    ReminderCheckRepository(appDatabase.reminderCheckDao())
                val reminder = reminderRepository.getById(reminderId)

                if (reminder == null) {
                    return@launch
                }

                val reminderCheck = ReminderCheck(
                    done = !reminder.isDone(),
                    dateTime = LocalDateTime.now(),
                    reminderId = reminder.id,
                )

                reminderCheckRepository.create(reminderCheck)

                reminder.reminderChecks.add(reminderCheck)

                updateWidget(context, AppWidgetManager.getInstance(context), appWidgetId, reminder)
            }
        }
    }

    class WidgetColors(
        val colorSurface: Int,
        val colorOnSurface: Int,
        val colorSurfaceVariant: Int
    ) {
        companion object {
            fun loadFromPreferences(context: Context): WidgetColors {
                val prefs = context.getSharedPreferences(
                    WIDGET_PREFS,
                    MODE_PRIVATE
                )
                return WidgetColors(
                    colorSurface = prefs.getInt("colorSurface", Color.BLACK),
                    colorOnSurface = prefs.getInt("colorOnSurface", Color.WHITE),
                    colorSurfaceVariant = prefs.getInt("colorSurfaceVariant", Color.GRAY)
                )
            }
        }
    }
}