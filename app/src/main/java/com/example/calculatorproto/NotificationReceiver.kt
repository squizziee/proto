package com.example.calculatorproto
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import kotlin.random.Random

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {

//        val channelId = "scheduled_proto_notification_channel"
//        val notificationManager =
//            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        val channel = NotificationChannel(
//            channelId,
//            "Scheduled Proto Notifications",
//            NotificationManager.IMPORTANCE_DEFAULT
//        )
//        notificationManager.createNotificationChannel(channel)
//
//        val notificationIntent = Intent(context, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(
//            context,
//            0,
//            notificationIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val notification = NotificationCompat.Builder(context, channelId)
//            .setContentTitle("Come on, man")
//            .setContentText("Let's get after them numbers")
//            .setSmallIcon(R.drawable.ic_launcher_proto_foreground)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//            .build()
//
//        // Show the notification
//        notificationManager.notify(Random(1).nextInt(), notification)
        println("LMAO WHAT")
    }
}