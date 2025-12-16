package com.example.habbittracker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.habbittracker.utils.DataManager
import com.example.habbittracker.work.HydrationReminderWorker
import java.util.concurrent.TimeUnit

/**
 * Broadcast receiver that restarts hydration reminders after device boot.
 * Ensures notifications continue working after device restart.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val dataManager = DataManager(context)
            
            // Restart hydration reminders if they were enabled
            if (dataManager.isHydrationReminderEnabled()) {
                val intervalMinutes = dataManager.getHydrationInterval()
                scheduleHydrationReminder(context, intervalMinutes)
            }
        }
    }

    /**
     * Schedule hydration reminder work.
     */
    private fun scheduleHydrationReminder(context: Context, intervalMinutes: Int) {
        val workRequest = PeriodicWorkRequestBuilder<HydrationReminderWorker>(
            intervalMinutes.toLong(),
            TimeUnit.MINUTES
        )
            .setInitialDelay(intervalMinutes.toLong(), TimeUnit.MINUTES)
            .addTag("hydration_reminder")
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
