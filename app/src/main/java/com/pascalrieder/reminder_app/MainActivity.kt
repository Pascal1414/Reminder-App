package com.pascalrieder.reminder_app

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pascalrieder.reminder_app.fragment.CreateReminderFragment
import com.pascalrieder.reminder_app.fragment.OverviewFragment
import com.pascalrieder.reminder_app.fragment.RoomOverviewFragment

class MainActivity : AppCompatActivity() {

    private lateinit var alarmManager: AlarmManager

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val fab = findViewById<FloatingActionButton>(R.id.floating_action_button)
        fab.setOnClickListener(::onFabClick)
        fab.setOnLongClickListener(::onFabLongClick)

        if (savedInstanceState == null)
            navigateToOverview()

        checkAndRequestPermissions()
    }

    fun onFabClick(view: View) {
        navigateToCreateReminder()
    }

    fun onFabLongClick(view: View): Boolean {
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
        if (!canPostNotifications() || !alarmManager.canScheduleExactAlarms())
            MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.ic_mark_email_unread)
                .setTitle("Permissions required")
                .setMessage("The App is missing one or more permissions that allow the app to send you reminders at exact times.")
                .setPositiveButton("Ok") { _, _ ->
                    requestPermissions()
                }
                .show()
    }

    private fun canPostNotifications(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        if (!canPostNotifications()) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (!alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            intent.setData(("package:$packageName").toUri())
            startActivity(intent)
        }
    }
}