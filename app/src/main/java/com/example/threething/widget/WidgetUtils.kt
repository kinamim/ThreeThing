package com.example.threething.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.example.threething.MyThreeThingsWidget

object WidgetUtils {

    private const val TAG = "WidgetUtils"

    fun openWidgetPicker(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val widgetComponent = ComponentName(context, MyThreeThingsWidget::class.java)

        val pinnedWidgetsSupported = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appWidgetManager.isRequestPinAppWidgetSupported
        } else {
            false
        }

        if (pinnedWidgetsSupported) {
            appWidgetManager.requestPinAppWidget(widgetComponent, null, null)
        } else {
            Toast.makeText(
                context,
                "Pinning widgets not supported on this device. Please add widget manually.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun updateAllWidgets(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val component = ComponentName(context, MyThreeThingsWidget::class.java)
        val widgetIds = appWidgetManager.getAppWidgetIds(component)

        Log.d(TAG, "updateAllWidgets called. Found widgetIds: ${widgetIds.joinToString()}")

        if (widgetIds.isNotEmpty()) {
            val updateIntent = android.content.Intent(context, MyThreeThingsWidget::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
            }
            context.sendBroadcast(updateIntent)
        } else {
            Log.d(TAG, "No widgets to update.")
        }
    }
}
