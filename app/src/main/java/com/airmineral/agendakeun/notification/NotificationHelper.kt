package com.airmineral.agendakeun.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.ui.MainActivity
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

fun sendNotification(context: Context, messageTitle: String?, messageBody: String?) {
    val intent = Intent(context, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    val channelId = context.getString(R.string.default_notification_channel_id)
    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_logo_check)
        .setContentTitle(messageTitle)
        .setContentText(messageBody)
        .setAutoCancel(true)
        .setSound(defaultSoundUri)
        .setContentIntent(pendingIntent)
        .setStyle(NotificationCompat.BigTextStyle())
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Event-Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.enableLights(true)
        channel.lightColor = Color.RED
        notificationManager.createNotificationChannel(channel)
    }
    notificationManager.notify(0, notificationBuilder.build())
}