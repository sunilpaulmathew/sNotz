package com.sunilpaulmathew.snotz.utils;

import java.io.Serializable;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 02, 2021
 */
public class ReminderItems implements Serializable {

    private final double mYear, mMonth, mDay;
    private final int mHour, mID, mMin;
    private final String mNote;

    public ReminderItems(String note, double year, double month, double day, int hour, int min, int id) {
        this.mNote = note;
        this.mYear = year;
        this.mMonth = month;
        this.mDay = day;
        this.mHour = hour;
        this.mMin = min;
        this.mID = id;
    }

    public double getYear() {
        return mYear;
    }

    public double getMonth() {
        return mMonth;
    }

    public double getDay() {
        return mDay;
    }

    public int getHour() {
        return mHour;
    }

    public int getNotificationID() {
        return mID;
    }

    public int getMin() {
        return mMin;
    }

    public String getNote() {
        return mNote;
    }

}