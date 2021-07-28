package com.example.wgutscheduler.Utilities;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.wgutscheduler.Activity.MainPage;
import com.example.wgutscheduler.R;

public class Notifications extends BroadcastReceiver {
    public static final String alertFile = "alertFile";
    public static final String termAlertFile = "termAlertFile";
    public static final String courseAlertFile = "courseAlertFile";
    public static final String nextAlert = "nextAlertID";
    public static final String Channel_ID = "WGU 196";

    public static boolean setAlert(Context context, int ID, long time, String title, String text) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int nextAlertID = getNextAlertID(context);
        Intent intentAlert = new Intent(context, Notifications.class);
        intentAlert.putExtra("ID", ID);
        intentAlert.putExtra("title", title);
        intentAlert.putExtra("text", text);
        intentAlert.putExtra("nextAlertID", nextAlertID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, nextAlertID, intentAlert, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);

        SharedPreferences sp = context.getSharedPreferences(alertFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Integer.toString(ID), nextAlertID);
        editor.apply();

        incrementNextAlertID(context);
        return true;
    }

    public static void setCourseAlert(Context context, int ID, long time, String title, String text) {
        setAlert(context, ID, time, title, text);
    }

    public static void setAssessmentAlert(Context context, int ID, long time, String title, String text) {
        setAlert(context, ID, time, title, text);
    }

    private static int getNextAlertID(Context context) {
        SharedPreferences alertPrefs;
        alertPrefs = context.getSharedPreferences(alertFile, Context.MODE_PRIVATE);
        int nextAlertID = alertPrefs.getInt(nextAlert, 1);
        return nextAlertID;
    }

    private static void incrementNextAlertID(Context context) {
        SharedPreferences alertPrefs;
        alertPrefs = context.getSharedPreferences(alertFile, Context.MODE_PRIVATE);
        int nextAlertID = alertPrefs.getInt(nextAlert, 1);
        SharedPreferences.Editor alertEditor = alertPrefs.edit();
        alertEditor.putInt(nextAlert, nextAlertID + 1);
        alertEditor.commit();
    }

    private static int getAndIncrementNextAlertID(Context context) {
        int nextAlarmID = getNextAlertID(context);
        incrementNextAlertID(context);
        return nextAlarmID;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String alertTitle = intent.getStringExtra("title");
        String alertText = intent.getStringExtra("text");
        int nextAlertID = intent.getIntExtra("nextAlertID", getAndIncrementNextAlertID(context));

        //Notification Channel
        createNotificationChannel(context, Channel_ID);

        //Notification tap Action
        Intent destination = new Intent(context, MainPage.class);
        destination.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivities(context, 0, new Intent[]{destination}, 0);

        //Notification Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Channel_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground1)
                .setContentTitle(alertTitle)
                .setContentText(alertText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(nextAlertID, builder.build());
    }

    private void createNotificationChannel(Context context, String Channel_ID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Channel_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
