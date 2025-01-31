package com.example.calculatorproto.services
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.calculatorproto.NotificationTextGenerator
import com.example.calculatorproto.R
import com.example.calculatorproto.misc.NotificationReceiver
import java.util.Calendar
import java.util.Random

class NotificationService {

    private val generator = NotificationTextGenerator()

    @RequiresApi(Build.VERSION_CODES.S)
    fun scheduleNotification(context: Context, timeInSeconds: Int, title: String, text: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra("title", title)
        intent.putExtra("text", text)

        //context.sendBroadcast(intent)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.SECOND, timeInSeconds)
        println(calendar.time)

        if (alarmManager.canScheduleExactAlarms()) {
            val deadline = System.currentTimeMillis() + timeInSeconds * 1000
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                deadline,
                pendingIntent
            )

            println(deadline)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun scheduleNotificationSet(context: Context) {
        println(context.getString(R.string.notification_interval).toInt())
        scheduleNotification(
            context,
            context.getString(R.string.notification_interval).toInt(),
            generator.generateTitle(),
            generator.generateText())
    }

}