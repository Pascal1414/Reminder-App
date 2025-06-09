package com.pascalrieder.reminder_app.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pascalrieder.reminder_app.repository.ReminderCheckRepository
import com.pascalrieder.reminder_app.repository.ReminderRepository
import com.pascalrieder.reminder_app.viewmodel.MainViewModel

class MainViewModelFactory(
    private val reminderRepository: ReminderRepository,
    private val reminderCheckRepository: ReminderCheckRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(reminderRepository, reminderCheckRepository) as T
    }
}
