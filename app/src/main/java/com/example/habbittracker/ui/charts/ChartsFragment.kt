package com.example.habbittracker.ui.charts

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.habbittracker.R
import com.example.habbittracker.databinding.FragmentChartsBinding
import com.example.habbittracker.utils.DataManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment for displaying charts and analytics.
 * Shows mood trends and habit completion statistics using MPAndroidChart.
 */
class ChartsFragment : Fragment() {

    private var _binding: FragmentChartsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var dataManager: DataManager
    private lateinit var viewModel: ChartsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dataManager = DataManager(requireContext())
        viewModel = ViewModelProvider(this)[ChartsViewModel::class.java]
        
        setupCharts()
        observeViewModel()
        
        // Load initial data
        viewModel.loadChartData(dataManager)
    }

    private fun setupCharts() {
        setupMoodTrendChart()
        setupHabitCompletionChart()
    }

    private fun setupMoodTrendChart() {
        binding.chartMoodTrend.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            
            // Configure X-axis
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                setDrawAxisLine(true)
            }
            
            // Configure Y-axis
            axisLeft.apply {
                setDrawGridLines(true)
                setDrawAxisLine(true)
                axisMinimum = 0f
                axisMaximum = 10f
            }
            
            axisRight.isEnabled = false
            
            // Configure legend
            legend.apply {
                isEnabled = true
                textSize = 12f
            }
        }
    }

    private fun setupHabitCompletionChart() {
        binding.chartHabitCompletion.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            
            // Configure X-axis
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                setDrawAxisLine(true)
            }
            
            // Configure Y-axis
            axisLeft.apply {
                setDrawGridLines(true)
                setDrawAxisLine(true)
                axisMinimum = 0f
                axisMaximum = 100f
            }
            
            axisRight.isEnabled = false
            
            // Configure legend
            legend.apply {
                isEnabled = true
                textSize = 12f
            }
        }
    }

    private fun observeViewModel() {
        viewModel.moodTrendData.observe(viewLifecycleOwner) { data ->
            updateMoodTrendChart(data)
        }
        
        viewModel.habitCompletionData.observe(viewLifecycleOwner) { data ->
            updateHabitCompletionChart(data)
        }
    }

    private fun updateMoodTrendChart(moodData: List<Pair<String, Float>>) {
        if (moodData.isEmpty()) {
            binding.chartMoodTrend.visibility = View.GONE
            binding.textViewMoodChartEmpty.visibility = View.VISIBLE
            return
        }
        
        binding.chartMoodTrend.visibility = View.VISIBLE
        binding.textViewMoodChartEmpty.visibility = View.GONE
        
        val entries = moodData.mapIndexed { index, (date, moodValue) ->
            Entry(index.toFloat(), moodValue)
        }
        
        val dataSet = LineDataSet(entries, "Mood Trend").apply {
            color = Color.parseColor("#4CAF50")
            setCircleColor(Color.parseColor("#4CAF50"))
            lineWidth = 3f
            circleRadius = 5f
            setDrawCircleHole(false)
            setDrawValues(true)
            valueTextSize = 10f
        }
        
        val lineData = LineData(dataSet)
        binding.chartMoodTrend.data = lineData
        
        // Set custom X-axis labels
        val xAxis = binding.chartMoodTrend.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index < moodData.size) {
                    moodData[index].first
                } else ""
            }
        }
        
        binding.chartMoodTrend.invalidate()
    }

    private fun updateHabitCompletionChart(habitData: List<Pair<String, Float>>) {
        if (habitData.isEmpty()) {
            binding.chartHabitCompletion.visibility = View.GONE
            binding.textViewHabitChartEmpty.visibility = View.VISIBLE
            return
        }
        
        binding.chartHabitCompletion.visibility = View.VISIBLE
        binding.textViewHabitChartEmpty.visibility = View.GONE
        
        val entries = habitData.mapIndexed { index, (habitName, completion) ->
            BarEntry(index.toFloat(), completion)
        }
        
        val dataSet = BarDataSet(entries, "Habit Completion %").apply {
            color = Color.parseColor("#2196F3")
            setDrawValues(true)
            valueTextSize = 10f
        }
        
        val barData = BarData(dataSet)
        binding.chartHabitCompletion.data = barData
        
        // Set custom X-axis labels
        val xAxis = binding.chartHabitCompletion.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index < habitData.size) {
                    habitData[index].first
                } else ""
            }
        }
        
        binding.chartHabitCompletion.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
