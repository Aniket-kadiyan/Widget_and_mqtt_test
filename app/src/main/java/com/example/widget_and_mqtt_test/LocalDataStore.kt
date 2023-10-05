package com.example.widget_and_mqtt_test

import org.json.JSONArray

val userlist = JSONArray(
    "[" +
            "{\"username\":\"sup01\",\"password\":\"asdf\",\"type\":\"supervisor\"}," +
            "{\"username\":\"main01\",\"password\":\"asdf\",\"type\":\"maintenance\"}," +
            "{\"username\":\"main02\",\"password\":\"asdf\",\"type\":\"maintenance\"}" +
            "]"
)

val machinestatuslist = JSONArray(
    "[" +
            "{\"line\":\"LINE 5\",\"machine\":\"MACHINE 3\",\"status\":\"assigned for maintenance\",\"assigned to\":\"engineer 1\",\"reason\":\"\"}," +
            "{\"line\":\"LINE 3\",\"machine\":\"MACHINE 6\",\"status\":\"sent for maintenance\",\"assigned to\":\"\",\"reason\":\"\"}," +
            "{\"line\":\"LINE 3\",\"machine\":\"MACHINE 2\",\"status\":\"sent for maintenance\",\"assigned to\":\"\",\"reason\":\"\"}" +
            "]"
)
val linestatuslist = JSONArray(
    "[" +
            "{\"type\":0,\"line\":\"LINE 1\",\"status\":\"RUNNING\",\"department\":\"dept1\",\"breakdownstarttime\":\"\"}," +
            "{\"type\":0,\"line\":\"LINE 2\",\"status\":\"RUNNING\",\"department\":\"dept1\",\"breakdownstarttime\":\"\"}," +
            "{\"type\":0,\"line\":\"LINE 3\",\"status\":\"IN MAINTENANCE\",\"department\":\"dept1\",\"breakdownstarttime\":\"28-03-202314:46:00\"}," +
            "{\"type\":0,\"line\":\"LINE 4\",\"status\":\"RUNNING\",\"department\":\"dept1\",\"breakdownstarttime\":\"\"}," +
            "{\"type\":0,\"line\":\"LINE 5\",\"status\":\"IN MAINTENANCE\",\"department\":\"dept1\",\"breakdownstarttime\":\"28-03-202313:46:50\"}," +
            "{\"type\":0,\"line\":\"LINE 6\",\"status\":\"DOWNTIME\",\"department\":\"dept1\",\"breakdownstarttime\":\"\"}" +
            "]"
)
