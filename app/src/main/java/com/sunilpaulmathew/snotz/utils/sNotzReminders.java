package com.sunilpaulmathew.snotz.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.appcompat.widget.AppCompatImageButton;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.receivers.ReminderReceiver;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.Dialog.sSingleItemDialog;
import in.sunilpaulmathew.sCommon.FileUtils.sFileUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 02, 2021
 */
public class sNotzReminders {

    private static double mYear = -1, mMonth = -1, mDay = -1;

    public static boolean isReminderSet(int noteID, Context context) {
        for (ReminderItems items : getRawData(context)) {
            if (items.getNoteID() == noteID && items.getTime() > System.currentTimeMillis()) {
                return true;
            }
        }
        return false;
    }

    public static int getNotificationID(Context context) {
        return sCommonUtils.getInt("notificationID", 0, context);
    }

    public static File getReminders(Context context) {
        return new File(context.getFilesDir(),"reminders");
    }

    private static int getNoteID(JsonObject object) {
        return object.get("noteID").getAsInt();
    }

    private static int getNotificationID(JsonObject object) {
        return object.get("notificationID").getAsInt();
    }

    private static List<ReminderItems> getRawData(Context context) {
        List<ReminderItems> mData = new ArrayList<>();
        if (sFileUtils.exist(getReminders(context))) {
            JsonArray sNotz = Objects.requireNonNull(sNotzData.getJSONObject(sFileUtils.read(getReminders(
                    context)))).getAsJsonArray("sNotz");
            for (int i = 0; i < sNotz.size(); i++) {
                mData.add(new ReminderItems(getNoteID(sNotz.get(i).getAsJsonObject()),
                        getNotificationID(sNotz.get(i).getAsJsonObject()),
                        getTime(sNotz.get(i).getAsJsonObject()))
                );
            }
        }
        return mData;
    }

    public static long getTime(JsonObject object) {
        return object.get("time").getAsLong();
    }

    private static void setReminder(AppCompatImageButton button, boolean modify, double year, double month, double day, int hour, int min,
                                   int noteID, String note, Context context) {
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent mIntent = new Intent(context, ReminderReceiver.class);
        mIntent.putExtra("note", note);
        mIntent.putExtra("id", noteID);

        int mNotificationID = Integer.MIN_VALUE;
        if (modify) {
            for (ReminderItems items : getRawData(context)) {
                if (items.getNoteID() == noteID) {
                    mNotificationID = items.getNotificationID();
                }
            }
        } else {
            mNotificationID = getNotificationID(context);
        }

        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(context, mNotificationID, mIntent, android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, (int) year);
        mCalendar.set(Calendar.MONTH, (int) month);
        mCalendar.set(Calendar.DAY_OF_MONTH, (int) day);
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.MINUTE, min);
        mCalendar.set(Calendar.SECOND, 0);

        if (mCalendar.getTimeInMillis() < System.currentTimeMillis()) {
            sCommonUtils.toast(context.getString(R.string.reminder_invalid_message, Common.getAdjustedTime(year, month, day, hour, min)), context).show();
            return;
        }

        mIntent.putExtra("id", mNotificationID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !mAlarmManager.canScheduleExactAlarms()) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
            return;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !mAlarmManager.canScheduleExactAlarms()) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            context.startActivity(intent);
            return;
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), mPendingIntent);
        } else {
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), mPendingIntent);
        }

        if (modify) {
            editReminder(mCalendar, noteID, mNotificationID, context);
        } else if (getReminders(context).exists()) {
            addReminder(button, mCalendar, noteID, mNotificationID, context);
        } else {
            initialize(mCalendar, noteID, mNotificationID, context);
        }

        if (!modify) {
            sCommonUtils.saveInt("notificationID", mNotificationID + 1, context);
        }
        new MaterialAlertDialogBuilder(context)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setMessage(context.getString(R.string.reminder_message, Common.getAdjustedTime(year, month, day, hour, min)))
                .setCancelable(false)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                }).show();
    }

    private static DatePickerDialog launchDatePicker(AppCompatImageButton button, boolean modify, String note, int noteID, Context context) {
        Calendar mCalendar = Calendar.getInstance();
        return new DatePickerDialog(context,
                (view, year, month, dayOfMonth) -> {
                    if (view.isShown()) {
                        mYear = year;
                        mMonth = month;
                        mDay = dayOfMonth;
                        if (mYear != -1 && mMonth != -1 && mDay != -1) {
                            launchTimePicker(button, modify, year, month, dayOfMonth, noteID, note, context).show();
                        }
                    }
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private static TimePickerDialog launchTimePicker(AppCompatImageButton button, boolean modify, double year, double month, double day, int noteID,
                                                    String note, Context context) {
        Calendar mCalendar = Calendar.getInstance();
        return new TimePickerDialog(context,
                (view, hourOfDay, minute) -> {
                    if (view.isShown()) {
                        setReminder(button, modify, year, month, day, hourOfDay, minute, noteID, note, context);
                    }
                }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false);
    }

    private static void addReminder(AppCompatImageButton button, Calendar calendar, int noteID, int notificationID, Context context) {
        JsonObject mJSONObject = sNotzData.getJSONObject(sFileUtils.read(getReminders(context)));
        JsonArray mJSONArray = Objects.requireNonNull(mJSONObject).getAsJsonArray("sNotz");
        JsonObject reminders = new JsonObject();
        reminders.addProperty("noteID", noteID);
        reminders.addProperty("notificationID", notificationID);
        reminders.addProperty("time", calendar.getTimeInMillis());
        mJSONArray.add(reminders);
        mJSONObject.add("sNotz", mJSONArray);
        Gson gson = new Gson();
        String json = gson.toJson(mJSONObject);
        sFileUtils.create(json, getReminders(context));
        button.setImageDrawable(sCommonUtils.getDrawable(isReminderSet(noteID, context) ? R.drawable.ic_notification_on
                : R.drawable.ic_notification, context));
    }

    private static void deleteReminder(AppCompatImageButton button, int noteID, Context context) {
        JsonObject mJSONObject = sNotzData.getJSONObject(sFileUtils.read(getReminders(context)));
        JsonArray mJSONArray = Objects.requireNonNull(mJSONObject).getAsJsonArray("sNotz");

        for (int i = 0; i < mJSONArray.size(); i++) {
            JsonObject reminders = mJSONArray.get(i).getAsJsonObject();
            if (reminders.get("noteID").getAsInt() == noteID || reminders.get("notificationID").getAsInt() < System.currentTimeMillis()) {
                mJSONArray.remove(i);
                AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent mIntent = new Intent(context, ReminderReceiver.class);
                PendingIntent mPendingIntent = PendingIntent.getBroadcast(context, reminders.get("notificationID").getAsInt(), mIntent,
                        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE
                                : PendingIntent.FLAG_UPDATE_CURRENT);
                mAlarmManager.cancel(mPendingIntent);
            }
        }
        mJSONObject.add("sNotz", mJSONArray);
        Gson gson = new Gson();
        String json = gson.toJson(mJSONObject);
        sFileUtils.create(json, getReminders(context));
        button.setImageDrawable(sCommonUtils.getDrawable(isReminderSet(noteID, context) ? R.drawable.ic_notification_on
                : R.drawable.ic_notification, context));
    }

    private static void editReminder(Calendar calendar, int noteID, int notificationID, Context context) {
        JsonObject mJSONObject = sNotzData.getJSONObject(sFileUtils.read(getReminders(context)));
        JsonArray mJSONArray = Objects.requireNonNull(mJSONObject).getAsJsonArray("sNotz");
        JsonObject reminders = new JsonObject();
        reminders.addProperty("noteID", noteID);
        reminders.addProperty("notificationID", notificationID);
        reminders.addProperty("time", calendar.getTimeInMillis());
        for (int i = 0; i < mJSONArray.size(); i++) {
            JsonObject note = mJSONArray.get(i).getAsJsonObject();
            if (note.get("noteID").getAsInt() == noteID && note.get("notificationID").getAsInt() == notificationID) {
                mJSONArray.remove(i);
                mJSONArray.add(reminders);
            }
        }
        mJSONObject.add("sNotz", mJSONArray);
        Gson gson = new Gson();
        String json = gson.toJson(mJSONObject);
        sFileUtils.create(json, getReminders(context));
    }

    private static void initialize(Calendar calendar, int noteID, int notificationID, Context context) {
        JsonObject mJSONObject = new JsonObject();
        JsonArray mJSONArray = new JsonArray();
        JsonObject reminders = new JsonObject();
        reminders.addProperty("noteID", noteID);
        reminders.addProperty("notificationID", notificationID);
        reminders.addProperty("time", calendar.getTimeInMillis());
        mJSONArray.add(reminders);
        mJSONObject.add("sNotz", mJSONArray);
        Gson gson = new Gson();
        String json = gson.toJson(mJSONObject);
        sFileUtils.create(json, getReminders(context));
    }

    public static void launchReminderMenu(AppCompatImageButton button, String note, int noteID, Context context) {
        if (isReminderSet(noteID, context)) {
            new sSingleItemDialog(R.mipmap.ic_launcher, context.getString(R.string.reminder_manage),
                    new String[] {
                            context.getString(R.string.modify),
                            context.getString(R.string.delete)
                    }, context) {

                @Override
                public void onItemSelected(int itemPosition) {
                    if (itemPosition == 0) {
                        mYear = -1;
                        mMonth = -1;
                        mDay = -1;
                        launchDatePicker(button, true, note, noteID, context).show();
                    } else {
                        deleteReminder(button, noteID, context);
                    }
                }
            }.show();
        } else {
            mYear = -1;
            mMonth = -1;
            mDay = -1;
            launchDatePicker(button, false, note, noteID, context).show();
        }
    }

}