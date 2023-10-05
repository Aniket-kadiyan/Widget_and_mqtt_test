package com.example.widget_and_mqtt_test.maintenanceInfoScreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONObject

class MachineSelectionViewModel : ViewModel() {

    private val _selected_machine = MutableLiveData<JSONObject>()
    private val _selected_reason_name = MutableLiveData<String>()

    val selected_machine get() =_selected_machine
    val selected_reason_name get() =_selected_reason_name

    fun getSelectedMachine() : MutableLiveData<JSONObject> = selected_machine
    fun getSelectedReasonName() : MutableLiveData<String> = selected_reason_name

    fun setSelectedMachine(machine : JSONObject){
        _selected_machine.value=machine
    }
    fun setSelectedReason(reasonName : String){
        _selected_reason_name.value=reasonName
    }

    fun getCurrentSelectedMachine() : JSONObject? = selected_machine.value
    fun getCurrentSelectedReason() : String? = selected_reason_name.value
}