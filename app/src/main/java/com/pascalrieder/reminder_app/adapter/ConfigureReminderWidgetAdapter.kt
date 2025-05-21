package com.pascalrieder.reminder_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pascalrieder.reminder_app.R
import com.pascalrieder.reminder_app.model.Reminder


class ConfigureReminderWidgetAdapter(
    private val dataSet: MutableList<Reminder>,
    private val onClick: (Reminder) -> Unit,
) : RecyclerView.Adapter<ConfigureReminderWidgetAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewName: TextView = view.findViewById(R.id.text_view_name)
        val textViewInterval: TextView = view.findViewById(R.id.text_view_interval)
        val textViewDescription: TextView = view.findViewById(R.id.text_view_description)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.widget_config_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val reminder = dataSet[position]

        viewHolder.textViewName.text = reminder.name

        if (reminder.description.isNullOrEmpty())
            viewHolder.textViewDescription.visibility = View.GONE
        else
            viewHolder.textViewDescription.text = reminder.description

        viewHolder.textViewInterval.text = reminder.getFormattedInterval()

        viewHolder.itemView.setOnClickListener { onClick(reminder) }
    }

    override fun getItemCount() = dataSet.size
}