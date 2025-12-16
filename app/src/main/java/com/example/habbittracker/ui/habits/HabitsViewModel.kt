package com.example.habbittracker.ui.habits

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.habbittracker.data.Habit
import com.example.habbittracker.utils.DataManager
import java.util.Date

/**
 * ViewModel for managing habits data and business logic.
 * Handles CRUD operations for habits and habit completions.
 */
class HabitsViewModel : ViewModel() {

    private val _habits = MutableLiveData<List<Habit>>()
    val habits: LiveData<List<Habit>> = _habits

    /**
     * Load all habits from data manager.
     */
    fun loadHabits(dataManager: DataManager) {
        val habitsList = dataManager.loadHabits()
        _habits.value = habitsList
    }

    /**
     * Add a new habit.
     */
    fun addHabit(habit: Habit, dataManager: DataManager) {
        dataManager.addHabit(habit)
        loadHabits(dataManager)
    }

    /**
     * Update an existing habit.
     */
    fun updateHabit(habit: Habit, dataManager: DataManager) {
        dataManager.updateHabit(habit)
        loadHabits(dataManager)
    }

    /**
     * Delete a habit.
     */
    fun deleteHabit(habit: Habit, dataManager: DataManager) {
        dataManager.deleteHabit(habit.id)
        loadHabits(dataManager)
    }

    /**
     * Mark a habit as complete for today.
     */
    fun markHabitComplete(habit: Habit, dataManager: DataManager) {
        dataManager.markHabitComplete(habit.id, Date())
        // Reload habits to update completion status
        loadHabits(dataManager)
    }

    /**
     * Mark a habit as incomplete for today.
     */
    fun markHabitIncomplete(habit: Habit, dataManager: DataManager) {
        dataManager.markHabitIncomplete(habit.id, Date())
        // Reload habits to update completion status
        loadHabits(dataManager)
    }
}
