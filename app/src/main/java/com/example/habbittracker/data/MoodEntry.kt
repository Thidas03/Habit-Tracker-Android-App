package com.example.habbittracker.data

import java.util.Date

/**
 * Data class representing a mood journal entry.
 * 
 * @param id Unique identifier for the mood entry
 * @param emoji The emoji representing the mood
 * @param note Optional note about the mood
 * @param date Date and time when the mood was recorded
 */
data class MoodEntry(
    val id: String = "",
    val emoji: String = "",
    val note: String = "",
    val date: Date = Date()
)

/**
 * Enum class representing different mood types with their corresponding emojis.
 */
enum class MoodType(val emoji: String, val displayName: String) {
    HAPPY("😊", "Happy"),
    SAD("😢", "Sad"),
    ANGRY("😠", "Angry"),
    EXCITED("🤩", "Excited"),
    CALM("😌", "Calm"),
    TIRED("😴", "Tired"),
    STRESSED("😰", "Stressed"),
    GRATEFUL("🙏", "Grateful");
    
    companion object {
        /**
         * Get all available mood types as a list.
         */
        fun getAllMoods(): List<MoodType> = values().toList()
        
        /**
         * Find mood type by emoji.
         */
        fun findByEmoji(emoji: String): MoodType? {
            return values().find { it.emoji == emoji }
        }
    }
}
