package com.example.calculatorproto.misc
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.calculatorproto.R
import com.example.calculatorproto.MainActivity
import com.example.calculatorproto.NotificationTextGenerator
import com.example.calculatorproto.services.NotificationService
import kotlin.random.Random

class NotificationReceiver : BroadcastReceiver() {
    private val notificationService = NotificationService()
    private val generator = NotificationTextGenerator()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context, intent: Intent?) {

        val channelId = "scheduled_proto_notification_channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Scheduled Proto Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(intent!!.getStringExtra("title"))
            .setContentText(intent.getStringExtra("text"))
            .setSmallIcon(R.drawable.ic_launcher_proto_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Show the notification
        notificationManager.notify(Random(1).nextInt(), notification)
        println("LMAO WHAT")
        notificationService.scheduleNotificationSet(context)
    }
}