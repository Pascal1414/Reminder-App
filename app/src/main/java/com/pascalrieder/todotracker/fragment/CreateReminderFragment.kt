package com.pascalrieder.todotracker.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pascalrieder.todotracker.R


class CreateReminderFragment : Fragment(R.layout.fragment_create_reminder) {
    private var fab: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fab = requireActivity().findViewById(R.id.floating_action_button)
        fab?.hide()

    }

    override fun onDestroy() {
        super.onDestroy()
        fab?.show()
    }
}