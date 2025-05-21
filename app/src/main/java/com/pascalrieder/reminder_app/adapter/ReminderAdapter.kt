package com.pascalrieder.reminder_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.pascalrieder.reminder_app.R
import com.pascalrieder.reminder_app.model.Reminder

class ReminderAdapter(
    private val dataSet: MutableList<Reminder>,
    private val onDeleteClick: (Reminder) -> Unit,
    private val onDoneClick: (Reminder) -> Unit
) : RecyclerView.Adapter<ReminderAdapter.ViewHolder>() {

    companion object {
        const val ANIMATE_STATUS_CHANGE = "animate status change"
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewName: TextView = view.findViewById(R.id.reminderName)
        val textViewInterval: TextView = view.findViewById(R.id.reminderInterval)
        val textViewDescription: TextView = view.findViewById(R.id.reminderDescription)
        val buttonDone: MaterialButton = view.findViewById(R.id.reminderDone)
        val buttonDelete: MaterialButton = view.findViewById(R.id.reminderDelete)

        fun animateDoneChanged() {
            buttonDone.animate()
                .setDuration(300)
                .start()
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.reminder_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
        payloads: List<Any?>
    ) {
        val reminder = dataSet[position]

        if (payloads.isNotEmpty()) {
            val payload = payloads[0]
            if (payload == ANIMATE_STATUS_CHANGE) {
                if (reminder.isDone()) {
                    holder.buttonDone.text = "Mark undone"
                    holder.buttonDone.icon =
                        ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_check)
                } else {
                    holder.buttonDone.setText(R.string.reminder_done_button_undone_text)
                    holder.buttonDone.icon = null
                }
                holder.animateDoneChanged()
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
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

        if (reminder.description.isNullOrEmpty())
            viewHolder.textViewDescription.visibility = View.GONE
        else
            viewHolder.textViewDescription.text = reminder.description

        viewHolder.textViewInterval.text = reminder.getFormattedInterval()

        if (reminder.isDone()) {
            viewHolder.buttonDone.text = "Mark undone"
            viewHolder.buttonDone.icon =
                ContextCompat.getDrawable(viewHolder.itemView.context, R.drawable.ic_check)
        } else {
            viewHolder.buttonDone.setText(R.string.reminder_done_button_undone_text)
            viewHolder.buttonDone.icon = null
        }
    }

    override fun getItemCount() = dataSet.size
}