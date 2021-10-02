package com.sunilpaulmathew.snotz.utils;

import java.io.Serializable;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 01, 2021
 */
public class sNotzItems implements Serializable {

    private final boolean mHidden;
    private final int mColorBackground, mColorText;
    private final String mNote, mTimeStamp;

    public sNotzItems(String note, String timeStamp, boolean hidden, int colorBackground, int colorText) {
        this.mNote = note;
        this.mTimeStamp = timeStamp;
        this.mHidden = hidden;
        this.mColorBackground = colorBackground;
        this.mColorText = colorText;
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

    public String getNote() {
        return mNote;
    }

    public String getTimeStamp() {
        return mTimeStamp;
    }

}