package com.sunilpaulmathew.snotz.utils;

import java.io.Serializable;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 02, 2021
 */
public class ReminderItems implements Serializable {


    private final int mHour, mMin;
    private final String mNote;

    public ReminderItems(String note, int hour, int min) {
        this.mNote = note;
        this.mHour = hour;
        this.mMin = min;
    }

    public int getHour() {
        return mHour;
    }

    public int getMin() {
        return mMin;
    }

    public String getNote() {
        return mNote;
    }

}