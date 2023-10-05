package com.example.widget_and_mqtt_test

import org.json.JSONArray
import org.json.JSONObject

val userlist = JSONArray(
    "[" +
            "{\"username\":\"sup01\",\"password\":\"asdf\",\"type\":\"supervisor\"}," +
            "{\"username\":\"main01\",\"password\":\"asdf\",\"type\":\"maintenance\"}," +
            "{\"username\":\"main02\",\"password\":\"asdf\",\"type\":\"maintenance\"}" +
            "]"
)

val machinestatuslist = JSONArray(
    "[" +
            "{\"line\":\"PDC_05\",\"machine\":\"Carbon Brush tightening (+)ve\",\"status\":\"assigned for maintenance\",\"assigned to\":\"engineer 1\",\"reason\":\"\"}," +
            "{\"line\":\"PDC_03\",\"machine\":\"No load checking\",\"status\":\"sent for maintenance\",\"assigned to\":\"\",\"reason\":\"\"}," +
            "{\"line\":\"PDC_03\",\"machine\":\"Visual inspection\",\"status\":\"sent for maintenance\",\"assigned to\":\"\",\"reason\":\"\"}" +
            "]"
)
val linestatuslist = JSONArray(
    "[" +
            "{\"type\":0,\"line\":\"PDC_01\",\"status\":\"RUNNING\",\"department\":\"dept1\",\"breakdownstarttime\":\"\"}," +
            "{\"type\":0,\"line\":\"PDC_02\",\"status\":\"RUNNING\",\"department\":\"dept1\",\"breakdownstarttime\":\"\"}," +
            "{\"type\":0,\"line\":\"PDC_03\",\"status\":\"IN MAINTENANCE\",\"department\":\"dept1\",\"breakdownstarttime\":\"28-03-202314:46:00\"}," +
            "{\"type\":0,\"line\":\"PDC_04\",\"status\":\"RUNNING\",\"department\":\"dept1\",\"breakdownstarttime\":\"\"}," +
            "{\"type\":0,\"line\":\"PDC_05\",\"status\":\"IN MAINTENANCE\",\"department\":\"dept1\",\"breakdownstarttime\":\"28-03-202313:46:50\"}," +
            "{\"type\":0,\"line\":\"PDC_06\",\"status\":\"DOWNTIME\",\"department\":\"dept1\",\"breakdownstarttime\":\"\"}" +
            "]"
)

var machine_and_reason_name = JSONObject()

