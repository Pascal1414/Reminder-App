package com.pascalrieder.reminder_app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.RemoteViews
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt
import com.pascalrieder.reminder_app.AppDatabase
import com.pascalrieder.reminder_app.R
import com.pascalrieder.reminder_app.dao.ReminderDao.Companion.toReminder
import com.pascalrieder.reminder_app.model.Reminder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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
                views.setInt(R.id.widget_reminder, "setBackgroundColor", colors.colorSurfaceVariant)
            }

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

    class WidgetColors(
        val colorSurface: Int,
        val colorOnSurface: Int,
        val colorSurfaceVariant: Int
    ) {
        companion object {
            fun loadFromPreferences(context: Context): WidgetColors {
                val prefs = context.getSharedPreferences(
                    WidgetConfigActivity.WIDGET_PREFS,
                    Context.MODE_PRIVATE
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