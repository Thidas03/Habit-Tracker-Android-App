package com.example.habbittracker.ui.mood

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.habbittracker.data.MoodEntry
import com.example.habbittracker.utils.DataManager

/**
 * ViewModel for managing mood journal data and business logic.
 * Handles CRUD operations for mood entries.
 */
class MoodViewModel : ViewModel() {

    private val _moodEntries = MutableLiveData<List<MoodEntry>>()
    val moodEntries: LiveData<List<MoodEntry>> = _moodEntries

    /**
     * Load all mood entries from data manager.
     */
    fun loadMoodEntries(dataManager: DataManager) {
        val entries = dataManager.loadMoodEntries().sortedByDescending { it.date }
        _moodEntries.value = entries
    }

    /**
     * Add a new mood entry.
     */
    fun addMoodEntry(moodEntry: MoodEntry, dataManager: DataManager) {
        dataManager.addMoodEntry(moodEntry)
        loadMoodEntries(dataManager)
    }

    /**
     * Get mood entries for the current week.
     */
    fun getWeeklyMoodEntries(dataManager: DataManager): List<MoodEntry> {
        return dataManager.getMoodEntriesForWeek()
    }
}
