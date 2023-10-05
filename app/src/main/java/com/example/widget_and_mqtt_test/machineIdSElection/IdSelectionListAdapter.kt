package com.example.widget_and_mqtt_test.machineIdSElection

import android.content.Context
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.example.widget_and_mqtt_test.customDrawables.CircleDrawable
import com.example.widget_and_mqtt_test.databinding.MachineSelectionListItemLayoutBinding
import org.json.JSONObject

class IdSelectionListAdapter(var itemList: List<JSONObject>, viewModel: LineSelectionViewModel) :
    RecyclerView.Adapter<IdSelectionListAdapter.MenuListHolder>() {
    private lateinit var context: Context
    val viewmd = viewModel

    inner class MenuListHolder(val binding: MachineSelectionListItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: JSONObject, position: Int) {
            Log.d("adapter binding", "bind: " + item.toString())
            binding.apply {
                lineNameTV.text = item.getString("line")
                breakdownStatusTV.text = item.getString("status")
                if (item.getString("status") == "DOWNTIME") {
                    statusIndicatorimg.setColorFilter(0xFFEE4B2B.toInt(), PorterDuff.Mode.LIGHTEN)
                } else if (item.getString("status") == "MAINTENANCE COMPLETE") {
                    statusIndicatorimg.setColorFilter(0xFFEE4B2B.toInt(), PorterDuff.Mode.LIGHTEN)
                } else if (item.getString("status") == "IN MAINTENANCE") {
                    statusIndicatorimg.setColorFilter(0xFFFFC000.toInt(), PorterDuff.Mode.LIGHTEN)
                } else {
                    statusIndicatorimg.setColorFilter(0xFF0BDA51.toInt(), PorterDuff.Mode.LIGHTEN)
                }

                root.setOnClickListener {
                    if (item.getString("status") == "DOWNTIME") {
                        viewmd.setSelectedLine(item)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuListHolder {
        context = parent.context
        Log.d("adapter binding", "bind: " + "on create view holder")
        return MenuListHolder(
            MachineSelectionListItemLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: MenuListHolder, position: Int) {
        val item = itemList[position]
        Log.d("adapter binding", "bind: " + "on bind view holder" + item.toString())
        holder.bind(item, position)
    }

    fun updateData(ulist: List<JSONObject>) {
        itemList = ulist
        notifyDataSetChanged()
    }

}