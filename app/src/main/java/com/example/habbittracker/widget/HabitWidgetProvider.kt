package com.example.habbittracker.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.habbittracker.MainActivity
import com.example.habbittracker.R
import com.example.habbittracker.utils.DataManager

/**
 * App Widget Provider for displaying today's habit completion progress.
 * Shows a summary of habit completion percentage on the home screen.
 */
class HabitWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Called when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Called when the last widget is deleted
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val dataManager = DataManager(context)
            val todayProgress = dataManager.getTodayHabitProgress()
            
            val completedCount = todayProgress.count { it.second?.isCompleted() == true }
            val totalCount = todayProgress.size
            
            val completionPercentage = if (totalCount > 0) {
                (completedCount.toFloat() / totalCount.toFloat() * 100).toInt()
            } else {
                0
            }
            
            // Create RemoteViews
            val views = RemoteViews(context.packageName, R.layout.widget_habit_progress)
            
            // Update widget content
            views.setTextViewText(R.id.textViewWidgetTitle, "Today's Progress")
            views.setTextViewText(R.id.textViewWidgetProgress, "$completedCount/$totalCount")
            views.setTextViewText(R.id.textViewWidgetPercentage, "$completionPercentage%")
            views.setProgressBar(R.id.progressBarWidget, 100, completionPercentage, false)
            
            // Set click intent to open the app
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widgetContainer, pendingIntent)
            
            // Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
