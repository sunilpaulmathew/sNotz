package com.sunilpaulmathew.snotz.utils.serializableItems;

import com.sunilpaulmathew.snotz.utils.CheckLists;

import java.io.Serializable;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 01, 2021
 */
public class sNotzItems implements Serializable {

    private final boolean mHidden;
    private final int mColorBackground, mColorText, mID;
    private final long mTimeStamp;
    private final String mNote;

    public sNotzItems(String note, long timeStamp, boolean hidden, int colorBackground, int colorText, int id) {
        this.mNote = note;
        this.mTimeStamp = timeStamp;
        this.mHidden = hidden;
        this.mColorBackground = colorBackground;
        this.mColorText = colorText;
        this.mID = id;
    }

    public boolean isChecklist() {
        return CheckLists.isValidCheckList(mNote);
    }

    public boolean isHidden() {
        return mHidden;
    }

    public int getColorBackground() {
        return mColorBackground;
    }

    public int getColorText() {
        return mColorText;
    }

    public int getNoteID() {
        return mID;
    }

    public String getNote() {
        return mNote;
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

}