package com.example.widget_and_mqtt_test

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import com.example.widget_and_mqtt_test.widget.CollectionWidgetProvider
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class App : Application() {

    lateinit var serviceContainer: ServiceContainer

    override fun onCreate() {
        super.onCreate()

        serviceContainer = ServiceContainer(this)

        serviceContainer.repository.data
            .onEach {
                val component = ComponentName(this, CollectionWidgetProvider::class.java)
                val ids = AppWidgetManager.getInstance(applicationContext).getAppWidgetIds(component)

                serviceContainer.appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.AdapterViewFlipper)
            }
            .launchIn(scope = serviceContainer.scope)
    }
}