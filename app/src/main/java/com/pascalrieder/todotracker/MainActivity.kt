package com.pascalrieder.todotracker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.android.material.color.DynamicColors
import com.pascalrieder.todotracker.dao.ReminderDao.Companion.toReminders
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

        lifecycleScope.launch {
            val db = AppDatabase.getInstance(applicationContext)

            val reminderDao = db.reminderDao()
            val reminderCheckDao = db.reminderCheckDao()


            // Create Reminder entity properly
            val id = reminderDao.create(
                Reminder(
                    name = "Test Reminder",
                    description = "Test Description"
                )
            )
            reminderCheckDao.create(
                ReminderCheck(
                    done = true,
                    dateTime = LocalDateTime.now(),
                    reminderId = id
                )
            )

            val reminders = reminderDao.getAll().toReminders()
            for (reminder in reminders) {
                println("Reminder: ${reminder.name}, ${reminder.description}")
            }
        }
    }
}