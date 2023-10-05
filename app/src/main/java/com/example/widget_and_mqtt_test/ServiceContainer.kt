package com.example.widget_and_mqtt_test


import android.appwidget.AppWidgetManager
import android.content.Context
import com.example.widget_and_mqtt_test.data.DataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class ServiceContainer(context: Context) {

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val repository = DataRepository(scope)

    val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(context)
}