package com.airmineral.agendakeun.notification

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

lateinit var requestQueue: RequestQueue

private const val TAG = "NotificationHelper"
private const val FCM_API = "https://fcm.googleapis.com/fcm/send"
private const val serverKey =
    "key=" + "AAAAKvbv8_0:APA91bFg4m_nlQh15tdXjXCdhH5rO1_yhYoHUAwMVZp98OQVF7bfcOZY42EMLQhIljkIlN_-OsmTWQV842kHbeKCzP7KTvH0QNt0aC3LH-HKJ1nbj7xwqERjo630IbxbOLxQxa0iJ1eA"
private const val contentType = "application/json"

fun sendNotificationToServer(context: Context, topic: String, title: String, message: String) {
    val notification = JSONObject()
    val notificationBody = JSONObject()

    try {
        notificationBody.put("title", title)
        notificationBody.put("message", message)
        notification.put("to", "/topics/$topic")
        notification.put("data", notificationBody)
        Log.e(TAG, "try")
    } catch (e: JSONException) {
        Log.e(TAG, "onCreate: " + e.message)
    }

    Log.d(TAG, "send notification for $topic")
    sendFCM(context, notification)
}

private fun sendFCM(context: Context, notification: JSONObject) {
    Log.e(TAG, "sendNotification")
    val jsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
        Response.Listener<JSONObject> { response ->
            Log.i(TAG, "onResponse: $response")
        },
        Response.ErrorListener {
            Toast.makeText(context, "Sending request error", Toast.LENGTH_LONG).show()
            Log.i(TAG, "onErrorResponse: Didn't work ${it.networkResponse.statusCode}")
        }) {

        override fun getHeaders(): Map<String, String> {
            val params = HashMap<String, String>()
            params["authorization"] = serverKey
            params["content-type"] = contentType
            return params
        }
    }
    requestQueue = Volley.newRequestQueue(context.applicationContext)
    requestQueue.add(jsonObjectRequest)
}


