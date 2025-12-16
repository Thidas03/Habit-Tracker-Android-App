package com.example.habbittracker.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.habbittracker.data.Habit
import com.example.habbittracker.data.HabitCompletion
import com.example.habbittracker.data.MoodEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class for managing data persistence using SharedPreferences.
 * Handles saving and loading of habits, habit completions, and mood entries.
 */
class DataManager(private val context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("habit_tracker_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    companion object {
        private const val KEY_HABITS = "habits"
        private const val KEY_HABIT_COMPLETIONS = "habit_completions"
        private const val KEY_MOOD_ENTRIES = "mood_entries"
        private const val KEY_HYDRATION_REMINDER_ENABLED = "hydration_reminder_enabled"
        private const val KEY_HYDRATION_INTERVAL = "hydration_interval"
    }
    
    // Habit Management
    fun saveHabits(habits: List<Habit>) {
        val habitsJson = gson.toJson(habits)
        sharedPreferences.edit().putString(KEY_HABITS, habitsJson).apply()
    }
    
    fun loadHabits(): List<Habit> {
        val habitsJson = sharedPreferences.getString(KEY_HABITS, null)
        return if (habitsJson != null) {
            val type = object : TypeToken<List<Habit>>() {}.type
            gson.fromJson(habitsJson, type) ?: emptyList()
        } else {
            emptyList()
        }
    }
    
    fun addHabit(habit: Habit) {
        val habits = loadHabits().toMutableList()
        val newHabit = habit.copy(id = UUID.randomUUID().toString())
        habits.add(newHabit)
        saveHabits(habits)
    }
    
    fun updateHabit(habit: Habit) {
        val habits = loadHabits().toMutableList()
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index != -1) {
            habits[index] = habit
            saveHabits(habits)
        }
    }
    
    fun deleteHabit(habitId: String) {
        val habits = loadHabits().toMutableList()
        habits.removeAll { it.id == habitId }
        saveHabits(habits)
        
        // Also remove all completions for this habit
        val completions = loadHabitCompletions().toMutableList()
        completions.removeAll { it.habitId == habitId }
        saveHabitCompletions(completions)
    }
    
    // Habit Completion Management
    fun saveHabitCompletions(completions: List<HabitCompletion>) {
        val completionsJson = gson.toJson(completions)
        sharedPreferences.edit().putString(KEY_HABIT_COMPLETIONS, completionsJson).apply()
    }
    
    fun loadHabitCompletions(): List<HabitCompletion> {
        val completionsJson = sharedPreferences.getString(KEY_HABIT_COMPLETIONS, null)
        return if (completionsJson != null) {
            val type = object : TypeToken<List<HabitCompletion>>() {}.type
            gson.fromJson(completionsJson, type) ?: emptyList()
        } else {
            emptyList()
        }
    }
    
    fun markHabitComplete(habitId: String, date: Date = Date()) {
        val dateString = dateFormat.format(date)
        val completions = loadHabitCompletions().toMutableList()
        
        val existingCompletion = completions.find { 
            it.habitId == habitId && it.date == dateString 
        }
        
        if (existingCompletion != null) {
            val updatedCompletion = existingCompletion.copy(
                completedCount = existingCompletion.completedCount + 1
            )
            val index = completions.indexOf(existingCompletion)
            completions[index] = updatedCompletion
        } else {
            val habit = loadHabits().find { it.id == habitId }
            val newCompletion = HabitCompletion(
                habitId = habitId,
                date = dateString,
                completedCount = 1,
                targetCount = habit?.frequency ?: 1
            )
            completions.add(newCompletion)
        }
        
        saveHabitCompletions(completions)
    }
    
    fun markHabitIncomplete(habitId: String, date: Date = Date()) {
        val dateString = dateFormat.format(date)
        val completions = loadHabitCompletions().toMutableList()
        
        val existingCompletion = completions.find { 
            it.habitId == habitId && it.date == dateString 
        }
        
        if (existingCompletion != null) {
            val updatedCompletion = existingCompletion.copy(
                completedCount = (existingCompletion.completedCount - 1).coerceAtLeast(0)
            )
            val index = completions.indexOf(existingCompletion)
            completions[index] = updatedCompletion
        }
        
        saveHabitCompletions(completions)
    }
    
    fun getHabitCompletionForDate(habitId: String, date: Date = Date()): HabitCompletion? {
        val dateString = dateFormat.format(date)
        return loadHabitCompletions().find { 
            it.habitId == habitId && it.date == dateString 
        }
    }
    
    fun getTodayHabitProgress(): List<Pair<Habit, HabitCompletion?>> {
        val today = Date()
        val habits = loadHabits().filter { it.isActive }
        return habits.map { habit ->
            val completion = getHabitCompletionForDate(habit.id, today)
            Pair(habit, completion)
        }
    }
    
    // Mood Entry Management
    fun saveMoodEntries(entries: List<MoodEntry>) {
        val entriesJson = gson.toJson(entries)
        sharedPreferences.edit().putString(KEY_MOOD_ENTRIES, entriesJson).apply()
    }
    
    fun loadMoodEntries(): List<MoodEntry> {
        val entriesJson = sharedPreferences.getString(KEY_MOOD_ENTRIES, null)
        return if (entriesJson != null) {
            val type = object : TypeToken<List<MoodEntry>>() {}.type
            gson.fromJson(entriesJson, type) ?: emptyList()
        } else {
            emptyList()
        }
    }
    
    fun addMoodEntry(moodEntry: MoodEntry) {
        val entries = loadMoodEntries().toMutableList()
        val newEntry = moodEntry.copy(id = UUID.randomUUID().toString())
        entries.add(newEntry)
        saveMoodEntries(entries)
    }
    
    fun getMoodEntriesForWeek(): List<MoodEntry> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        val weekStart = calendar.time
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val weekEnd = calendar.time
        
        return loadMoodEntries().filter { entry ->
            entry.date >= weekStart && entry.date <= weekEnd
        }.sortedBy { it.date }
    }
    
    // Hydration Reminder Settings
    fun setHydrationReminderEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_HYDRATION_REMINDER_ENABLED, enabled).apply()
    }
    
    fun isHydrationReminderEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_HYDRATION_REMINDER_ENABLED, false)
    }
    
    fun setHydrationInterval(intervalMinutes: Int) {
        sharedPreferences.edit().putInt(KEY_HYDRATION_INTERVAL, intervalMinutes).apply()
    }
    
    fun getHydrationInterval(): Int {
        return sharedPreferences.getInt(KEY_HYDRATION_INTERVAL, 60) // Default 60 minutes
    }
}
