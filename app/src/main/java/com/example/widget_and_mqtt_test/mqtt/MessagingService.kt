package com.example.widget_and_mqtt_test

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import org.eclipse.paho.client.mqttv3.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.log


//var mqttClient : MQTTClient?=null
class MessagingService : Service(), SharedPreferences.OnSharedPreferenceChangeListener {

    public lateinit var mqttClient: MQTTClient
    private var CHANNEL_ID = "Ak.app.FS.notification"
    lateinit var NF: NotificationManager
    lateinit var sharedPreferences: SharedPreferences

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //check for internet connectivity
        if (!isConnected()) {
            Log.d("connection status:::", "Internet connection NOT available")

        } else {
            Log.d("connection status:::", "Internet connection available")
        }

        //setup shared preferences for data storage and event handling
        sharedPreferences = getSharedPreferences("FS", Context.MODE_PRIVATE)
        var myEdit = sharedPreferences.edit()


        //start MQTT connection
        // values for connection variables are defined in com.example.watchos_notification_test.MQTTConstants
        mqttClient = MQTTClient(this, MQTT_SERVER_URI, MqttClient.generateClientId() + "")

        //Action Listener and Callback implemented as inner class for .connect
        val service = Executors.newSingleThreadScheduledExecutor()
        val handler = Handler(Looper.getMainLooper())
        mqttClient.connect(
            MQTT_USERNAME,
            MQTT_PWD,
            object : IMqttActionListener {
                //defines action to be taken if connection is a success
                // in this case try to subscribe to the topic defined in MQTTConstants.MQTT_TEST_TOPIC
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(this.javaClass.name, "Connection success")
                    var topic = MQTT_TEST_TOPIC
                    if (mqttClient.isConnected()) {
                        //subscribe to topic
                        mqttClient.subscribe(MQTT_TEST_TOPIC,
                            0,
                            object : IMqttActionListener {
                                // action taken if successfully subscribed to topic
                                override fun onSuccess(asyncActionToken: IMqttToken?) {
                                    val msg = "Subscribed to: $topic"
                                    Log.d(this.javaClass.name, msg)

                                }

                                // action taken on subscription failure
                                override fun onFailure(
                                    asyncActionToken: IMqttToken?,
                                    exception: Throwable?
                                ) {
                                    Log.d(
                                        this.javaClass.name,
                                        "Failed to subscribe: $topic"
                                    )
                                }
                            })

                        mqttClient.subscribe(MQTT_TEST_TOPIC2,
                            0,
                            object : IMqttActionListener {
                                // action taken if successfully subscribed to topic
                                override fun onSuccess(asyncActionToken: IMqttToken?) {
                                    val msg = "Subscribed to: $MQTT_TEST_TOPIC2"
                                    Log.d(this.javaClass.name, msg)

                                }

                                // action taken on subscription failure
                                override fun onFailure(
                                    asyncActionToken: IMqttToken?,
                                    exception: Throwable?
                                ) {
                                    Log.d(
                                        this.javaClass.name,
                                        "Failed to subscribe: $topic"
                                    )
                                }
                            })
                    } else {
                        Log.d(
                            this.javaClass.name,
                            "Impossible to subscribe, no server connected"
                        )
                    }
                }

                //defines action to be taken if connection is a failure
                // in this case do nothing but log failure to connect
                override fun onFailure(
                    asyncActionToken: IMqttToken?, exception: Throwable?
                ) {
                    Log.d(
                        this.javaClass.name,
                        "Connection failure: ${exception.toString()}"
                    )
                }
            },

            object : MqttCallback {
                // function that runs when MSG arrives
                override fun messageArrived(topic: String?, message: MqttMessage?) {

                    Log.d("message arrived::: ", "Topic: "+topic!!+"\n\nmessage: "+message.toString())
                    // data coming from monitoring service
                    if (topic!! == MQTT_TEST_TOPIC) {
                        var msg = message.toString()
                        var jsa = JSONArray(msg)
                        Log.d("service msg chk1",jsa.toString())
                        for (i in 0..jsa.length()-1) {
                            Log.d("array input test:::", jsa[i].toString())
                        }
                        var tmplinestatus = convertJSONArrayToAppFormat(jsa)
                        Log.d("service msg chk2", tmplinestatus.toString())
//                        if(sharedPreferences.contains("linestatusarray")){
//                            myEdit.remove("linestatusarray").apply()
//                        }
                        myEdit.putString("linestatusarray",tmplinestatus.toString()).apply()

                        Log.d("shared preference data", sharedPreferences.all.toString())
//                                val intent = Intent(baseContext, MainActivity::class.java)
//                                intent.putExtra("message", message.toString())
//
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                                var pendingIntent: PendingIntent =
//                                    PendingIntent.getActivity(
//                                        baseContext,
//                                        0,
//                                        intent,
//                                        PendingIntent.FLAG_UPDATE_CURRENT,
//                                    )
//
//                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
//                                    pendingIntent =
//                                        PendingIntent.getActivity(
//                                            baseContext,
//                                            0,
//                                            intent,
//                                            PendingIntent.FLAG_IMMUTABLE,
//                                        )
//                                } else {
//                                    pendingIntent =
//                                        PendingIntent.getActivity(
//                                            baseContext,
//                                            0,
//                                            intent,
//                                            PendingIntent.FLAG_UPDATE_CURRENT,
//                                        )
//                                }
//
//
////                        createNotificationChannel()
//                                var builder =
//                                    NotificationCompat.Builder(
//                                        this@MessagingService,
//                                        CHANNEL_ID
//                                    )
//                                        .setSmallIcon(R.mipmap.notification_icon)
//                                        .setContentTitle("My notification")
//                                        .setContentText(msg)
//                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                                        .setContentIntent(pendingIntent)
//                                        .setAutoCancel(true)
//                                NF.notify(1, builder.build())
////                            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
//                            }
//                        }
                    }
                    // data originating from app instance
                    else if (topic!! == MQTT_TEST_TOPIC2) {
                        var msg = message.toString()
                        var jobj = JSONObject(msg)
                        if(jobj.getInt("type")==0){
                            myEdit.putString("lineupdate",jobj.toString()).apply()
                        }
                        else if(jobj.getInt("type")==1){
                            myEdit.putString("machineupdate",jobj.toString()).apply()
                        }

                    }
                }

                override fun connectionLost(cause: Throwable?) {
                    Log.d(this.javaClass.name, "Connection lost ${cause.toString()}")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d(this.javaClass.name, "Delivery complete")
                }
            }
        )


//        }
        if (mqttClient.isConnected()) {
            Log.d("MQTT stat::::", mqttClient.isConnected().toString())
//            mqttClient1=mqttClient
        }


        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        return START_STICKY
    }

    // function to check if device has internet connectivity in any form
    //uses some deprecated calls
    private fun isConnected(): Boolean {
        var result = false
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
                    ConnectivityManager.TYPE_VPN,
                    -> true

                    else -> false
                }
            }
        }
        return result
    }


    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "AKFSnotificationtest"
            val descriptionText = "test channel for notification by Ak for FS"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            NF =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            NF.createNotificationChannel(channel)
        }
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        if (p1.equals("publish")) {
            var str = p0?.getString(p1, "")
            if (str != null) {
                //publish to APP channel
                //no processing required
                mqttClient.publish(
                    MQTT_TEST_TOPIC2,
                    str,
                    0,
                    false,
                    object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            val msg =
                                "Publish message: ${str} to topic: $MQTT_TEST_TOPIC2"
                            Log.d(this.javaClass.name, msg)

                        }

                        override fun onFailure(
                            asyncActionToken: IMqttToken?,
                            exception: Throwable?
                        ) {
                            Log.d(this.javaClass.name, "Failed to publish message to $MQTT_TEST_TOPIC2")
                        }
                    })
            }


        }
        else if(p1.equals("publishtoService")){
            var str = p0?.getString(p1, "")
            if (str != null) {
                var tmpmasg = convertJSONArrayToServiceFormat(JSONArray(str))
                str=tmpmasg.toString()
                mqttClient.publish(
                    MQTT_TEST_TOPIC,
                    str,
                    0,
                    false,
                    object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            val msg =
                                "Publish message: ${str} to topic: $MQTT_TEST_TOPIC"
                            Log.d(this.javaClass.name, msg)

                            //                                            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                        }

                        override fun onFailure(
                            asyncActionToken: IMqttToken?,
                            exception: Throwable?
                        ) {
                            Log.d(this.javaClass.name, "Failed to publish message to $MQTT_TEST_TOPIC")
                        }
                    })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
//        mqttClient.disconnect()
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences("FS", Context.MODE_PRIVATE)
        var myEdit = sharedPreferences.edit()
        //set notification channel
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "AKFSnotificationtest",
                NotificationManager.IMPORTANCE_HIGH
            )
            NF = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            NF.createNotificationChannel(channel)
            val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("A service is running in the background")
                .setContentText("").build()
            startForeground(1, notification)
        }
    }

    fun convertJSONArrayToAppFormat(array : JSONArray):JSONArray{
        var resultarray = JSONArray()
        for(i in 0..array.length()-1){
            var lineinfo = array.getJSONObject(i)
            lineinfo.put("line",array.getJSONObject(i).getString("machine_name"))
            lineinfo.put("department","dept1")
            lineinfo.put("type",0)
            if(array.getJSONObject(i).getString("mahine_status")=="0"){
                lineinfo.put("status","RUNNING")
            }
            else if(array.getJSONObject(i).getString("mahine_status")=="1"){
                lineinfo.put("status","IDLE")
            }
            else if(array.getJSONObject(i).getString("mahine_status")=="2"){
                lineinfo.put("status","DOWNTIME")
            }
            else if(array.getJSONObject(i).getString("mahine_status")=="3"){
                lineinfo.put("status","IN MAINTENANCE")
            }
            resultarray.put(lineinfo)
        }
        return resultarray
    }
    fun convertJSONArrayToServiceFormat(array : JSONArray):JSONArray{
        var resultarray = JSONArray()
        for(i in 0..array.length()-1){
            var lineinfo = array.getJSONObject(i)
            lineinfo.remove("line")
            lineinfo.remove("department")
            lineinfo.remove("type")
            if(array.getJSONObject(i).getString("status")=="RUNNING"){
                lineinfo.put("mahine_status","0")
            }
            else if(array.getJSONObject(i).getString("status")=="IDLE"){
                lineinfo.put("mahine_status","1")
            }
            else if(array.getJSONObject(i).getString("status")=="DOWNTIME"){
                lineinfo.put("mahine_status","2")
            }
            else if(array.getJSONObject(i).getString("status")=="IN MAINTENANCE"){
                lineinfo.put("mahine_status","3")
            }
            else if(array.getJSONObject(i).getString("status")=="MAINTENANCE COMPLETE"){
                lineinfo.put("mahine_status","1")
            }
            resultarray.put(lineinfo)
        }
        return resultarray
    }

}