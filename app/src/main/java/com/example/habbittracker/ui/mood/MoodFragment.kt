package com.example.habbittracker.ui.mood

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habbittracker.R
import com.example.habbittracker.data.MoodEntry
import com.example.habbittracker.data.MoodType
import com.example.habbittracker.databinding.FragmentMoodBinding
import com.example.habbittracker.databinding.DialogAddMoodBinding
import com.example.habbittracker.utils.DataManager
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment for managing mood journal entries.
 * Allows users to log their mood with emoji selection and notes.
 */
class MoodFragment : Fragment() {

    private var _binding: FragmentMoodBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var moodAdapter: MoodAdapter
    private lateinit var dataManager: DataManager
    private lateinit var viewModel: MoodViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dataManager = DataManager(requireContext())
        viewModel = ViewModelProvider(this)[MoodViewModel::class.java]
        
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
        
        // Load initial data
        viewModel.loadMoodEntries(dataManager)
    }

    private fun setupRecyclerView() {
        moodAdapter = MoodAdapter(
            onMoodClick = { moodEntry -> showMoodDetails(moodEntry) },
            onMoodShare = { moodEntry -> shareMood(moodEntry) }
        )
        
        binding.recyclerViewMoods.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = moodAdapter
        }
    }

    private fun setupClickListeners() {
        binding.fabAddMood.setOnClickListener {
            showAddMoodDialog()
        }
        
        binding.buttonShakeToMood.setOnClickListener {
            // This would integrate with sensor to detect shake
            showAddMoodDialog()
        }
    }

    private fun observeViewModel() {
        viewModel.moodEntries.observe(viewLifecycleOwner) { entries ->
            moodAdapter.submitList(entries)
            updateMoodSummary(entries)
        }
    }

    private fun showAddMoodDialog() {
        val dialogBinding = DialogAddMoodBinding.inflate(layoutInflater)
        
        // Setup emoji selection
        setupEmojiSelection(dialogBinding)
        
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.add_mood))
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val selectedEmoji = dialogBinding.textViewSelectedEmoji.text.toString()
                val note = dialogBinding.editTextNote.text.toString().trim()
                
                if (selectedEmoji.isNotEmpty()) {
                    val moodEntry = MoodEntry(
                        emoji = selectedEmoji,
                        note = note
                    )
                    viewModel.addMoodEntry(moodEntry, dataManager)
                } else {
                    Toast.makeText(context, "Please select a mood emoji", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
        
        dialog.show()
    }

    private fun setupEmojiSelection(binding: DialogAddMoodBinding) {
        val moods = MoodType.getAllMoods()
        
        // Create emoji buttons
        val emojiButtons = moods.map { mood ->
            val button = android.widget.Button(requireContext()).apply {
                text = mood.emoji
                textSize = 24f
                setOnClickListener {
                    binding.textViewSelectedEmoji.text = mood.emoji
                    binding.textViewSelectedMoodName.text = mood.displayName
                }
            }
            button
        }
        
        // Add buttons to the layout
        binding.linearLayoutEmojis.removeAllViews()
        emojiButtons.forEach { button ->
            binding.linearLayoutEmojis.addView(button)
        }
    }

    private fun showMoodDetails(moodEntry: MoodEntry) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
        val message = "Mood: ${moodEntry.emoji}\n" +
                "Date: ${dateFormat.format(moodEntry.date)}\n" +
                if (moodEntry.note.isNotEmpty()) "Note: ${moodEntry.note}" else ""
        
        AlertDialog.Builder(requireContext())
            .setTitle("Mood Entry")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .setNeutralButton("Share") { _, _ -> shareMood(moodEntry) }
            .show()
    }

    private fun shareMood(moodEntry: MoodEntry) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val shareText = "My mood today: ${moodEntry.emoji}\n" +
                "Date: ${dateFormat.format(moodEntry.date)}\n" +
                if (moodEntry.note.isNotEmpty()) "Note: ${moodEntry.note}" else ""
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        
        startActivity(Intent.createChooser(shareIntent, "Share your mood"))
    }

    private fun updateMoodSummary(entries: List<MoodEntry>) {
        if (entries.isEmpty()) {
            binding.textViewMoodSummary.text = "No mood entries yet"
            binding.layoutEmpty.visibility = View.VISIBLE
            binding.recyclerViewMoods.visibility = View.GONE
        } else {
            binding.layoutEmpty.visibility = View.GONE
            binding.recyclerViewMoods.visibility = View.VISIBLE
            
            val today = Calendar.getInstance()
            val todayEntries = entries.filter { entry ->
                val entryDate = Calendar.getInstance().apply { time = entry.date }
                entryDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
                entryDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)
            }
            
            if (todayEntries.isNotEmpty()) {
                val latestMood = todayEntries.maxByOrNull { it.date }
                binding.textViewMoodSummary.text = "Today's mood: ${latestMood?.emoji}"
            } else {
                binding.textViewMoodSummary.text = "No mood logged today"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
