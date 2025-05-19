package com.pascalrieder.todotracker.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pascalrieder.todotracker.AppDatabase
import com.pascalrieder.todotracker.R
import com.pascalrieder.todotracker.adapter.ReminderAdapter
import com.pascalrieder.todotracker.dao.ReminderDao
import com.pascalrieder.todotracker.dao.ReminderDao.Companion.toReminders
import com.pascalrieder.todotracker.model.Reminder
import kotlinx.coroutines.launch

class OverviewFragment : Fragment(R.layout.fragment_overview) {

    private var recyclerView: RecyclerView? = null

    private lateinit var reminderDao: ReminderDao

    private var reminders = mutableListOf<Reminder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.reminderRecyclerView)

        val db = AppDatabase.getInstance(requireContext())
        reminderDao = db.reminderDao()

        lifecycleScope.launch {
            reminders = reminderDao.getAll().toReminders().toMutableList()

            recyclerView?.layoutManager = LinearLayoutManager(requireContext())
            recyclerView?.adapter = ReminderAdapter(reminders.toTypedArray())
        }
    }
}