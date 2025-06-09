package com.pascalrieder.reminder_app.viewmodel

import android.appwidget.AppWidgetManager
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.pascalrieder.reminder_app.NotificationHandler
import com.pascalrieder.reminder_app.adapter.ReminderAdapter
import com.pascalrieder.reminder_app.model.Reminder
import com.pascalrieder.reminder_app.model.ReminderCheck
import com.pascalrieder.reminder_app.repository.ReminderCheckRepository
import com.pascalrieder.reminder_app.repository.ReminderRepository
import com.pascalrieder.reminder_app.widget.WidgetConfigActivity
import com.pascalrieder.reminder_app.widget.WidgetProvider.Companion.updateWidget
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.text.get

class MainViewModel(
    private val reminderRepository: ReminderRepository,
    private val reminderCheckRepository: ReminderCheckRepository

) : ViewModel() {

    private val _reminders = MutableLiveData<List<Reminder>>(emptyList())
    val reminders: LiveData<List<Reminder>> = _reminders

    /**
     * Creates a new reminder in the database and schedules a notification for it.
     */
    fun createReminder(context: Context, reminder: Reminder) = viewModelScope.launch {
        val reminder = reminderRepository.create(reminder)

        if (reminder == null) {
            Toast.makeText(
                context,
                "Failed to create reminder",
                Toast.LENGTH_SHORT
            ).show()
            return@launch
        }

        val reminders = _reminders.value?.toMutableList() ?: mutableListOf()
        reminders.add(reminder)
        _reminders.value = reminders

        val isScheduled =
            NotificationHandler().scheduleNotification(context, reminder.id, reminder)

        if (!isScheduled) {
            Toast.makeText(
                context,
                "Notification was not scheduled",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun getReminders() = viewModelScope.launch {
        _reminders.value = reminderRepository.getAll().toMutableList()
    }

    fun deleteReminder(context: Context, reminder: Reminder) = viewModelScope.launch {
        reminderRepository.delete(reminder)

        NotificationHandler().cancelNotification(context, reminder.id, reminder.name)

        // Remove the reminder from the live data list
        val reminders = _reminders.value?.toMutableList() ?: mutableListOf()
        reminders.remove(reminder)

        _reminders.value = reminders

    }

    fun updateReminderStatus(done: Boolean, reminder: Reminder) = viewModelScope.launch {
        val reminderCheck = ReminderCheck(
            done = done,
            dateTime = LocalDateTime.now(),
            reminderId = reminder.id,
        )
        reminderCheckRepository.create(reminderCheck)

        _reminders.value.first { it.id == reminder.id }.reminderChecks.add(reminderCheck)
    }

    fun notifyReminderWidgets(context: Context, reminder: Reminder) {
        WidgetConfigActivity.getWidgetIds(context, reminder.id)
            .forEach { appWidgetId ->
                updateWidget(
                    context,
                    AppWidgetManager.getInstance(context),
                    appWidgetId,
                    reminder
                )
            }
    }

    fun scheduleNotifications(context: Context) = viewModelScope.launch {
        reminders.value.forEach { reminder ->
            NotificationHandler().scheduleNotification(context, reminder.id, reminder)
        }
    }
}