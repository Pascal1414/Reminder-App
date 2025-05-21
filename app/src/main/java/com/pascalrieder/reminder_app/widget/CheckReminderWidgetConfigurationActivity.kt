package com.pascalrieder.reminder_app.widget

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CheckReminderWidgetConfigurationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(this, "Configuration activity started", Toast.LENGTH_SHORT).show()
    }
}