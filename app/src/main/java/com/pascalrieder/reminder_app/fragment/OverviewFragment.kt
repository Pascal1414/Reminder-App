package com.pascalrieder.reminder_app.fragment

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pascalrieder.reminder_app.R
import com.pascalrieder.reminder_app.adapter.ReminderAdapter
import com.pascalrieder.reminder_app.model.Reminder
import com.pascalrieder.reminder_app.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class OverviewFragment : Fragment(R.layout.fragment_overview) {

    private var recyclerView: RecyclerView? = null
    private var noRemindersLinearLayout: LinearLayout? = null

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the Views
        recyclerView = view.findViewById(R.id.reminderRecyclerView)
        noRemindersLinearLayout = view.findViewById(R.id.no_reminders)

        lifecycleScope.launch {
            loadReminders()

            viewModel.scheduleNotifications(requireContext())
        }
    }

    private fun displayNoReminders() {
        noRemindersLinearLayout?.visibility = View.VISIBLE
        recyclerView?.visibility = View.GONE
    }

    private fun loadReminders() {

        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ReminderAdapter(::onDeleteClick, ::onDoneClick)

        recyclerView?.adapter = adapter

        viewModel.reminders.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)

            if (list.isEmpty())
                displayNoReminders()
        }
    }

    private fun onDeleteClick(reminder: Reminder) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Are you sure?")
            .setMessage("Do you want to delete the reminder?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteReminder(requireContext(), reminder)
            }
            .setNegativeButton("Cancel") { _, _ ->
            }
            .show()
    }

    private fun onDoneClick(reminder: Reminder) {
        viewModel.updateReminderStatus(!reminder.isDone(), reminder)
    }
}