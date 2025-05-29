package com.example.threething

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.example.threething.data.userPreferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MyThreeWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val prefsFlow = context.userPreferencesDataStore.data
            CoroutineScope(Dispatchers.IO).launch {
                val prefs = prefsFlow.first()
                val views = RemoteViews(context.packageName, R.layout.widget_layout).apply {
                    setTextViewText(R.id.task1, prefs.task1)
                    setTextViewText(R.id.task2, prefs.task2)
                    setTextViewText(R.id.task3, prefs.task3)
                }
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}
