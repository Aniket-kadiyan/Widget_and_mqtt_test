package com.example.widget_and_mqtt_test.widget
import  android.content.Context
import android.graphics.Color
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.widget_and_mqtt_test.R
import com.example.widget_and_mqtt_test.data.DataRepository

class CollectionRemoteViewsFactory(
    private val context: Context,
    private val repository: DataRepository
) : RemoteViewsService.RemoteViewsFactory {

    data class WidgetItem(val text: String)

    private var widgetItems: List<WidgetItem> = emptyList()

    override fun onCreate() {

    }

    override fun onDestroy() {

    }

    override fun onDataSetChanged() {
        widgetItems = repository.data.value.map { WidgetItem(it.name) }
    }

    override fun getCount(): Int {
        return widgetItems.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        // Construct a remote views item based on the widget item XML file,
        // and set the text based on the position.
        return RemoteViews(context.packageName, R.layout.widget_item).apply {
            setTextViewText(R.id.textView, widgetItems[position].text)
            if(position%3 ==0)
                setInt(R.id.textView , "setBackgroundColor" , Color.GREEN)
            else if(position%3==1)
                setInt(R.id.textView , "setBackgroundColor" , Color.TRANSPARENT)
            else
                setInt(R.id.textView , "setBackgroundColor" , Color.LTGRAY)
        }
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return widgetItems[position].hashCode().toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

}