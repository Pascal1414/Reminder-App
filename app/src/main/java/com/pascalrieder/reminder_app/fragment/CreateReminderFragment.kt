package com.pascalrieder.reminder_app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import com.pascalrieder.reminder_app.R
import com.pascalrieder.reminder_app.databinding.FragmentCreateReminderBinding
import com.pascalrieder.reminder_app.model.Interval
import com.pascalrieder.reminder_app.model.Reminder
import com.pascalrieder.reminder_app.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime


class CreateReminderFragment : Fragment() {

    companion object {
        const val TAG = "CreateReminderFragment"
    }

    private var fab: FloatingActionButton? = null
    private var selectedTime: LocalTime? = null

    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentCreateReminderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateReminderBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab = requireActivity().findViewById(R.id.floating_action_button)
        fab?.hide()

        binding.editTextTime.setOnClickListener {
            openTimePicker()
        }

        binding.textViewInterval.setSimpleItems(Interval.getStringValues().toTypedArray())
        binding.textViewInterval.doOnTextChanged { text, _, _, _ ->
            if (text.toString() == Interval.Daily.toString())
                hideWeekdayMenu()
            else
                showWeekdayMenu()
        }
        binding.textViewWeekday.setSimpleItems(DayOfWeek.entries.map { it.name }.toTypedArray())

        binding.buttonCreate.setOnClickListener {
            onCreateClick()
        }
    }

    private fun showWeekdayMenu() {
        binding.textInputLayoutWeekday.visibility = View.VISIBLE
    }

    private fun hideWeekdayMenu() {
        binding.textInputLayoutWeekday.visibility = View.GONE
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
            binding.editTextTime.setText(selectedTime.toString())
        }
    }

    private fun onCreateClick() {
        hideError()
        // Get name
        val name = binding.editTextName.text.toString()

        // Get description
        val description = binding.editTextDescription.text.toString()

        // Get interval
        val interval = binding.textViewInterval.text.toString()

        // Get weekday
        var weekday: String? = null
        if (interval == Interval.Weekly.toString())
            weekday = binding.textViewWeekday.text.toString()

        val errorMessage = if (name.isEmpty()) {
            "Please enter a name"
        } else if (interval.isEmpty()) {
            "Please select an interval"
        } else if (selectedTime == null) {
            "Please select a time"
        } else {
            null
        }

        if (errorMessage != null) {
            showError(errorMessage)
            return
        }

        if (interval == Interval.Weekly.toString()) {
            if (weekday.isNullOrEmpty()) {
                showError("Please select a weekday")
                return
            }
        }

        val reminder = Reminder(
            name = name,
            description = if (description.isEmpty()) null else description,
            interval = Interval.valueOf(interval),
            weekday = weekday?.let { DayOfWeek.valueOf(it) },
            time = selectedTime!!
        )

        requireActivity().lifecycleScope.launch {

            viewModel.createReminder(requireContext(), reminder)

            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun showError(message: String) {
        binding.textViewError.text = message
    }

    private fun hideError() {
        binding.textViewError.text = ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fab?.show()
        _binding = null
    }
}