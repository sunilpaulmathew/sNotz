package com.sunilpaulmathew.snotz.activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.receivers.ReminderReceiver;
import com.sunilpaulmathew.snotz.utils.Common;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzReminders;

import java.util.Calendar;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 01, 2021
 */
public class ReminderActivity extends AppCompatActivity {

    private int mNotificationID, mHour, mMin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        AppCompatImageButton mBack = findViewById(R.id.back_button);
        MaterialCardView mCancel = findViewById(R.id.cancel);
        MaterialCardView mSet = findViewById(R.id.set);
        TimePicker mTimePicker = findViewById(R.id.timePicker);

        mNotificationID = sNotzReminders.getNotificationID(this);

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent mIntent = new Intent(this, ReminderReceiver.class);

        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(this, mNotificationID, mIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        mSet.setOnClickListener(v -> {
            mHour = mTimePicker.getHour();
            mMin = mTimePicker.getMinute();
            Calendar mCalendar = Calendar.getInstance();
            mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
            mCalendar.set(Calendar.MINUTE, mMin);
            mCalendar.set(Calendar.SECOND, 0);
            mIntent.putExtra("id", mNotificationID);
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), mPendingIntent);

            if (Common.getID() != -1) {
                sNotzReminders.edit(Common.getNote(), mHour, mMin,Common.getID(), this);
            } else if (Utils.exist(getCacheDir().getPath() + "/reminders")) {
                sNotzReminders.add(Common.getNote(), mHour, mMin, mNotificationID,this);
            } else {
                sNotzReminders.initialize(Common.getNote(), mHour, mMin, mNotificationID, this);
            }
            Utils.saveInt("notificationID", mNotificationID + 1, this);
            new MaterialAlertDialogBuilder(this)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(R.string.app_name)
                    .setMessage(getString(R.string.reminder_message, Common.getAdjustedTime(mHour, mMin)))
                    .setCancelable(false)
                    .setPositiveButton(R.string.cancel, (dialogInterface, i) -> {
                        finish();
                    }).show();
        });

        mBack.setOnClickListener(v -> finish());
        mCancel.setOnClickListener(v -> {
            mAlarmManager.cancel(mPendingIntent);
            finish();
        });
    }

}