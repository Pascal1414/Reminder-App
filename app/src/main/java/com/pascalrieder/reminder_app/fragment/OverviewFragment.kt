package com.pascalrieder.reminder_app.fragment

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pascalrieder.reminder_app.AppDatabase
import com.pascalrieder.reminder_app.NotificationHandler
import com.pascalrieder.reminder_app.R
import com.pascalrieder.reminder_app.adapter.ReminderAdapter
import com.pascalrieder.reminder_app.dao.ReminderCheckDao
import com.pascalrieder.reminder_app.dao.ReminderDao
import com.pascalrieder.reminder_app.dao.ReminderDao.Companion.toReminders
import com.pascalrieder.reminder_app.model.Reminder
import com.pascalrieder.reminder_app.model.ReminderCheck
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class OverviewFragment : Fragment(R.layout.fragment_overview) {

    private var recyclerView: RecyclerView? = null
    private var noRemindersLinearLayout: LinearLayout? = null

    private lateinit var db: AppDatabase
    private lateinit var reminderDao: ReminderDao
    private lateinit var reminderCheckDao: ReminderCheckDao

    private var reminders = mutableListOf<Reminder>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the Views
        recyclerView = view.findViewById(R.id.reminderRecyclerView)
        noRemindersLinearLayout = view.findViewById(R.id.no_reminders)

        // Initialize the database and DAOs
        db = AppDatabase.getInstance(requireContext()).also {
            reminderDao = it.reminderDao()
            reminderCheckDao = it.reminderCheckDao()
        }

        lifecycleScope.launch {
            loadReminders()

            scheduleNotifications()
        }
    }

    private fun updateNoRemindersVisibility() {
        if (reminders.isEmpty()) {
            noRemindersLinearLayout?.visibility = View.VISIBLE
            recyclerView?.visibility = View.GONE
        }
    }

    private suspend fun loadReminders() {
        reminders = reminderDao.getAll().toReminders().toMutableList()

        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        recyclerView?.adapter = ReminderAdapter(reminders, ::onDeleteClick, ::onDoneClick)

        updateNoRemindersVisibility()
    }

    private suspend fun scheduleNotifications() {
        reminders.forEach { reminder ->
            NotificationHandler().scheduleNotification(requireContext(), reminder.id, reminder)
        }
    }

    private fun onDeleteClick(reminder: Reminder) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Are you sure?")
            .setMessage("Do you want to delete the reminder?")
            .setPositiveButton("Delete") { _, _ ->
                deleteReminder(reminder)
            }
            .setNegativeButton("Cancel") { _, _ ->
            }
            .show()
    }

    private fun onDoneClick(reminder: Reminder) {
        updateReminderStatus(!reminder.isDone(), reminder)
    }

    private fun updateReminderStatus(done: Boolean, reminder: Reminder) = lifecycleScope.launch {
        val reminderCheck = ReminderCheck(
            done = done,
            dateTime = LocalDateTime.now(),
            reminderId = reminder.id,
        )
        reminderCheckDao.create(reminderCheck)

        val index = reminders.indexOf(reminder)
        if (index != -1) {
            reminders[index].reminderChecks.add(reminderCheck)
            recyclerView?.adapter?.notifyItemChanged(index, ReminderAdapter.ANIMATE_STATUS_CHANGE)
        }
    }

    private fun deleteReminder(reminder: Reminder) = lifecycleScope.launch {
        reminderDao.delete(reminder)

        NotificationHandler().cancelNotification(requireContext(), reminder.id, reminder.name)

        val index = reminders.indexOf(reminder)
        if (index != -1) {
            reminders.removeAt(index)
            recyclerView?.adapter?.notifyItemRemoved(index)
        }

        updateNoRemindersVisibility()
    }
}