package com.example.widget_and_mqtt_test.maintenanceInfoScreen

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.RecyclerView
import com.example.widget_and_mqtt_test.R
import com.example.widget_and_mqtt_test.databinding.ActivityMachineIdSelectionBinding
import com.example.widget_and_mqtt_test.databinding.MaintenanceInfoScreenLayoutBinding
import com.example.widget_and_mqtt_test.machinestatuslist
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.log

class MaintenanceInfoScreen : AppCompatActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: MaintenanceInfoScreenLayoutBinding
    lateinit var machinestatusarray: JSONArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MaintenanceInfoScreenLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var sharedPreferences = getSharedPreferences("FS", MODE_PRIVATE)
        var myEdit = sharedPreferences.edit()
        var flag = sharedPreferences.contains("machinestatusarray")
        Log.d("shared pref. flags", sharedPreferences.all.toString())
        var machineSelectionViewModel =
            ViewModelProvider(this).get(MachineSelectionViewModel::class.java)
        binding.apply {

            if (flag) {
                machinestatusarray =
                    JSONArray(sharedPreferences.getString("machinestatusarray", ""))
                Log.d("from shared pref.", machinestatusarray.toString())
            } else {
                machinestatusarray = machinestatuslist
                Log.d("from pre stored", machinestatusarray.toString())
            }

            var machinestatus: ArrayList<JSONObject> = arrayListOf()
            for (i in 0..machinestatusarray.length() - 1) {
//                Log.d("json", machinestatusarray.getString(i))
                machinestatus.add(machinestatusarray.getJSONObject(i))
            }
            var adapter = MachineMaintenanceListAdapter(machinestatus, machineSelectionViewModel)
            maintenanceInfoScreenitemListRV.adapter = adapter

            val dialog = BottomSheetDialog(this@MaintenanceInfoScreen)
            dialog.setCancelable(true)
            val modalView =
                layoutInflater.inflate(R.layout.maintenance_info_screen_bottom_modal_layout, null)
            dialog.setContentView(modalView)
            val modalTV = modalView.findViewById<TextView>(R.id.staticTVmislbm)
            val modalButton = modalView.findViewById<Button>(R.id.acceptButtonmislbm)
            val modalReasonList = modalView.findViewById<RecyclerView>(R.id.reasonListRV)
            val reasonnamelist = listOf<String>(
                "Reason 1",
                "Reason 2",
                "Reason 3",
                "Reason 4",
                "Reason 5",
                "Reason 6",
                "Reason 7",
                "Reason 8",
                "Reason 9",
                "Reason 10"
            )
            var adapter1 =
                MaintenanceInfoBottomSheetAdapter(reasonnamelist, machineSelectionViewModel)
            modalReasonList.adapter = adapter1

            maintenanceInfoScreenCameraScannerFAB.setOnClickListener {
                val options = GmsBarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_EAN_13).build()
                val scanner =
                    GmsBarcodeScanning.getClient(this@MaintenanceInfoScreen, options)
                scanner.startScan()
                    .addOnSuccessListener {
                        val rawValue: String = it.rawValue!!
                        if (!rawValue.isNullOrBlank())
                            Log.d("camera scan", "Scan VALUE== " + rawValue.toString())
                        //
                        //set camera MQTT action here
                        //

                        var jobj = JSONObject(rawValue)
                        var machineinfo =
                            JSONObject("{\"line\":\"line5\",\"machine\":\"machine3\",\"status\":\"sent for maintenance\",\"assigned to\":\"\",\"reason\":\"\"}")
                        machineinfo.put("line", jobj.getString("line"))
                        machineinfo.put("machine", jobj.getString("machine name"))
                        var machineexists=false
                        var index=-1
                        var itemstatus=""
                        for(item in machinestatus){
                            if((item.getString("line")==machineinfo.getString("line"))&& (item.getString("machine")==machineinfo.getString("machine"))){
                                machineexists = true
                                index = machinestatus.indexOf(item)
                                itemstatus = item.getString("status")
                                break
                            }
                        }
                        if(machineexists){
                            if(itemstatus=="sent for maintenance"){
                                machineinfo.put("status","assigned for maintenance")
                                machineinfo.put("assigned to"," Engineer 1")
                                machinestatus.remove(machinestatus.get(index))
                                machinestatus.add(index,machineinfo)
                                adapter =
                                    MachineMaintenanceListAdapter(machinestatus, machineSelectionViewModel)
                                maintenanceInfoScreenitemListRV.adapter = adapter

                                if (sharedPreferences.contains("machinestatusarray")) {
                                    myEdit.remove("machinestatusarray").apply()
                                }

                                myEdit.putString("machinestatusarray", machinestatus.toString()).apply()
                            }
                            else if(itemstatus=="assigned for maintenance"){
                                machineinfo.put("status",itemstatus)
                                machineinfo.put("assigned to"," Engineer 1")
                                machineSelectionViewModel.setSelectedMachine(machineinfo)
                                dialog.show()
                            }

                        }




                        if (it.valueType == Barcode.TYPE_URL) {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(rawValue))
                            startActivity(browserIntent)
                        }
                    }
                    .addOnCanceledListener {
                        Log.d("camera scan", "Scan cancelled")
                    }
                    .addOnFailureListener {
                        Log.d("camera scan", "Scan cancelled " + it.toString())
                    }
            }

            modalButton.setOnClickListener {
                var selectedmachine = machineSelectionViewModel.getCurrentSelectedMachine()!!
                if (selectedmachine.getString("status") == "sent for maintenance") {
                    val options = GmsBarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_EAN_13).build()
                    val scanner =
                        GmsBarcodeScanning.getClient(this@MaintenanceInfoScreen, options)
                    scanner.startScan()
                        .addOnSuccessListener {
                            val rawValue: String = it.rawValue!!
                            if (!rawValue.isNullOrBlank())
                                Log.d("camera scan", "Scan VALUE== " + rawValue.toString())
                            //
                            //set camera MQTT action here
                            //

                            var jobj = JSONObject(rawValue)
                            var machineinfo =
                                JSONObject("{\"line\":\"line5\",\"machine\":\"machine3\",\"status\":\"sent for maintenance\",\"assigned to\":\"\",\"reason\":\"\"}")
                            machineinfo.put("line", jobj.getString("line"))
                            machineinfo.put("machine", jobj.getString("machine name"))
                            machineinfo.put("status" , "assigned for maintenance")
                            machineinfo.put("assigned to","engineer 1")
                            var index = -1
                            for (item in machinestatus) {
                                if ((item.getString("line") == jobj.getString("line")) && (item.getString(
                                        "machine"
                                    ) == jobj.getString("machine name"))
                                ) {
                                    index = machinestatus.indexOf(item)
                                }
                            }
                            machinestatus.remove(machinestatus.get(index))
                            machinestatus.add(index, machineinfo)

                            adapter =
                                MachineMaintenanceListAdapter(machinestatus, machineSelectionViewModel)
                            maintenanceInfoScreenitemListRV.adapter = adapter

                            if (sharedPreferences.contains("machinestatusarray")) {
                                myEdit.remove("machinestatusarray").apply()
                            }

                            myEdit.putString("machinestatusarray", machinestatus.toString()).apply()

                            if (it.valueType == Barcode.TYPE_URL) {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(rawValue))
                                startActivity(browserIntent)
                            }
                        }
                        .addOnCanceledListener {
                            Log.d("camera scan", "Scan cancelled")
                        }
                        .addOnFailureListener {
                            Log.d("camera scan", "Scan cancelled " + it.toString())
                        }
                    dialog.show()


                }
                else if (selectedmachine.getString("status") == "assigned for maintenance") {
                    selectedmachine.put("status", "maintenance complete")
                    selectedmachine.put(
                        "reason",
                        machineSelectionViewModel.getCurrentSelectedReason()!!
                    )
                    var index = -1
                    for (item in machinestatus) {
                        if ((item.getString("line") == selectedmachine.getString("line")) && (item.getString(
                                "machine"
                            ) == selectedmachine.getString("machine"))
                        ) {
                            index = machinestatus.indexOf(item)
                        }
                    }
                    machinestatus.remove(machinestatus.get(index))
                    machinestatus.add(index, selectedmachine)
                    adapter =
                        MachineMaintenanceListAdapter(machinestatus, machineSelectionViewModel)
                    maintenanceInfoScreenitemListRV.adapter = adapter

                    if (sharedPreferences.contains("machinestatusarray")) {
                        myEdit.remove("machinestatusarray").apply()
                    }

                    myEdit.putString("machinestatusarray", machinestatus.toString()).apply()
                    var linecomplete = true
                    var templinestatus =
                        JSONArray(sharedPreferences.getString("linestatusarray", ""))
                    for (item in machinestatus) {
                        if ((item.getString("line") == selectedmachine.getString("line")) && (item.getString(
                                "machine"
                            ) != selectedmachine.getString("machine"))
                        ) {
                            if (item.getString("status") != selectedmachine.getString("status")) {
                                linecomplete = false
                            }
                        }
                    }
                    index = -1
                    for (i in 0..templinestatus.length() - 1) {
                        if (selectedmachine.getString("line") == templinestatus.getJSONObject(i)
                                .getString("line")
                        ) {

                            index = i
                            break

                        }
                    }
                    if (linecomplete) {
                        templinestatus.getJSONObject(index).put("status", "MAINTENANCE COMPLETE")
                        myEdit.remove("linestatusarray").apply()
                        myEdit.putString("linestatusarray", templinestatus.toString()).apply()
                    }


                }
                dialog.dismiss()
            }

            machineSelectionViewModel.getSelectedMachine().observe(this@MaintenanceInfoScreen) {
                if (it.getString("status") == "sent for maintenance") {
                    modalTV.text =
                        "Would you like to accept work on: " + it.getString("line") + " -- " + it.getString(
                            "machine"
                        )
                    modalButton.text = "ACCEPT"
                    modalReasonList.isVisible = false
                } else if (it.getString("status") == "assigned for maintenance") {
                    modalTV.text =
                        "Select Downtime Reason for: " + it.getString("line") + " -- " + it.getString(
                            "machine"
                        )
                    modalReasonList.isVisible = true
                    modalButton.text = "SUBMIT"
                }
                dialog.show()
            }
        }

    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        Log.d("updated pref.", p1!!)
        if (p1 == "machinestatusarray") {
            var str = p0?.getString(p1!!, "")!!
            Log.d("supervisor update", str)
        }

    }
}
