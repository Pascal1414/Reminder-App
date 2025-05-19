package com.pascalrieder.todotracker.fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import com.pascalrieder.todotracker.AppDatabase
import com.pascalrieder.todotracker.NotificationHandler
import com.pascalrieder.todotracker.R
import com.pascalrieder.todotracker.model.Interval
import com.pascalrieder.todotracker.model.Reminder
import com.pascalrieder.todotracker.model.Weekday
import kotlinx.coroutines.launch
import java.time.LocalTime


class CreateReminderFragment : Fragment(R.layout.fragment_create_reminder) {

    companion object {
        const val TAG = "CreateReminderFragment"
    }

    private var fab: FloatingActionButton? = null
    private var errorMessageTextView: TextView? = null
    private var createButton: Button? = null
    private var nameEditText: EditText? = null
    private var descriptionEditText: EditText? = null
    private var intervalEditText: MaterialAutoCompleteTextView? = null
    private var weekdayEditTextContainer: TextInputLayout? = null
    private var weekdayEditText: MaterialAutoCompleteTextView? = null
    private var timeEditTextContainer: TextInputLayout? = null
    private var timeEditText: TextInputEditText? = null
    private var selectedTime: LocalTime? = null

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
        timeEditTextContainer = requireActivity().findViewById(R.id.reminder_time_container)
        timeEditText = requireActivity().findViewById(R.id.reminder_time)

        fab?.hide()

        timeEditText?.setOnClickListener {
            openTimePicker()
        }

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

    private fun openTimePicker() {
        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setInputMode(INPUT_MODE_CLOCK)
                .setTitleText("Select reminder time")
                .build()

        picker.show(requireActivity().supportFragmentManager, TAG)

        picker.addOnPositiveButtonClickListener {
            selectedTime = LocalTime.of(picker.hour, picker.minute)
            timeEditText?.setText(selectedTime.toString())
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

        // Get interval
        val interval =
            intervalEditText?.text.toString()

        // Get weekday
        var weekday: String? = null
        if (interval == Interval.Weekly.toString())
            weekday = weekdayEditText?.text.toString()

        if (name.isEmpty() || description.isEmpty() || interval.isEmpty() || selectedTime == null) {
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

        val reminder = Reminder(
            name = name,
            description = description,
            interval = Interval.valueOf(interval),
            weekday = weekday?.let { Weekday.valueOf(it) },
            time = selectedTime!!
        )

        val reminderId = reminderDao.create(reminder)

        val isScheduled =
            NotificationHandler().scheduleNotification(requireContext(), reminderId, reminder)

        if (!isScheduled) {
            Toast.makeText(requireContext(), "Notification was not scheduled", Toast.LENGTH_SHORT)
                .show()
        }

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