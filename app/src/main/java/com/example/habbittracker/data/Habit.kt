package com.example.habbittracker.data

import java.util.Date

/**
 * Data class representing a daily habit with its properties and completion tracking.
 * 
 * @param id Unique identifier for the habit
 * @param name The name of the habit
 * @param description Optional description of the habit
 * @param frequency Number of times this habit should be completed per day
 * @param createdAt Date when the habit was created
 * @param isActive Whether the habit is currently active
 */
data class Habit(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val frequency: Int = 1,
    val createdAt: Date = Date(),
    val isActive: Boolean = true
)

/**
 * Data class representing a habit completion record for a specific date.
 * 
 * @param habitId ID of the habit this completion belongs to
 * @param date Date of completion
 * @param completedCount Number of times the habit was completed on this date
 * @param targetCount Target number of completions for this date
 */
data class HabitCompletion(
    val habitId: String,
    val date: String, // Format: yyyy-MM-dd
    val completedCount: Int = 0,
    val targetCount: Int = 1
) {
    /**
     * Returns the completion percentage for this habit on this date.
     */
    fun getCompletionPercentage(): Float {
        return if (targetCount > 0) {
            (completedCount.toFloat() / targetCount.toFloat() * 100).coerceAtMost(100f)
        } else 0f
    }
    
    /**
     * Returns true if the habit is fully completed for this date.
     */
    fun isCompleted(): Boolean {
        return completedCount >= targetCount
    }
}
