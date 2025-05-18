package com.pascalrieder.todotracker

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.android.material.color.DynamicColors
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pascalrieder.todotracker.dao.ReminderDao.Companion.toReminders
import com.pascalrieder.todotracker.fragment.CreateReminderFragment
import com.pascalrieder.todotracker.fragment.OverviewFragment
import com.pascalrieder.todotracker.model.Reminder
import com.pascalrieder.todotracker.model.ReminderCheck
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        DynamicColors.applyToActivitiesIfAvailable(application)

        val fab = findViewById<FloatingActionButton>(R.id.floating_action_button)
        fab.setOnClickListener(::onFabClick)

        navigateToOverview()

        lifecycleScope.launch {
            val db = AppDatabase.getInstance(applicationContext)

            val reminderDao = db.reminderDao()

            val reminders = reminderDao.getAll().toReminders()
            for (reminder in reminders) {
                println("Reminder: ${reminder.name}, ${reminder.description}")
            }
        }
    }

    fun onFabClick(view: View) {
        navigateToCreateReminder()
    }

    private fun navigateToCreateReminder() {
        val reminderFragment = CreateReminderFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, reminderFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToOverview() {
        val overviewFragment = OverviewFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, overviewFragment)
            .commit()
    }
}