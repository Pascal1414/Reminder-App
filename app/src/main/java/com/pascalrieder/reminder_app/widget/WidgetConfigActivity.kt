package com.pascalrieder.reminder_app.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.DynamicColors
import com.pascalrieder.reminder_app.AppDatabase
import com.pascalrieder.reminder_app.R
import com.pascalrieder.reminder_app.adapter.ConfigureReminderWidgetAdapter
import com.pascalrieder.reminder_app.model.Reminder
import com.pascalrieder.reminder_app.repository.ReminderRepository
import kotlinx.coroutines.launch


class WidgetConfigActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var reminderRepository: ReminderRepository

    private var reminders = mutableListOf<Reminder>()

    private var recyclerView: RecyclerView? = null
    private var noRemindersLinearLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        saveThemeColorsForWidget()

        setContentView(R.layout.activity_widget_config)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.widget_config)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recycler_view)
        noRemindersLinearLayout = findViewById(R.id.no_reminders_container)

        db = AppDatabase.getInstance(this).also {
            reminderRepository = ReminderRepository(it.reminderDao())
        }

        loadReminders()
    }

    private fun loadReminders() = lifecycleScope.launch {
        reminders = reminderRepository.getAll().toMutableList()

        recyclerView?.layoutManager = LinearLayoutManager(this@WidgetConfigActivity)
        recyclerView?.adapter = ConfigureReminderWidgetAdapter(reminders, ::onReminderClick)

        updateNoRemindersVisibility()
    }

    private fun onReminderClick(reminder: Reminder) {
        val appWidgetId = intent.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        if (appWidgetId == null || appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Toast.makeText(this, "Failed to get widget ID", Toast.LENGTH_SHORT).show()
        } else {
            setReminderId(this, appWidgetId, reminder.id)
            WidgetProvider.updateWidget(
                this,
                AppWidgetManager.getInstance(this),
                appWidgetId,
                reminder
            )
        }

        finish()
    }

    private fun saveThemeColorsForWidget() {
        val prefs = getSharedPreferences(WIDGET_PREFS, MODE_PRIVATE)

        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorSurface, typedValue, true)
        val colorSurface = typedValue.data

        theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true)
        val colorOnSurface = typedValue.data

        theme.resolveAttribute(
            com.google.android.material.R.attr.colorSurfaceVariant,
            typedValue,
            true
        )
        val colorSurfaceVariant = typedValue.data

        prefs.edit(commit = true) {
            putInt("colorSurface", colorSurface)
            putInt("colorOnSurface", colorOnSurface)
            putInt("colorSurfaceVariant", colorSurfaceVariant)
        }
    }

    private fun updateNoRemindersVisibility() {
        if (reminders.isEmpty()) {
            noRemindersLinearLayout?.visibility = View.VISIBLE
            recyclerView?.visibility = View.GONE
        }
    }

    companion object {

        const val WIDGET_PREFS = "WidgetPreferences"

        fun setReminderId(context: Context, widgetId: Int, reminderId: Long) {
            val prefs = context.getSharedPreferences(WIDGET_PREFS, MODE_PRIVATE)
            prefs.edit { putLong("widget_$widgetId", reminderId) }
        }

        fun getReminderId(context: Context, widgetId: Int): Long? {
            val prefs = context.getSharedPreferences(WIDGET_PREFS, MODE_PRIVATE)
            val reminderId = prefs.getLong("widget_$widgetId", -1)
            return if (reminderId == -1L) null else reminderId
        }

        fun getWidgetIds(context: Context, reminderId: Long): List<Int> {
            val prefs = context.getSharedPreferences(WIDGET_PREFS, MODE_PRIVATE)
            val keys = prefs.all.filter { it.value == reminderId }.keys
            return keys.mapNotNull { key ->
                key.removePrefix("widget_").toIntOrNull()
            }
        }
    }
}