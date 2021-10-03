package com.sunilpaulmathew.snotz.utils;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 02, 2021
 */
public class sNotzReminders {

    private static JSONObject mJSONObject;
    private static JSONArray mJSONArray;

    public static void add(String noteToAdd, int hour, int min, Context context) {
        mJSONObject = new JSONObject();
        mJSONArray = new JSONArray();
        try {
            for (ReminderItems items : getRawData(context)) {
                JSONObject reminder = new JSONObject();
                reminder.put("note", items.getNote());
                reminder.put("hour", items.getHour());
                reminder.put("min", items.getMin());
                mJSONArray.put(reminder);
            }
            JSONObject reminder = new JSONObject();
            reminder.put("note", noteToAdd);
            reminder.put("hour", hour);
            reminder.put("min", min);
            mJSONArray.put(reminder);
            mJSONObject.put("reminders", mJSONArray);
            Utils.create(mJSONObject.toString(), context.getCacheDir().getPath() + "/reminders");
        } catch (JSONException ignored) {
        }
    }

    public static void initialize(String note, int hour, int min, Context context) {
        mJSONObject = new JSONObject();
        mJSONArray = new JSONArray();
        try {
            JSONObject reminder = new JSONObject();
            reminder.put("note", note);
            reminder.put("hour", hour);
            reminder.put("min", min);
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
                mData.add(new ReminderItems(getNote(command.toString()), getHour(command.toString()), getMin(command.toString())));
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
        Date mDate = Calendar.getInstance().getTime();
        int mHour = mDate.getHours();
        int mMin = mDate.getMinutes();
        for (ReminderItems items : sNotzReminders.getRawData(context)) {
            if (mHour == items.getHour() && mMin == items.getMin()) {
                return items.getNote();
            }
        }
        return null;
    }

    private static String getNote(String string) {
        try {
            JSONObject obj = new JSONObject(string);
            return obj.getString("note");
        } catch (JSONException ignored) {
        }
        return null;
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

    public static int getNotificationID(Context context) {
        return Utils.getInt("notificationID", 0, context);
    }

}