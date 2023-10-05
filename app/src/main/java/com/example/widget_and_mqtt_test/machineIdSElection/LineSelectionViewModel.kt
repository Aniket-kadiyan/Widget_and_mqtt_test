package com.example.widget_and_mqtt_test.machineIdSElection

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONObject

class LineSelectionViewModel : ViewModel() {
    private val _selected_line =MutableLiveData<JSONObject>()
    private val _selected_machine_name =MutableLiveData<String>()

    val selected_line get() =_selected_line
    val selected_machine_name get() =_selected_machine_name

    fun getSelectedLine() : MutableLiveData<JSONObject> = selected_line
    fun getSelectedMachineName() : MutableLiveData<String> = selected_machine_name

    fun setSelectedLine(line : JSONObject){
        _selected_line.value=line
    }

    fun setSelectedMachine(machineName : String){
        _selected_machine_name.value=machineName
    }


    fun getCurrentSelectedLine() : JSONObject? = selected_line.value

    fun getCurrentSelectedMachine() : String? = selected_machine_name.value
}