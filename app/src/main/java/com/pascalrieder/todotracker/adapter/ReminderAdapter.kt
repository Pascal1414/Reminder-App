package com.pascalrieder.todotracker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pascalrieder.todotracker.R
import com.pascalrieder.todotracker.model.Reminder

class ReminderAdapter(
    private val dataSet: MutableList<Reminder>,
    private val onDeleteClick: (Reminder) -> Unit,
    private val onDoneClick: (Reminder) -> Unit
) : RecyclerView.Adapter<ReminderAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewName: TextView = view.findViewById(R.id.reminderName)
        val textViewInterval: TextView = view.findViewById(R.id.reminderInterval)
        val textViewDescription: TextView = view.findViewById(R.id.reminderDescription)
        val buttonDone: TextView = view.findViewById(R.id.reminderDone)
        val buttonDelete: TextView = view.findViewById(R.id.reminderDelete)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.reminder_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val reminder = dataSet[position]

        viewHolder.buttonDone.setOnClickListener {
            onDoneClick(reminder)
        }
        viewHolder.buttonDelete.setOnClickListener {
            onDeleteClick(reminder)
        }
        viewHolder.textViewName.text = reminder.name
        viewHolder.textViewDescription.text = reminder.description

        viewHolder.textViewInterval.text = if (reminder.weekday == null)
            "${reminder.interval} at ${reminder.time}"
        else
            "Every ${reminder.weekday} at ${reminder.time}"
    }

    override fun getItemCount() = dataSet.size
}