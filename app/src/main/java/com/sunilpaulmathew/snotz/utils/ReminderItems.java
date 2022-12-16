package com.sunilpaulmathew.snotz.utils;

import java.io.Serializable;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 15, 2022
 */
public class ReminderItems implements Serializable {

    private final int mNoteID, mNotificationID;
    private final long mTime;

    public ReminderItems(int noteID, int notificationID, long time) {
        this.mNoteID = noteID;
        this.mNotificationID = notificationID;
        this.mTime = time;
    }

    public int getNoteID() {
        return mNoteID;
    }

    public int getNotificationID() {
        return mNotificationID;
    }

    public long getTime() {
        return mTime;
    }

}