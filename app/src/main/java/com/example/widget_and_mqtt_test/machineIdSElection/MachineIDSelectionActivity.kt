package com.example.widget_and_mqtt_test.machineIdSElection

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.RecyclerView
import com.example.widget_and_mqtt_test.R
import com.example.widget_and_mqtt_test.databinding.ActivityMachineIdSelectionBinding
import com.example.widget_and_mqtt_test.linestatuslist
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import org.json.JSONArray
import org.json.JSONObject

class MachineIDSelectionActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMachineIdSelectionBinding
    lateinit var linestatusarray: JSONArray
    lateinit var selectMachineButton : Button
    lateinit var lineSelectionViewModel: LineSelectionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMachineIdSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lineSelectionViewModel = ViewModelProvider(this).get(LineSelectionViewModel::class.java)

        var sharedPreferences = getSharedPreferences("FS", MODE_PRIVATE)
        var myEdit = sharedPreferences.edit()
        var flag = sharedPreferences.contains("linestatusarray")

        binding.apply {

            Log.d("spinner debug", "chk 31")
            if (flag) {
                linestatusarray =
                    JSONArray(sharedPreferences.getString("linestatusarray", ""))
            } else {
                linestatusarray = linestatuslist
            }
            Log.d("json", linestatusarray.toString())
            var linestatus: ArrayList<JSONObject> = arrayListOf()
            for (i in 0..linestatusarray.length() - 1) {
                Log.d("json", linestatusarray.getString(i))
                linestatus.add(linestatusarray.getJSONObject(i))
            }
            Log.d("adapter data", linestatus.toString())
            var adapter = IdSelectionListAdapter(linestatus, lineSelectionViewModel)
            machineSelectionRV.adapter = adapter
            lineSelectionViewModel.getSelectedLine().observe(this@MachineIDSelectionActivity) {
                var selectedline = it!!
                val dialog = BottomSheetDialog(this@MachineIDSelectionActivity)
                dialog.setCancelable(true)
                val modalView =
                    layoutInflater.inflate(R.layout.select_machine_bottom_panel_modal_layout, null)
                dialog.setContentView(modalView)
                Log.d("spinner debug", "chk 1")
//                val machineListRV = modalView.findViewById<RecyclerView>(R.id.selectMachineModalRV)
                selectMachineButton = modalView.findViewById<Button>(R.id.selectMachineButton)
                val camerascanner =
                    modalView.findViewById<FloatingActionButton>(R.id.breakdownModalCameraScannerFAB)
                val machinelistSpinner = modalView.findViewById<Spinner>(R.id.machineListSpinner)
                val machinenamelist = listOf<String>(
                    "<Select a machine>",
                    "MACHINE 1",
                    "MACHINE 2",
                    "MACHINE 3",
                    "MACHINE 4",
                    "MACHINE 5",
                    "MACHINE 6",
                    "MACHINE 7",
                    "MACHINE 8",
                    "MACHINE 9",
                    "MACHINE 10"
                )
                var arrayadapter = ArrayAdapter<String>(this@MachineIDSelectionActivity, R.layout.spinner_item_layout, machinenamelist)
                machinelistSpinner.adapter=arrayadapter
                Log.d("spinner debug", "chk 2")
                machinelistSpinner.onItemSelectedListener=this@MachineIDSelectionActivity
                var adapter1 =
                    MachineSelectionModalListAdapter(machinenamelist, lineSelectionViewModel)
//                machineListRV.adapter = adapter1
                Log.d("spinner debug", "chk 3")
                selectMachineButton.isEnabled=false

                selectMachineButton.setOnClickListener {
                    var selectedmachinename = lineSelectionViewModel.getCurrentSelectedMachine()
                    if (selectedmachinename != null) {
                        Log.d("bottom sheet selection", selectedmachinename)
                        selectedline.put("status", "IN MAINTENANCE")
                    }
                    var index = -1
                    for (item in linestatus) {
                        if (item.getString("line") == selectedline.getString("line"))
                            index = linestatus.indexOf(item)
                    }
                    linestatus.remove(linestatus.get(index))
//                    linestatus.add(selectedline)
                    linestatus.add(index, selectedline)
                    adapter = IdSelectionListAdapter(linestatus, lineSelectionViewModel)
                    machineSelectionRV.adapter = adapter
                    if(sharedPreferences.contains("linestatusarray")){
                        myEdit.remove("linestatusarray").apply()
                    }
                    myEdit.putString("linestatusarray",linestatus.toString())
                    //
                    //add machine name to shared preferences
                    //
                    var machineinfo =
                        JSONObject("{\"line\":\"line5\",\"machine\":\"machine3\",\"status\":\"sent for maintenance\",\"assigned to\":\"\",\"reason\":\"\"}")
                    machineinfo.put("line", selectedline.getString("line"))
                    machineinfo.put("machine", selectedmachinename)
                    Log.d("machine info", machineinfo.toString())
                    if(sharedPreferences.contains("machinestatusarray")){
                        var tempmachinestatuslist = JSONArray(sharedPreferences.getString("machinestatusarray",""))
                        tempmachinestatuslist.put(machineinfo)
                        myEdit.remove("machinestatusarray").apply()
                        myEdit.putString("machinestatusarray",tempmachinestatuslist.toString()).apply()
                    }
                    else{
                        myEdit.putString("addmachineienfo",machineinfo.toString())
                    }
                    dialog.dismiss()
                }

                camerascanner.setOnClickListener {
                    val options = GmsBarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_EAN_13).build()
                    val scanner =
                        GmsBarcodeScanning.getClient(this@MachineIDSelectionActivity, options)
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
                            machineinfo.put("line", selectedline.getString("line"))
                            machineinfo.put("machine", jobj.getString("machine name"))
                            if(sharedPreferences.contains("machinestatusarray")){
                                var tempmachinestatuslist = JSONArray(sharedPreferences.getString("machinestatusarray",""))
                                tempmachinestatuslist.put(machineinfo)
                                myEdit.remove("machinestatusarray").apply()
                                myEdit.putString("machinestatusarray",tempmachinestatuslist.toString()).apply()
                            }
                            else{
                                myEdit.putString("addmachineienfo",machineinfo.toString())
                            }

                            var index = -1
                            for (item in linestatus) {
                                if (item.getString("line") == machineinfo.getString("line"))
                                    index = linestatus.indexOf(item)
                            }
                            var templine = linestatus.get(index)
                            templine.put("status","IN MAINTENANCE")
                            linestatus.remove(linestatus.get(index))
//                    linestatus.add(selectedline)
                            linestatus.add(index, templine)
                            adapter = IdSelectionListAdapter(linestatus, lineSelectionViewModel)
                            machineSelectionRV.adapter = adapter
                            if(sharedPreferences.contains("linestatusarray")){
                                myEdit.remove("linestatusarray").apply()
                            }
                            myEdit.putString("linestatusarray",linestatus.toString())

                            dialog.dismiss()
                            Toast.makeText(this@MachineIDSelectionActivity,"SENT MAINTENANCE REQUEST FOR:\n"+jobj.getString("line").uppercase()+"\n"+jobj.getString("machine name").uppercase(), Toast.LENGTH_SHORT).show()


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


                dialog.show()
            }

        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        Log.d("spinner", p0!!.getItemAtPosition(p2).toString()+"\n"+p2)
        var item = p0!!.getItemAtPosition(p2).toString()
        if(item!="<Select a machine>"){
            lineSelectionViewModel.setSelectedMachine(item)
            selectMachineButton.isEnabled=true
        }
        else{
            selectMachineButton.isEnabled=false
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        selectMachineButton.isEnabled=false
    }


}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T) {
            observer.onChanged(t)
            removeObserver(this)
        }

    })
}

fun <T> LiveData<T>.observeRepeat(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T) {
            observer.onChanged(t)
//            removeObserver(this)
        }

    })
}