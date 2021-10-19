package com.sunilpaulmathew.snotz.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.receivers.ReminderReceiver;

import java.util.Calendar;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 02, 2021
 */
public class sNotzReminders {

    private static double mYear = -1, mMonth = -1, mDay = -1;

    public static int getNotificationID(Context context) {
        return Utils.getInt("notificationID", 0, context);
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
                                   String note, Context context) {
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent mIntent = new Intent(context, ReminderReceiver.class);
        mIntent.putExtra("note", note);

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
        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), mPendingIntent);

        Utils.saveInt("notificationID", mNotificationID + 1, context);
        new MaterialAlertDialogBuilder(context)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setMessage(context.getString(R.string.reminder_message, Common.getAdjustedTime(year, month, day, hour, min)))
                .setCancelable(false)
                .setPositiveButton(R.string.cancel, (dialogInterface, i) -> {
                }).show();
    }

    private static DatePickerDialog launchDatePicker(String note, Context context) {
        Calendar mCalendar = Calendar.getInstance();
        return new DatePickerDialog(context,
                (view, year, month, dayOfMonth) -> {
                    setYear(year);
                    setMonth(month);
                    setDay(dayOfMonth);
                    if (getYear() != -1 && getMonth() != -1 && getDay() != -1) {
                        launchTimePicker(year, month, dayOfMonth, note, context).show();
                    }
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private static TimePickerDialog launchTimePicker(double year, double month, double day,
                                                    String note, Context context) {
        Calendar mCalendar = Calendar.getInstance();
        return new TimePickerDialog(context,
                (view, hourOfDay, minute) -> setReminder(year, month,  day, hourOfDay, minute, note, context), mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false);
    }

    public static void launchReminderMenu(String note, Context context) {
        if (Utils.getBoolean("first_reminder", true, context)) {
            new MaterialAlertDialogBuilder(context)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(R.string.warning)
                    .setMessage(context.getString(R.string.reminder_warning))
                    .setCancelable(false)
                    .setPositiveButton(R.string.go_ahead, (dialogInterface, i) -> {
                        launchDatePicker(note, context).show();
                        Utils.saveBoolean("first_reminder", false, context);
                    }).show();
        } else {
            launchDatePicker(note, context).show();
        }
    }

}