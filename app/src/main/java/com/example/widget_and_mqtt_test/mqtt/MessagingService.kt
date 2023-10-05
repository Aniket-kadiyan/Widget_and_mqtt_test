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
import org.json.JSONObject
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


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
        sharedPreferences = getSharedPreferences("FS",Context.MODE_PRIVATE)
        var myEdit = sharedPreferences.edit()
        var flag = sharedPreferences.contains("FSdata")
        if (!flag) {
            myEdit.putString("FSdata", "{\"data\":[]}").apply()
        }


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
                        mqttClient.subscribe(MQTT_TEST_TOPIC,
                            1,
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
                    var msg = message.toString()
                    //parse incoming string to json
                    var jsm = JSONObject(msg)
                    Log.d("receivedmsg:::", jsm.toString() +"\n\n"+msg)
                    var jsa = jsm.getJSONArray("")
                    for(i in 0..jsa.length()){
                        Log.d("array input teset:::", jsa[i].toString())
                    }
                    var jsonObject = JSONObject(msg)
                    Log.d(
                        "object:::",
                        jsonObject.getString("machine_name") + "\n\tstatus= " + jsonObject.getInt(
                            "mahine_status"
                        ) + "\n\tfailurecode= " + jsonObject.getString(
                            "breakdown_reasonid"
                        ).equals("null")
                    )

                    var machineStatus = jsonObject.getInt("mahine_status")
                    if (machineStatus == 2 && jsonObject.getString("breakdown_reasonid")
                            .equals("null")
                    ) {
                        msg =
                            "BREAKDOWN " + jsonObject.getString("machine_name") + " at " + jsonObject.getString(
                                "breakdown_start"
                            )
                        Log.d(this.javaClass.name, msg)

                        //get current stored data from shared preferences
                        var currentdata = sharedPreferences.getString("FSdata", "")
                        var jsonObject1 = JSONObject(         currentdata)
                        var jsonArray1 = jsonObject1.getJSONArray("data")

                        //add recieved object to stored data
                        var                   flag = true
                        for (i in 0..jsonArray1.length() - 1) {
                            if (jsonArray1.getJSONObject(i).toString()
                                    .equals(jsonObject.toString())
                            )
                                flag = false
                        }
                        if (flag) {
                            jsonArray1.put(jsonObject)
                            var str = jsonArray1.toString()
//                        str=str.substringBeforeLast("]")+" , "+jsonObject.toString()+"]"
                            Log.d("json array length:::", "\n\t" + jsonArray1.length())
                            jsonObject1.remove("data")
                            jsonObject1.put("data", jsonArray1)
                            Log.d(
                                "json object put back length:::",
                                "\n\t" + jsonObject1.length()
                            )
                            myEdit.putString("FSdata", jsonObject1.toString()).apply()
                            val intent = Intent(baseContext, MainActivity::class.java)
                            intent.putExtra("message", message.toString())

                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            var pendingIntent: PendingIntent =
                                PendingIntent.getActivity(
                                    baseContext,
                                    0,
                                    intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT,
                                )

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                                pendingIntent =
                                    PendingIntent.getActivity(
                                        baseContext,
                                        0,
                                        intent,
                                        PendingIntent.FLAG_IMMUTABLE,
                                    )
                            }
                            else{
                                pendingIntent =
                                    PendingIntent.getActivity(
                                        baseContext,
                                        0,
                                        intent,
                                        PendingIntent.FLAG_UPDATE_CURRENT,
                                    )
                            }


//                        createNotificationChannel()
                            var builder =
                                NotificationCompat.Builder(
                                    this@MessagingService,
                                    CHANNEL_ID
                                )
                                    .setSmallIcon(R.mipmap.notification_icon)
                                    .setContentTitle("My notification")
                                    .setContentText(msg)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true)
                            NF.notify(1, builder.build())
//                            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
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
                            Log.d(this.javaClass.name, "Failed to publish message to topic")
                        }
                    })
            }
//            applicationContext.startActivity(
//                Intent(
//                    applicationContext,
//                    MainActivity::class.java
//                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            )

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
//        mqttClient.disconnect()
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences("FS",Context.MODE_PRIVATE)
        var myEdit = sharedPreferences.edit()
        var flag = sharedPreferences.contains("FSdata")
        if (!flag) {
            myEdit.putString("FSdata", "{\"data\":[]}").apply()
        }
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

}