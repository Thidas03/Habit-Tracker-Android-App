package com.example.habbittracker.ui.habits

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.habbittracker.data.Habit
import com.example.habbittracker.databinding.ItemHabitBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * RecyclerView adapter for displaying habits with completion status.
 * Handles habit interactions like completion toggle, edit, and delete.
 */
class HabitsAdapter(
    private val onHabitClick: (Habit) -> Unit,
    private val onHabitComplete: (Habit) -> Unit,
    private val onHabitDelete: (Habit) -> Unit
) : ListAdapter<Habit, HabitsAdapter.HabitViewHolder>(HabitDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HabitViewHolder(
        private val binding: ItemHabitBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(habit: Habit) {
            binding.apply {
                textViewHabitName.text = habit.name
                textViewDescription.text = habit.description
                textViewFrequency.text = "Target: ${habit.frequency} times/day"
                
                // Set click listeners
                root.setOnClickListener { onHabitClick(habit) }
                buttonComplete.setOnClickListener { onHabitComplete(habit) }
                buttonDelete.setOnClickListener { onHabitDelete(habit) }
                
                // Update completion status (this would need to be passed from the fragment)
                // For now, we'll show a placeholder
                updateCompletionStatus(habit)
            }
        }
        
        private fun updateCompletionStatus(habit: Habit) {
            // This is a simplified version - in a real implementation,
            // you'd pass the completion status from the fragment
            binding.apply {
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                // You would get the actual completion status here
                val isCompleted = false // This should come from the data manager
                
                if (isCompleted) {
                    buttonComplete.text = "✓ Completed"
                    buttonComplete.isEnabled = false
                } else {
                    buttonComplete.text = "Mark Complete"
                    buttonComplete.isEnabled = true
                }
            }
        }
    }

    class HabitDiffCallback : DiffUtil.ItemCallback<Habit>() {
        override fun areItemsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem == newItem
        }
    }
}
