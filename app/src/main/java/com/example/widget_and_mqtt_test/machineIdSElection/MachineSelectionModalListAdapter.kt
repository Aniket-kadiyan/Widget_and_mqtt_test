package com.example.widget_and_mqtt_test.machineIdSElection

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.widget_and_mqtt_test.R
import com.example.widget_and_mqtt_test.databinding.SelectMachineBottomModalItemLayoutBinding

class MachineSelectionModalListAdapter(var machineList : List<String> , viewModel: LineSelectionViewModel) : RecyclerView.Adapter<MachineSelectionModalListAdapter.ItemViewHolder>(){
    private lateinit var context : Context
    val viewmd = viewModel
    var selectedPosition = -1
    inner class ItemViewHolder(val binding : SelectMachineBottomModalItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(machine : String){
            binding.apply {
                machineItemTV.text=machine
                root.setOnClickListener{
                    viewmd.setSelectedMachine(machine)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(SelectMachineBottomModalItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return  machineList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val machine = machineList[position]
        holder.bind(machine)
        if(selectedPosition==position){
            holder.itemView.isSelected=true
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
            viewmd.setSelectedMachine(machine)
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