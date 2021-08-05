package com.example.wgutscheduler.Utilities

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.wgutscheduler.Activity.MainPage
import com.example.wgutscheduler.R


class Notifications : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alertTitle = intent.getStringExtra("title")
        val alertText = intent.getStringExtra("text")
        val nextAlertID = intent.getIntExtra("nextAlertID", getAndIncrementNextAlertID(context))

        //Notification Channel
        createNotificationChannel(context, Channel_ID)

        //Notification tap Action
        val destination = Intent(context, MainPage::class.java)
        destination.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivities(context, 0, arrayOf(destination), 0)

        //Notification Builder
        val builder = NotificationCompat.Builder(context, Channel_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground1)
                .setContentTitle(alertTitle)
                .setContentText(alertText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(nextAlertID, builder.build())
    }

    private fun createNotificationChannel(context: Context, Channel_ID: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "channel"
            val description = "channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Channel_ID, name, importance)
            channel.description = description
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val alertFile = "alertFile"
        const val termAlertFile = "termAlertFile"
        const val courseAlertFile = "courseAlertFile"
        const val nextAlert = "nextAlertID"
        const val Channel_ID = "WGU 196"
        fun setAlert(context: Context, ID: Int, time: Long, title: String?, text: String?): Boolean {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val nextAlertID = getNextAlertID(context)
            val intentAlert = Intent(context, Notifications::class.java)
            intentAlert.putExtra("ID", ID)
            intentAlert.putExtra("title", title)
            intentAlert.putExtra("text", text)
            intentAlert.putExtra("nextAlertID", nextAlertID)
            val pendingIntent = PendingIntent.getBroadcast(context, nextAlertID, intentAlert, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager[AlarmManager.RTC_WAKEUP, time] = pendingIntent
            val sp = context.getSharedPreferences(alertFile, Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putInt(ID.toString(), nextAlertID)
            editor.apply()
            incrementNextAlertID(context)
            return true
        }

        fun setCourseAlert(context: Context, ID: Int, time: Long, title: String?, text: String?) {
            setAlert(context, ID, time, title, text)
        }

        fun setAssessmentAlert(context: Context, ID: Int, time: Long, title: String?, text: String?) {
            setAlert(context, ID, time, title, text)
        }

        private fun getNextAlertID(context: Context): Int {
            val alertPrefs: SharedPreferences = context.getSharedPreferences(alertFile, Context.MODE_PRIVATE)
            return alertPrefs.getInt(nextAlert, 1)
        }

        private fun incrementNextAlertID(context: Context) {
            val alertPrefs: SharedPreferences = context.getSharedPreferences(alertFile, Context.MODE_PRIVATE)
            val nextAlertID = alertPrefs.getInt(nextAlert, 1)
            val alertEditor = alertPrefs.edit()
            alertEditor.putInt(nextAlert, nextAlertID + 1)
            alertEditor.apply()
        }

        private fun getAndIncrementNextAlertID(context: Context): Int {
            val nextAlarmID = getNextAlertID(context)
            incrementNextAlertID(context)
            return nextAlarmID
        }
    }
}