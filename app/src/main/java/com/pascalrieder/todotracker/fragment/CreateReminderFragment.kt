package com.pascalrieder.todotracker.fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.pascalrieder.todotracker.AppDatabase
import com.pascalrieder.todotracker.R
import com.pascalrieder.todotracker.model.Interval
import com.pascalrieder.todotracker.model.Reminder
import com.pascalrieder.todotracker.model.Weekday
import kotlinx.coroutines.launch


class CreateReminderFragment : Fragment(R.layout.fragment_create_reminder) {
    private var fab: FloatingActionButton? = null
    private var errorMessageTextView: TextView? = null
    private var createButton: Button? = null
    private var nameEditText: EditText? = null
    private var descriptionEditText: EditText? = null
    private var intervalEditText: MaterialAutoCompleteTextView? = null
    private var weekdayEditTextContainer: TextInputLayout? = null
    private var weekdayEditText: MaterialAutoCompleteTextView? = null

    private lateinit var db: AppDatabase

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getInstance(requireContext())

        fab = requireActivity().findViewById(R.id.floating_action_button)
        errorMessageTextView = requireActivity().findViewById(R.id.error_message)
        createButton = requireActivity().findViewById(R.id.create_reminder_button)
        nameEditText = requireActivity().findViewById(R.id.reminder_name)
        descriptionEditText = requireActivity().findViewById(R.id.reminder_description)
        intervalEditText = requireActivity().findViewById(R.id.reminder_interval)
        weekdayEditTextContainer = requireActivity().findViewById(R.id.reminder_weekday_container)
        weekdayEditText = requireActivity().findViewById(R.id.reminder_weekday)

        fab?.hide()

        intervalEditText?.setSimpleItems(Interval.getStringValues().toTypedArray())
        intervalEditText?.doOnTextChanged { text, _, _, _ ->
            if (text.toString() == Interval.Daily.toString())
                hideWeekdayMenu()
            else
                showWeekdayMenu()
        }
        weekdayEditText?.setSimpleItems(Weekday.getStringValues().toTypedArray())

        val createButton = requireActivity().findViewById<Button>(R.id.create_reminder_button)
        createButton.setOnClickListener {
            onCreateClick()
        }
    }

    private fun showWeekdayMenu() {
        weekdayEditTextContainer?.visibility = View.VISIBLE
    }

    private fun hideWeekdayMenu() {
        weekdayEditTextContainer?.visibility = View.GONE
    }

    private fun onCreateClick() = requireActivity().lifecycleScope.launch {
        hideError()
        // Get name
        val name =
            nameEditText?.text.toString()

        // Get description
        val description =
            descriptionEditText?.text.toString()

        // Get interval
        val interval =
            intervalEditText?.text.toString()

        // Get weekday
        var weekday: String? = null
        if (interval == Interval.Weekly.toString())
            weekday = weekdayEditText?.text.toString()

        if (name.isEmpty() || description.isEmpty() || interval.isEmpty()) {
            showError("Please fill in all fields")
            return@launch
        }

        if (interval == Interval.Weekly.toString()) {
            if (weekday.isNullOrEmpty()) {
                showError("Please select a weekday")
                return@launch
            }
        }

        val reminderDao = db.reminderDao()
        reminderDao.create(
            Reminder(
                name = name,
                description = description,
                interval = Interval.valueOf(interval),
                weekday = weekday?.let { Weekday.valueOf(it) }
            )
        )

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