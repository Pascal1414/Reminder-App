package com.pascalrieder.todotracker.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pascalrieder.todotracker.AppDatabase
import com.pascalrieder.todotracker.R
import com.pascalrieder.todotracker.model.Reminder
import kotlinx.coroutines.launch


class CreateReminderFragment : Fragment(R.layout.fragment_create_reminder) {
    private var fab: FloatingActionButton? = null
    private var errorMessageTextView: TextView? = null
    private var createButton: Button? = null
    private var nameEditText: EditText? = null
    private var descriptionEditText: EditText? = null

    private lateinit var db: AppDatabase

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getInstance(requireContext())

        fab = requireActivity().findViewById(R.id.floating_action_button)
        errorMessageTextView = requireActivity().findViewById(R.id.error_message)
        createButton = requireActivity().findViewById(R.id.create_reminder_button)
        nameEditText = requireActivity().findViewById(R.id.reminder_name)
        descriptionEditText = requireActivity().findViewById(R.id.reminder_description)

        fab?.hide()

        val createButton = requireActivity().findViewById<Button>(R.id.create_reminder_button)
        createButton.setOnClickListener {
            onCreateClick()
        }
    }

    private fun onCreateClick() = requireActivity().lifecycleScope.launch {
        hideError()
        // Get name
        val name =
            nameEditText?.text.toString()

        // Get description
        val description =
            descriptionEditText?.text.toString()

        if (name.isEmpty() || description.isEmpty()) {
            showError("Please fill in all fields")
            return@launch
        }

        val reminderDao = db.reminderDao()
        reminderDao.create(Reminder(name = name, description = description))

        requireActivity().supportFragmentManager.popBackStack()

    }

    private fun showError(message: String) {
        errorMessageTextView?.text = message
    }

    private fun hideError() {
        errorMessageTextView?.text = ""
    }

    override fun onDestroy() {
        super.onDestroy()
        fab?.show()
    }
}