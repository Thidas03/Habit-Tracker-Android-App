package com.example.habbittracker.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.habbittracker.utils.DataManager
import com.example.habbittracker.work.HydrationReminderWorker
import java.util.concurrent.TimeUnit

/**
 * ViewModel for managing app settings and preferences.
 * Handles hydration reminder configuration and other settings.
 */
class SettingsViewModel : ViewModel() {

    private val _hydrationReminderEnabled = MutableLiveData<Boolean>()
    val hydrationReminderEnabled: LiveData<Boolean> = _hydrationReminderEnabled

    private val _hydrationInterval = MutableLiveData<Int>()
    val hydrationInterval: LiveData<Int> = _hydrationInterval

    /**
     * Load current settings from data manager.
     */
    fun loadSettings(dataManager: DataManager) {
        _hydrationReminderEnabled.value = dataManager.isHydrationReminderEnabled()
        _hydrationInterval.value = dataManager.getHydrationInterval()
    }

    /**
     * Update hydration reminder settings.
     */
    fun updateHydrationSettings(enabled: Boolean, intervalMinutes: Int, dataManager: DataManager) {
        dataManager.setHydrationReminderEnabled(enabled)
        dataManager.setHydrationInterval(intervalMinutes)
        
        _hydrationReminderEnabled.value = enabled
        _hydrationInterval.value = intervalMinutes
        
        // Schedule or cancel work based on settings
        if (enabled) {
            scheduleHydrationReminder(intervalMinutes)
        } else {
            cancelHydrationReminder()
        }
    }

    /**
     * Schedule periodic hydration reminders using WorkManager.
     */
    private fun scheduleHydrationReminder(intervalMinutes: Int) {
        val workRequest = PeriodicWorkRequestBuilder<HydrationReminderWorker>(
            intervalMinutes.toLong(),
            TimeUnit.MINUTES
        )
            .setInitialDelay(intervalMinutes.toLong(), TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance().enqueue(workRequest)
    }

    /**
     * Cancel hydration reminder work.
     */
    private fun cancelHydrationReminder() {
        WorkManager.getInstance().cancelAllWorkByTag("hydration_reminder")
    }
}
