package com.example.widget_and_mqtt_test.maintenanceInfoScreen

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.widget_and_mqtt_test.R
import com.example.widget_and_mqtt_test.databinding.SelectMachineBottomModalItemLayoutBinding
import com.example.widget_and_mqtt_test.machineIdSElection.LineSelectionViewModel
import com.example.widget_and_mqtt_test.machineIdSElection.MachineSelectionModalListAdapter

class MaintenanceInfoBottomSheetAdapter(var reasonList : List<String> , viewModel: MachineSelectionViewModel) : RecyclerView.Adapter<MaintenanceInfoBottomSheetAdapter.ItemViewHolder>(){
    private lateinit var context : Context
    val viewmd = viewModel
    var selectedPosition = -1
    inner class ItemViewHolder(val binding : SelectMachineBottomModalItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(reason : String){
            binding.apply {
                machineItemTV.text=reason
                root.setOnClickListener{
                    viewmd.setSelectedReason(reason)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(SelectMachineBottomModalItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return reasonList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val reason = reasonList[position]
        holder.bind(reason)
        if(selectedPosition==position){
            holder.itemView.isSelected=true
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
            viewmd.setSelectedReason(reason)
        }
        else{
            holder.itemView.isSelected=false
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
        }
        holder.itemView.setOnClickListener {
            if(selectedPosition>=0) {
                notifyItemChanged(selectedPosition)
            }
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
        }

    }
}