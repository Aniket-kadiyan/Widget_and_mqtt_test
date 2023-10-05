package com.example.widget_and_mqtt_test.maintenanceInfoScreen

import android.content.Context
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.text.toUpperCase
import androidx.recyclerview.widget.RecyclerView
import com.example.widget_and_mqtt_test.databinding.MaintenanceInfoScreenListItemLayoutBinding
import org.json.JSONObject

class MachineMaintenanceListAdapter(
    var itemlist: List<JSONObject>,
    viewModel: MachineSelectionViewModel
) : RecyclerView.Adapter<MachineMaintenanceListAdapter.MenuListHolder>() {
    private lateinit var context: Context
    val viewmd = viewModel

    inner class MenuListHolder(val binding: MaintenanceInfoScreenListItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: JSONObject, position: Int) {
            binding.apply {
                var name = item.getString("line") + " -- " + item.getString("machine")
                var status = ""
                lineNameTVmisl.text=name
                if (item.getString("status") == "sent for maintenance") {
                    status = item.getString("status").uppercase()
                    statusIndicatorimgmisl.setColorFilter(0xFFEE4B2B.toInt(), PorterDuff.Mode.LIGHTEN)
                } else if (item.getString("status") == "assigned for maintenance") {
                    status = "assigned for maintenance to:\n" + item.getString("assigned to")
                    status = status.uppercase()
                    statusIndicatorimgmisl.setColorFilter(0xFFFFC000.toInt(), PorterDuff.Mode.LIGHTEN)
                }else if (item.getString("status") == "maintenance complete") {
                    status = "maintenance complete.\n reason :\n" + item.getString("reason")
                    status = status.uppercase()
                    statusIndicatorimgmisl.setColorFilter(0xFF0BDA51.toInt(), PorterDuff.Mode.LIGHTEN)
                }
                StatusTVmisl.text=status
                root.setOnClickListener {
                    if(item.getString("status")=="sent for maintenance"){
                        viewmd.setSelectedMachine(item)
                    }
                    else if(item.getString("status") == "assigned for maintenance"){
                        viewmd.setSelectedMachine(item)
                    }

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuListHolder {
        context = parent.context
        return MenuListHolder(
            MaintenanceInfoScreenListItemLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return itemlist.size
    }

    override fun onBindViewHolder(holder: MenuListHolder, position: Int) {
        val item = itemlist[position]
        Log.d("adapter binding", "bind: " + "on bind view holder" + item.toString())
        holder.bind(item, position)
    }

    fun updateData(ulist: List<JSONObject>) {
        itemlist = ulist
        notifyDataSetChanged()
    }

}