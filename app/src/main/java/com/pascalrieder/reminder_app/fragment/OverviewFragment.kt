package com.pascalrieder.reminder_app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pascalrieder.reminder_app.adapter.ReminderAdapter
import com.pascalrieder.reminder_app.databinding.FragmentOverviewBinding
import com.pascalrieder.reminder_app.model.Reminder
import com.pascalrieder.reminder_app.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class OverviewFragment : Fragment() {

    private var _binding: FragmentOverviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            loadReminders()

            viewModel.scheduleNotifications(requireContext())
        }
    }

    private fun displayNoReminders() {
        binding.recyclerView.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }

    private fun loadReminders() {

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ReminderAdapter(::onDeleteClick, ::onDoneClick)

        binding.recyclerView.adapter = adapter

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}