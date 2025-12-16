package com.example.habbittracker.ui.charts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.habbittracker.data.MoodType
import com.example.habbittracker.utils.DataManager
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel for managing chart data and analytics.
 * Processes mood and habit data for visualization.
 */
class ChartsViewModel : ViewModel() {

    private val _moodTrendData = MutableLiveData<List<Pair<String, Float>>>()
    val moodTrendData: LiveData<List<Pair<String, Float>>> = _moodTrendData

    private val _habitCompletionData = MutableLiveData<List<Pair<String, Float>>>()
    val habitCompletionData: LiveData<List<Pair<String, Float>>> = _habitCompletionData

    /**
     * Load and process data for charts.
     */
    fun loadChartData(dataManager: DataManager) {
        loadMoodTrendData(dataManager)
        loadHabitCompletionData(dataManager)
    }

    /**
     * Load mood trend data for the past 7 days.
     */
    private fun loadMoodTrendData(dataManager: DataManager) {
        val moodEntries = dataManager.getMoodEntriesForWeek()
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        
        // Group mood entries by date and calculate average mood value
        val moodByDate = moodEntries.groupBy { entry ->
            val calendar = Calendar.getInstance()
            calendar.time = entry.date
            calendar.get(Calendar.DAY_OF_WEEK)
        }.mapValues { (_, entries) ->
            // Convert emoji to numeric value for charting
            val moodValues = entries.map { entry ->
                convertEmojiToNumericValue(entry.emoji)
            }
            moodValues.average().toFloat()
        }
        
        // Create data for the past 7 days
        val calendar = Calendar.getInstance()
        val moodTrendList = mutableListOf<Pair<String, Float>>()
        
        for (i in 6 downTo 0) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val dateString = dateFormat.format(calendar.time)
            
            val moodValue = moodByDate[dayOfWeek] ?: 5f // Default neutral mood
            moodTrendList.add(Pair(dateString, moodValue))
        }
        
        _moodTrendData.value = moodTrendList
    }

    /**
     * Load habit completion data for today.
     */
    private fun loadHabitCompletionData(dataManager: DataManager) {
        val todayProgress = dataManager.getTodayHabitProgress()
        val habitCompletionList = todayProgress.map { (habit, completion) ->
            val completionPercentage = if (completion != null) {
                completion.getCompletionPercentage()
            } else {
                0f
            }
            Pair(habit.name, completionPercentage)
        }
        
        _habitCompletionData.value = habitCompletionList
    }

    /**
     * Convert emoji to numeric value for charting.
     * Returns a value between 1-10 where 10 is the most positive.
     */
    private fun convertEmojiToNumericValue(emoji: String): Float {
        return when (emoji) {
            "😊" -> 9f  // Happy
            "🤩" -> 10f // Excited
            "😌" -> 8f  // Calm
            "🙏" -> 9f  // Grateful
            "😴" -> 4f  // Tired
            "😰" -> 3f  // Stressed
            "😢" -> 2f  // Sad
            "😠" -> 1f  // Angry
            else -> 5f  // Neutral
        }
    }
}
