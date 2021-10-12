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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 02, 2021
 */
public class sNotzReminders {

    private static double mYear = -1, mMonth = -1, mDay = -1;
    private static int mPosition = -1;
    private static JSONObject mJSONObject;
    private static JSONArray mJSONArray;
    private static String mNote = null;

    public static void add(String noteToAdd, double year, double month, double day,
                           int hour, int min, int id, Context context) {
        mJSONObject = new JSONObject();
        mJSONArray = new JSONArray();
        try {
            for (ReminderItems items : getRawData(context)) {
                JSONObject reminder = new JSONObject();
                reminder.put("note", items.getNote());
                reminder.put("year", items.getYear());
                reminder.put("month", items.getMonth());
                reminder.put("day", items.getDay());
                reminder.put("hour", items.getHour());
                reminder.put("min", items.getMin());
                reminder.put("id", items.getNotificationID());
                mJSONArray.put(reminder);
            }
            JSONObject reminder = new JSONObject();
            reminder.put("note", noteToAdd);
            reminder.put("year", year);
            reminder.put("month", month);
            reminder.put("day", day);
            reminder.put("hour", hour);
            reminder.put("min", min);
            reminder.put("id", id);
            mJSONArray.put(reminder);
            mJSONObject.put("reminders", mJSONArray);
            Utils.create(mJSONObject.toString(), context.getCacheDir().getPath() + "/reminders");
        } catch (JSONException ignored) {
        }
    }

    public static void edit(String noteToEdit, double year, double month, double day,
                            int hour, int min, int id, Context context) {
        mJSONObject = new JSONObject();
        mJSONArray = new JSONArray();
        try {
            for (ReminderItems items : getRawData(context)) {
                if (items.getNotificationID() != id) {
                    JSONObject reminder = new JSONObject();
                    reminder.put("note", items.getNote());
                    reminder.put("year", items.getYear());
                    reminder.put("month", items.getMonth());
                    reminder.put("day", items.getDay());
                    reminder.put("hour", items.getHour());
                    reminder.put("min", items.getMin());
                    reminder.put("id", items.getNotificationID());
                    mJSONArray.put(reminder);
                }
            }
            JSONObject reminder = new JSONObject();
            reminder.put("note", noteToEdit);
            reminder.put("year", year);
            reminder.put("month", month);
            reminder.put("day", day);
            reminder.put("hour", hour);
            reminder.put("min", min);
            reminder.put("id", id);
            mJSONArray.put(reminder);
            mJSONObject.put("reminders", mJSONArray);
            Utils.create(mJSONObject.toString(), context.getCacheDir().getPath() + "/reminders");
        } catch (JSONException ignored) {
        }
    }

    public static void delete(int id, Context context) {
        mJSONObject = new JSONObject();
        mJSONArray = new JSONArray();
        try {
            for (ReminderItems items : getRawData(context)) {
                if (items.getNotificationID() != id) {
                    JSONObject reminder = new JSONObject();
                    reminder.put("note", items.getNote());
                    reminder.put("hour", items.getHour());
                    reminder.put("min", items.getMin());
                    reminder.put("id", items.getNotificationID());
                    mJSONArray.put(reminder);
                }
            }
            mJSONObject.put("reminders", mJSONArray);
            Utils.create(mJSONObject.toString(), context.getCacheDir().getPath() + "/reminders");
        } catch (JSONException ignored) {
        }
    }

    public static void initialize(String note, double year, double month, double day, int hour,
                                  int min, int id, Context context) {
        mJSONObject = new JSONObject();
        mJSONArray = new JSONArray();
        try {
            JSONObject reminder = new JSONObject();
            reminder.put("note", note);
            reminder.put("year", year);
            reminder.put("month", month);
            reminder.put("day", day);
            reminder.put("hour", hour);
            reminder.put("min", min);
            reminder.put("id", id);
            mJSONArray.put(reminder);
            mJSONObject.put("reminders", mJSONArray);
            Utils.create(mJSONObject.toString(), context.getCacheDir().getPath() + "/reminders");
        } catch (JSONException ignored) {
        }
    }

    public static List<ReminderItems> getRawData(Context context) {
        List<ReminderItems> mData = new ArrayList<>();
        for (int i = 0; i < Objects.requireNonNull(getReminders(context)).length(); i++) {
            try {
                JSONObject command = Objects.requireNonNull(getReminders(context)).getJSONObject(i);
                mData.add(new ReminderItems(getNote(command.toString()), getYear(command.toString()), getMonth(command.toString()),
                        getDay(command.toString()), getHour(command.toString()), getMin(command.toString()), getID(command.toString())));
            } catch (JSONException ignored) {
            }
        }
        return mData;
    }

    public static JSONArray getReminders(Context context) {
        try {
            JSONObject main = new JSONObject(Objects.requireNonNull(Utils.read(context.getCacheDir().getPath() + "/reminders")));
            return main.getJSONArray("reminders");
        } catch (JSONException ignored) {
        }
        return null;
    }

    public static String getReminderMessage(Context context) {
        Calendar mCalendar = Calendar.getInstance();
        int mYear = mCalendar.get(Calendar.YEAR);
        int mMonth = mCalendar.get(Calendar.MONTH);
        int mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        int mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int mMin = mCalendar.get(Calendar.MINUTE);
        for (ReminderItems items : sNotzReminders.getRawData(context)) {
            if (mYear == items.getYear() && mMonth == items.getMonth() && mDay == items.getDay()
                    && mHour == items.getHour() && mMin == items.getMin()) {
                mNote = items.getNote();
                mPosition = items.getNotificationID();
            }
        }
        delete(mPosition, context);
        return mNote;
    }

    private static String getNote(String string) {
        try {
            JSONObject obj = new JSONObject(string);
            return obj.getString("note");
        } catch (JSONException ignored) {
        }
        return null;
    }

    private static double getYear(String string) {
        try {
            JSONObject obj = new JSONObject(string);
            return obj.getDouble("year");
        } catch (JSONException ignored) {
        }
        return 0;
    }

    private static double getMonth(String string) {
        try {
            JSONObject obj = new JSONObject(string);
            return obj.getDouble("month");
        } catch (JSONException ignored) {
        }
        return 0;
    }

    private static double getDay(String string) {
        try {
            JSONObject obj = new JSONObject(string);
            return obj.getDouble("day");
        } catch (JSONException ignored) {
        }
        return 0;
    }

    private static int getHour(String string) {
        try {
            JSONObject obj = new JSONObject(string);
            return obj.getInt("hour");
        } catch (JSONException ignored) {
        }
        return 0;
    }

    private static int getMin(String string) {
        try {
            JSONObject obj = new JSONObject(string);
            return obj.getInt("min");
        } catch (JSONException ignored) {
        }
        return 0;
    }

    private static int getID(String string) {
        try {
            JSONObject obj = new JSONObject(string);
            return obj.getInt("id");
        } catch (JSONException ignored) {
        }
        return 0;
    }

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

    public static void setReminder(double year, double month, double day, int hour, int min, int id,
                                   String note, Context context) {
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent mIntent = new Intent(context, ReminderReceiver.class);

        int mNotificationID = sNotzReminders.getNotificationID(context);

        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(context, mNotificationID, mIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.MINUTE, min);
        mCalendar.set(Calendar.SECOND, 0);
        mIntent.putExtra("id", mNotificationID);
        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), mPendingIntent);

        if (id != -1) {
            sNotzReminders.edit(note, year, month, day, hour, min, id, context);
        } else if (Utils.exist(context.getCacheDir().getPath() + "/reminders")) {
            sNotzReminders.add(note, year, month, day, hour, min, mNotificationID,context);
        } else {
            sNotzReminders.initialize(note, year, month, day, hour, min, mNotificationID, context);
        }
        Utils.saveInt("notificationID", mNotificationID + 1, context);
        new MaterialAlertDialogBuilder(context)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setMessage(context.getString(R.string.reminder_message, Common.getAdjustedTime(year, month, day, hour, min)))
                .setCancelable(false)
                .setPositiveButton(R.string.cancel, (dialogInterface, i) -> {
                }).show();
    }

    public static DatePickerDialog launchDatePicker(int id, String note, Context context) {
        Calendar mCalendar = Calendar.getInstance();
        return new DatePickerDialog(context,
                (view, year, month, dayOfMonth) -> {
                    setYear(year);
                    setMonth(month);
                    setDay(dayOfMonth);
                    if (getYear() != -1 && getMonth() != -1 && getDay() != -1) {
                        launchTimePicker(year, month, dayOfMonth, id, note, context).show();
                    }
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public static TimePickerDialog launchTimePicker(double year, double month, double day, int id,
                                                    String note, Context context) {
        Calendar mCalendar = Calendar.getInstance();
        return new TimePickerDialog(context,
                (view, hourOfDay, minute) -> setReminder(year, month,  day, hourOfDay, minute, id, note, context), mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false);
    }

}