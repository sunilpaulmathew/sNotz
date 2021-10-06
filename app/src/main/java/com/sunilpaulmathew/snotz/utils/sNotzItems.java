package com.sunilpaulmathew.snotz.utils;

import java.io.Serializable;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 01, 2021
 */
public class sNotzItems implements Serializable {

    private final boolean mHidden;
    private final int mColorBackground, mColorText, mID;
    private final String mImageString, mNote, mTimeStamp;

    public sNotzItems(String note, String timeStamp, String imageString, boolean hidden, int colorBackground, int colorText, int id) {
        this.mNote = note;
        this.mTimeStamp = timeStamp;
        this.mImageString = imageString;
        this.mHidden = hidden;
        this.mColorBackground = colorBackground;
        this.mColorText = colorText;
        this.mID = id;
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

    public String getImageString() {
        return mImageString;
    }

    public String getNote() {
        return mNote;
    }

    public String getTimeStamp() {
        return mTimeStamp;
    }

}