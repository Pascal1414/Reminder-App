package com.pascalrieder.reminder_app.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.pascalrieder.reminder_app.AppDatabase
import com.pascalrieder.reminder_app.R
import kotlinx.coroutines.launch

class RoomOverviewFragment : Fragment(R.layout.fragment_room_overview) {

    private lateinit var db: AppDatabase

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView = view.findViewById<TextView>(R.id.room_overview_text)

        db = AppDatabase.getInstance(requireContext())

        lifecycleScope.launch {
            val notifications = db.notificationDao().getAll()
            textView.text = notifications.sortedBy { it.reminderId }
                .joinToString("") { "${it.id}    ${it.wasAlreadyDone}    ${it.reminderId}\n" }
        }
    }
}