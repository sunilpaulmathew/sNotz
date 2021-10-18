package com.sunilpaulmathew.snotz.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.activities.ReadNoteActivity;
import com.sunilpaulmathew.snotz.utils.Common;
import com.sunilpaulmathew.snotz.utils.sNotzReminders;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 01, 2021
 */
public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        String mNote = bundle.getString("note");

        Uri mAlarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        int mNotificationID = sNotzReminders.getNotificationID(context);

        Intent mIntent = new Intent(context, ReadNoteActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder mTaskStackBuilder = TaskStackBuilder.create(context);
        mTaskStackBuilder.addParentStack(ReadNoteActivity.class);
        mTaskStackBuilder.addNextIntent(mIntent);
        
        PendingIntent mPendingIntent = mTaskStackBuilder.getPendingIntent(mNotificationID, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationChannel mNotificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mNotificationChannel = new NotificationChannel("channel", context.getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
        }

        Common.setReadModeText(mNote);
        Common.setReadModeImage(null);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "channel");
        Notification mNotification = mBuilder.setContentTitle(context.getString(R.string.app_name))
                .setContentText(mNote)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setSound(mAlarmSound)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentIntent(mPendingIntent)
                .build();
        
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(mNotificationChannel);
        }
        try {
            notificationManager.notify(mNotificationID, mNotification);
        } catch (NullPointerException ignored) {}
    }
    
}