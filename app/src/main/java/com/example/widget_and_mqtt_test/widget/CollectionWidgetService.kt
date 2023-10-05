package com.example.widget_and_mqtt_test.widget

import android.content.Intent
import android.widget.RemoteViewsService
import com.example.widget_and_mqtt_test.App


class CollectionWidgetService : RemoteViewsService() {

    private val container by lazy { (applicationContext as App).serviceContainer }

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return CollectionRemoteViewsFactory(applicationContext, container.repository)
    }
}