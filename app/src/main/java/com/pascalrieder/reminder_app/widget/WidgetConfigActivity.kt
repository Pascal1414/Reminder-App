package com.pascalrieder.reminder_app.widget

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.DynamicColors
import com.pascalrieder.reminder_app.AppDatabase
import com.pascalrieder.reminder_app.R
import com.pascalrieder.reminder_app.adapter.ConfigureReminderWidgetAdapter
import com.pascalrieder.reminder_app.dao.ReminderDao
import com.pascalrieder.reminder_app.dao.ReminderDao.Companion.toReminders
import com.pascalrieder.reminder_app.model.Reminder
import kotlinx.coroutines.launch

class WidgetConfigActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var reminderDao: ReminderDao

    private var reminders = mutableListOf<Reminder>()

    private var recyclerView: RecyclerView? = null
    private var noRemindersLinearLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_widget_config)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.widget_config)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        DynamicColors.applyToActivitiesIfAvailable(application)

        recyclerView = findViewById(R.id.recycler_view)
        noRemindersLinearLayout = findViewById(R.id.no_reminders_container)

        db = AppDatabase.getInstance(this).also {
            reminderDao = it.reminderDao()
        }

        loadReminders()
    }

    private fun loadReminders() = lifecycleScope.launch {
        reminders = reminderDao.getAll().toReminders().toMutableList()

        recyclerView?.layoutManager = LinearLayoutManager(this@WidgetConfigActivity)
        recyclerView?.adapter = ConfigureReminderWidgetAdapter(reminders, ::onReminderClick)

        updateNoRemindersVisibility()
    }

    private fun onReminderClick(reminder: Reminder) {
        Log.i("WidgetConfigActivity", "Reminder clicked: ${reminder.id}")
    }

    private fun updateNoRemindersVisibility() {
        if (reminders.isEmpty()) {
            noRemindersLinearLayout?.visibility = View.VISIBLE
            recyclerView?.visibility = View.GONE
        }
    }
}