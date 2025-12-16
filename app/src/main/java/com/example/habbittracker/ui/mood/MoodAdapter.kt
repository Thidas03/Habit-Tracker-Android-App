package com.example.habbittracker.ui.mood

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.habbittracker.data.MoodEntry
import com.example.habbittracker.databinding.ItemMoodBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * RecyclerView adapter for displaying mood journal entries.
 * Shows mood emoji, date/time, and optional notes.
 */
class MoodAdapter(
    private val onMoodClick: (MoodEntry) -> Unit,
    private val onMoodShare: (MoodEntry) -> Unit
) : ListAdapter<MoodEntry, MoodAdapter.MoodViewHolder>(MoodDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val binding = ItemMoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MoodViewHolder(
        private val binding: ItemMoodBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(moodEntry: MoodEntry) {
            binding.apply {
                textViewEmoji.text = moodEntry.emoji
                textViewNote.text = moodEntry.note
                
                // Format date and time
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                
                textViewDate.text = dateFormat.format(moodEntry.date)
                textViewTime.text = timeFormat.format(moodEntry.date)
                
                // Set click listeners
                root.setOnClickListener { onMoodClick(moodEntry) }
                buttonShare.setOnClickListener { onMoodShare(moodEntry) }
                
                // Show/hide note based on content
                if (moodEntry.note.isNotEmpty()) {
                    textViewNote.visibility = android.view.View.VISIBLE
                } else {
                    textViewNote.visibility = android.view.View.GONE
                }
            }
        }
    }

    class MoodDiffCallback : DiffUtil.ItemCallback<MoodEntry>() {
        override fun areItemsTheSame(oldItem: MoodEntry, newItem: MoodEntry): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MoodEntry, newItem: MoodEntry): Boolean {
            return oldItem == newItem
        }
    }
}
