package com.pascalrieder.todotracker

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.color.DynamicColors
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pascalrieder.todotracker.dao.ReminderDao.Companion.toReminders
import com.pascalrieder.todotracker.fragment.CreateReminderFragment
import com.pascalrieder.todotracker.fragment.OverviewFragment
import com.pascalrieder.todotracker.fragment.RoomOverviewFragment
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        DynamicColors.applyToActivitiesIfAvailable(application)

        val fab = findViewById<FloatingActionButton>(R.id.floating_action_button)
        fab.setOnClickListener(::onFabClick)
        fab.setOnLongClickListener(::onFabLongClick)

        navigateToOverview()

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        if (!alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            startActivity(intent)
        }

        checkAndRequestPermissions()
    }

    fun onFabClick(view: View) {
        navigateToCreateReminder()
    }

    fun onFabLongClick(view: View) : Boolean {
        navigateToRoomOverview()
        return true
    }

    private fun navigateToCreateReminder() {
        val reminderFragment = CreateReminderFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, reminderFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToOverview() {
        val overviewFragment = OverviewFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, overviewFragment)
            .commit()
    }

    private fun navigateToRoomOverview() {
        val roomOverviewFragment = RoomOverviewFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, roomOverviewFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }


        val alarmManager =
            getSystemService(ALARM_SERVICE) as AlarmManager
        if (!alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            intent.setData(Uri.fromParts("package", packageName, null))
        }
    }
}