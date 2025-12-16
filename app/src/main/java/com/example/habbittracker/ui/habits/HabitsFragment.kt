package com.example.habbittracker.ui.habits

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habbittracker.R
import com.example.habbittracker.data.Habit
import com.example.habbittracker.databinding.FragmentHabitsBinding
import com.example.habbittracker.databinding.DialogAddHabitBinding
import com.example.habbittracker.utils.DataManager
import java.util.*

/**
 * Fragment for managing daily habits.
 * Displays a list of habits with completion status and allows adding/editing/deleting habits.
 */
class HabitsFragment : Fragment() {

    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var habitsAdapter: HabitsAdapter
    private lateinit var dataManager: DataManager
    private lateinit var viewModel: HabitsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dataManager = DataManager(requireContext())
        viewModel = ViewModelProvider(this)[HabitsViewModel::class.java]
        
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
        
        // Load initial data
        viewModel.loadHabits(dataManager)
    }

    private fun setupRecyclerView() {
        habitsAdapter = HabitsAdapter(
            onHabitClick = { habit -> showEditHabitDialog(habit) },
            onHabitComplete = { habit -> toggleHabitCompletion(habit) },
            onHabitDelete = { habit -> showDeleteConfirmation(habit) }
        )
        
        binding.recyclerViewHabits.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = habitsAdapter
        }
    }

    private fun setupClickListeners() {
        binding.fabAddHabit.setOnClickListener {
            showAddHabitDialog()
        }
    }

    private fun observeViewModel() {
        viewModel.habits.observe(viewLifecycleOwner) { habits ->
            habitsAdapter.submitList(habits)
            updateProgressSummary()
        }
    }

    private fun showAddHabitDialog() {
        val dialogBinding = DialogAddHabitBinding.inflate(layoutInflater)
        
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.add_habit))
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val name = dialogBinding.editTextHabitName.text.toString().trim()
                val description = dialogBinding.editTextDescription.text.toString().trim()
                val frequency = dialogBinding.editTextFrequency.text.toString().toIntOrNull() ?: 1
                
                if (name.isNotEmpty()) {
                    val habit = Habit(
                        name = name,
                        description = description,
                        frequency = frequency
                    )
                    viewModel.addHabit(habit, dataManager)
                } else {
                    Toast.makeText(context, "Please enter a habit name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
        
        dialog.show()
    }

    private fun showEditHabitDialog(habit: Habit) {
        val dialogBinding = DialogAddHabitBinding.inflate(layoutInflater)
        
        // Pre-fill the form with existing habit data
        dialogBinding.editTextHabitName.setText(habit.name)
        dialogBinding.editTextDescription.setText(habit.description)
        dialogBinding.editTextFrequency.setText(habit.frequency.toString())
        
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.edit_habit))
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val name = dialogBinding.editTextHabitName.text.toString().trim()
                val description = dialogBinding.editTextDescription.text.toString().trim()
                val frequency = dialogBinding.editTextFrequency.text.toString().toIntOrNull() ?: 1
                
                if (name.isNotEmpty()) {
                    val updatedHabit = habit.copy(
                        name = name,
                        description = description,
                        frequency = frequency
                    )
                    viewModel.updateHabit(updatedHabit, dataManager)
                } else {
                    Toast.makeText(context, "Please enter a habit name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
        
        dialog.show()
    }

    private fun toggleHabitCompletion(habit: Habit) {
        val today = Date()
        val completion = dataManager.getHabitCompletionForDate(habit.id, today)
        
        if (completion?.isCompleted() == true) {
            // Mark as incomplete
            viewModel.markHabitIncomplete(habit, dataManager)
        } else {
            // Mark as complete
            viewModel.markHabitComplete(habit, dataManager)
        }
    }

    private fun showDeleteConfirmation(habit: Habit) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_habit))
            .setMessage("Are you sure you want to delete '${habit.name}'? This will also delete all completion records.")
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deleteHabit(habit, dataManager)
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun updateProgressSummary() {
        val todayProgress = dataManager.getTodayHabitProgress()
        val completedCount = todayProgress.count { it.second?.isCompleted() == true }
        val totalCount = todayProgress.size
        
        binding.textViewProgress.text = getString(
            R.string.progress,
            completedCount,
            totalCount
        )
        
        if (totalCount > 0) {
            val percentage = (completedCount.toFloat() / totalCount.toFloat() * 100).toInt()
            binding.progressBar.setProgress(percentage, true)
        } else {
            binding.progressBar.setProgress(0, true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
