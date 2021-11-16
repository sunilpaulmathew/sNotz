package com.sunilpaulmathew.snotz.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.receivers.ReminderReceiver;

import java.util.Calendar;

import in.sunilpaulmathew.sCommon.Utils.sUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 02, 2021
 */
public class sNotzReminders {

    private static double mYear = -1, mMonth = -1, mDay = -1;

    public static int getNotificationID(Context context) {
        return sUtils.getInt("notificationID", 0, context);
    }

    private static double getYear() {
        return mYear;
    }

    private static double getMonth() {
        return mMonth;
    }

    private static double getDay() {
        return mDay;
    }

    public static void setYear(double year) {
        mYear = year;
    }

    public static void setMonth(double month) {
        mMonth = month;
    }

    public static void setDay(double day) {
        mDay = day;
    }

    private static void setReminder(double year, double month, double day, int hour, int min,
                                   int noteID, String note, Context context) {
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent mIntent = new Intent(context, ReminderReceiver.class);
        mIntent.putExtra("note", note);
        mIntent.putExtra("id", noteID);

        int mNotificationID = getNotificationID(context);

        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(context, mNotificationID, mIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, (int) year);
        mCalendar.set(Calendar.MONTH, (int) month);
        mCalendar.set(Calendar.DAY_OF_MONTH, (int) day);
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.MINUTE, min);
        mCalendar.set(Calendar.SECOND, 0);

        mIntent.putExtra("id", mNotificationID);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), mPendingIntent);
        } else {
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), mPendingIntent);
        }

        sUtils.saveInt("notificationID", mNotificationID + 1, context);
        new MaterialAlertDialogBuilder(context)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setMessage(context.getString(R.string.reminder_message, Common.getAdjustedTime(year, month, day, hour, min)))
                .setCancelable(false)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                }).show();
    }

    private static DatePickerDialog launchDatePicker(String note, int noteID, Context context) {
        Calendar mCalendar = Calendar.getInstance();
        return new DatePickerDialog(context,
                (view, year, month, dayOfMonth) -> {
                    if (view.isShown()) {
                        setYear(year);
                        setMonth(month);
                        setDay(dayOfMonth);
                        if (getYear() != -1 && getMonth() != -1 && getDay() != -1) {
                            launchTimePicker(year, month, dayOfMonth, noteID, note, context).show();
                        }
                    }
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private static TimePickerDialog launchTimePicker(double year, double month, double day, int noteID,
                                                    String note, Context context) {
        Calendar mCalendar = Calendar.getInstance();
        return new TimePickerDialog(context,
                (view, hourOfDay, minute) -> {
                    if (view.isShown()) {
                        setReminder(year, month, day, hourOfDay, minute, noteID, note, context);
                    }
                }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false);
    }

    public static void launchReminderMenu(String note, int noteID, Context context) {
        if (sUtils.getBoolean("first_reminder", true, context)) {
            new MaterialAlertDialogBuilder(context)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(R.string.warning)
                    .setMessage(context.getString(R.string.reminder_warning))
                    .setCancelable(false)
                    .setPositiveButton(R.string.go_ahead, (dialogInterface, i) -> {
                        launchDatePicker(note, noteID, context).show();
                        sUtils.saveBoolean("first_reminder", false, context);
                    }).show();
        } else {
            launchDatePicker(note, noteID, context).show();
        }
    }

}