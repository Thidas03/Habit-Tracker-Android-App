package com.example.habbittracker.ui.settings

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.habbittracker.R
import com.example.habbittracker.databinding.FragmentSettingsBinding
import com.example.habbittracker.databinding.DialogHydrationSettingsBinding
import com.example.habbittracker.utils.DataManager
import com.example.habbittracker.work.HydrationReminderWorker

/**
 * Fragment for app settings and configuration.
 * Handles hydration reminder settings and other app preferences.
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var dataManager: DataManager
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dataManager = DataManager(requireContext())
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        
        setupClickListeners()
        observeViewModel()
        
        // Load initial settings
        viewModel.loadSettings(dataManager)
    }

    private fun setupClickListeners() {
        binding.cardHydrationReminder.setOnClickListener {
            showHydrationSettingsDialog()
        }
        
        binding.cardNotifications.setOnClickListener {
            // Handle notification settings
            Toast.makeText(context, "Notification settings coming soon", Toast.LENGTH_SHORT).show()
        }
        
        binding.cardTheme.setOnClickListener {
            // Handle theme settings
            Toast.makeText(context, "Theme settings coming soon", Toast.LENGTH_SHORT).show()
        }
        
        binding.cardDataExport.setOnClickListener {
            exportData()
        }
        
        binding.cardDataImport.setOnClickListener {
            importData()
        }
        
        binding.cardAbout.setOnClickListener {
            showAboutDialog()
        }
    }

    private fun observeViewModel() {
        viewModel.hydrationReminderEnabled.observe(viewLifecycleOwner) { enabled ->
            binding.switchHydrationReminder.isChecked = enabled
            binding.textViewHydrationStatus.text = if (enabled) {
                getString(R.string.reminder_enabled)
            } else {
                getString(R.string.reminder_disabled)
            }
        }
        
        viewModel.hydrationInterval.observe(viewLifecycleOwner) { interval ->
            binding.textViewHydrationInterval.text = "$interval minutes"
        }
    }

    private fun showHydrationSettingsDialog() {
        val dialogBinding = DialogHydrationSettingsBinding.inflate(layoutInflater)
        
        // Pre-fill current settings
        dialogBinding.switchEnableReminders.isChecked = dataManager.isHydrationReminderEnabled()
        dialogBinding.editTextInterval.setText(dataManager.getHydrationInterval().toString())
        
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.hydration_title))
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val enabled = dialogBinding.switchEnableReminders.isChecked
                val interval = dialogBinding.editTextInterval.text.toString().toIntOrNull() ?: 60
                
                if (interval < 15) {
                    Toast.makeText(context, "Minimum interval is 15 minutes", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                viewModel.updateHydrationSettings(enabled, interval, dataManager)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
        
        dialog.show()
    }

    private fun exportData() {
        // Export data functionality
        val exportIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Export your habit tracker data")
            type = "text/plain"
        }
        
        startActivity(Intent.createChooser(exportIntent, "Export Data"))
    }

    private fun importData() {
        // Import data functionality
        Toast.makeText(context, "Import data functionality coming soon", Toast.LENGTH_SHORT).show()
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("About Habit Tracker")
            .setMessage("Version 1.0\n\nA wellness app to help you track daily habits and mood.\n\nFeatures:\n• Daily habit tracking\n• Mood journal with emoji\n• Hydration reminders\n• Progress charts\n• Home screen widget")
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
