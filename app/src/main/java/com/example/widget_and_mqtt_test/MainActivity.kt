package com.example.widget_and_mqtt_test

import android.app.ActivityManager
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.widget_and_mqtt_test.databinding.ActivityLoginBinding
import com.example.widget_and_mqtt_test.machineIdSElection.MachineIDSelectionActivity
import com.example.widget_and_mqtt_test.maintenanceInfoScreen.MaintenanceInfoScreen
import org.json.JSONArray

class MainActivity : ComponentActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!isConnected()) {
            Log.d("connection status:::", "Internet connection NOT available")
//            Toast.makeText(applicationContext, "Internet connection NOT available", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("connection status:::", "Internet connection available")
//            Toast.makeText(applicationContext, "Internet connection available", Toast.LENGTH_SHORT).show()
        }
        if (isMyServiceRunning(MessagingService::class.java) < 1) {
            try {
                startService(Intent(this, MessagingService::class.java))
                Toast.makeText(
                    this,
                    "chk 1\n" + getSystemService(MessagingService::class.java).toString(),
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                try {
                    startForegroundService(Intent(this, MessagingService::class.java))
                    Toast.makeText(
                        this,
                        "chk 2\n" + getSystemService(MessagingService::class.java).toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                e.printStackTrace()
            }
        }
        Toast.makeText(
            this,
            isMyServiceRunning(MessagingService::class.java).toString(),
            Toast.LENGTH_SHORT
        ).show()

        var sharedPreferences = getSharedPreferences("FS", MODE_PRIVATE)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        var myEdit = sharedPreferences.edit()
        var flag = sharedPreferences.contains("FSdata")
        if (!sharedPreferences.contains("addmachineienfo")) {
            myEdit.putString("addmachineinfo", "{}").apply()
        }
        if (!sharedPreferences.contains("machineupdate")) {
            myEdit.putString("machineupdate", "{}").apply()
        }
        if (!sharedPreferences.contains("lineupdate")) {
            myEdit.putString("lineupdate", "{}").apply()
        }
        if (!sharedPreferences.contains("linestatusarray")) {
            myEdit.putString("linestatusarray", linestatuslist.toString()).apply()
        }
        if (!sharedPreferences.contains("machinestatusarray")) {
            myEdit.putString("machinestatusarray", machinestatuslist.toString()).apply()
        }
        if (!sharedPreferences.contains("publish")) {
            myEdit.putString("publish", "{}").apply()
        }
        if (!sharedPreferences.contains("publishtoService")) {
            myEdit.putString("publishtoService", "{}").apply()
        }

        machine_and_reason_name.put(
            "Terminal pressing",
            JSONArray("[\"Bracket Crack\",\"Terminal Broken\",\"Low load\",\"High load\",\"Terminal Gap\"]")
        )
        machine_and_reason_name.put(
            "Oil seal pressing",
            JSONArray("[\"Bracket Blow hole\",\"Bracket casting defect\",\"front Bracket dent\",\"Bracket burr\"]")
        )
        machine_and_reason_name.put(
            "Ball Bearing pressing",
            JSONArray("[\"High load\",\"Low load\"]")
        )
        machine_and_reason_name.put(
            "Carbon Brush tightening (-)ve",
            JSONArray("[\"Bracket M4 thread tight\",\"Bracket M4 thread free\",\"Bracket M4 thread miss\",\"Brush holder loose\"]")
        )
        machine_and_reason_name.put(
            "Carbon Brush tightening (+)ve",
            JSONArray("[\"Bracket M4 thread tight\",\"Bracket M4 thread free\",\"Bracket M4 thread miss\"]")
        )
        machine_and_reason_name.put(
            "Brush spring insertion(+,-)",
            JSONArray("[\"Brush holder broken\",\"Brush holder ID U/S\"]")
        )
        machine_and_reason_name.put(
            "Yoke unit tightening",
            JSONArray("[\"Bracket M5 thread tight\",\"Bracket M5 thread free\",\"Bracket M5 thread miss\",\"Motor jam\"]")
        )
        machine_and_reason_name.put(
            "Lead wire tightening",
            JSONArray("[\"Lead wire thread free\",\"Lead wire sleeve cut\",\"Lead wire Grommet cut\"]")
        )
        machine_and_reason_name.put(
            "Earth wire tightening",
            JSONArray("[\"Bracket M5 thread tight\",\"Bracket M5 thread free\",\"Bracket M5 thread miss\"]")
        )
        machine_and_reason_name.put(
            "Characteristic checking",
            JSONArray("[\"Low voltage\",\"High current(load)\",\"Low torque\",\"Low RPM\",\"Motor Jam\",\"Drop Down parts\"]")
        )
        machine_and_reason_name.put(
            "No load checking",
            JSONArray("[\"High current\",\"Wave Form NG\",\"Noise NG\"]")
        )
        machine_and_reason_name.put(
            "Leakage testing",
            JSONArray("[\"Leakage NG\",\"M5 screw miss\"]")
        )
        machine_and_reason_name.put(
            "Laser Prinitng",
            JSONArray("[\"Batch code shift\",\"Batch code not visible\"]")
        )
        machine_and_reason_name.put(
            "Lot no. Punching",
            JSONArray("[\"Batch code NG\",\"Bracket damage\"]")
        )
        machine_and_reason_name.put(
            "Visual inspection",
            JSONArray("[\"Fr. Bracket casting defect\",\"Front bracket blow hole\",\"Front bracket extra material\",\"Terminal Gap\",\"Fr. Bracket dent\",\"Terminal Broken\",\"Yoke unit dent\",\"Yoke unit Plating NG\",\"Yoke unit line mark\",\"Extra wire in grommet\",\"Lead wire grommet cut\",\"Lead wire sleeve cut\",\"Armature shaft dent\",\"Packing inside\",\"Packing Outside\",\"Packing miss\"]")
        )
        if (!sharedPreferences.contains("machinereasonmapping")) {
            myEdit.putString("machinereasonmapping", machine_and_reason_name.toString()).apply()
        }
        binding.apply {
//            buttonsupervisor.setOnClickListener{
//                var intent = Intent(this@MainActivity.applicationContext , MachineIDSelectionActivity::class.java)
////                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK )
//                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK)
//                applicationContext.startActivity(intent)
//            }
//            buttonmaintenance.setOnClickListener{
//                var intent = Intent(this@MainActivity.applicationContext , MaintenanceInfoScreen::class.java)
////                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK )
//                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK)
//                applicationContext.startActivity(intent)

            loginButton.setOnClickListener {
                var usernametext = usernameET.text.toString().trim()
                var passwordtext = passwordET.text.toString().trim()
                var status = "invalid username or password"
                for (i in 0..userlist.length() - 1) {
                    var user = userlist.getJSONObject(i)!!
                    if (user.getString("username") == usernametext) {
                        if (user.getString("password") == passwordtext) {
                            status = user.getString("type")
                            break
                        } else {
                            status = "invalid password"
                            break
                        }
                    }
                }
                if (status == "supervisor") {
                    var intent = Intent(
                        this@MainActivity.applicationContext,
                        MachineIDSelectionActivity::class.java
                    )
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("user", usernametext)
                    applicationContext.startActivity(intent)
                } else if (status == "maintenance") {
                    var intent = Intent(
                        this@MainActivity.applicationContext,
                        MaintenanceInfoScreen::class.java
                    )
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("user", usernametext)
                    applicationContext.startActivity(intent)
                } else {
                    Toast.makeText(this@MainActivity, status, Toast.LENGTH_SHORT).show()
                }
            }
//            }
        }

    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        Log.d("Main", "onSharedPreferenceChanged: ")
    }

    private fun isConnected(): Boolean {
        var result = false
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
            if (capabilities != null) {
                result = when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true

                    else -> false
                }
            }
        } else {
            val activeNetwork = cm.activeNetworkInfo
            if (activeNetwork != null) {
                // connected to the internet
                result = when (activeNetwork.type) {
                    ConnectivityManager.TYPE_WIFI,
                    ConnectivityManager.TYPE_MOBILE,
                    ConnectivityManager.TYPE_VPN -> true

                    else -> false
                }
            }
        }
        return result
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Int {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        var count = 0
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                count++
            }
        }
        return count
    }
}

