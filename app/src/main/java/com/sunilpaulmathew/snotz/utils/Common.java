package com.sunilpaulmathew.snotz.utils;

import android.graphics.Bitmap;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 01, 2021
 */
public class Common {

    private static Bitmap mReadModeImage = null;
    private static boolean mClearNote = false, mHiddenNotes = false,
            mReload = false, mWorking = false;
    private static int mColorBackground = -1, mColorText = -1, mId = -1, mSpan = 1;
    private static RecyclerView mRecyclerView;
    private static String mImageString = null, mNote = null, mReadModeText = null, mSearchText = null;

    public static Bitmap getReadModeImage() {
        return mReadModeImage;
    }

    public static boolean isClearingNotes() {
        return mClearNote;
    }

    public static boolean isHiddenNote() {
        return mHiddenNotes;
    }

    public static boolean isTextMatched(String note) {
        for (int a = 0; a < note.length() - mSearchText.length() + 1; a++) {
            if (mSearchText.equalsIgnoreCase(note.substring(a, a + mSearchText.length()))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isReloading() {
        return mReload;
    }

    public static boolean isWorking() {
        return mWorking;
    }

    public static int getBackgroundColor() {
        return mColorBackground;
    }

    public static int getID() {
        return mId;
    }

    public static int getSpanCount() {
        return mSpan;
    }

    public static int getTextColor() {
        return mColorText;
    }

    public static RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public static RecyclerView initializeRecyclerView(int id, View view) {
        return mRecyclerView = view.findViewById(id);
    }

    public static String getAdjustedTime(double year, double month, double day, int hour, int min) {
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.getDefault());
        List<String> months = new ArrayList<>();
        Collections.addAll(months, dfs.getMonths());
        String mMonth = months.get((int) month) + " ";
        String mTime;
        if (hour > 12) {
            mTime =  (hour - 12) + ":" + (min < 10 ? "0" + min : min) + " PM";
        } else {
            mTime = hour + ":" + (min < 10 ? "0" + min : min) + " AM";
        }
        return mMonth + " " + (int) day + ", " + (int) year + " " + mTime;
    }

    public static String getImageString() {
        return mImageString;
    }

    public static String getNote() {
        return mNote;
    }

    public static String getReadModeText() {
        return mReadModeText;
    }

    public static String getSearchText() {
        return mSearchText;
    }

    public static void isClearingNotes(boolean b) {
        mClearNote = b;
    }

    public static void isHiddenNote(boolean b) {
        mHiddenNotes = b;
    }

    public static void isReloading(boolean b) {
        mReload = b;
    }

    public static void isWorking(boolean b) {
        mWorking = b;
    }

    public static void setBackgroundColor(int color) {
        mColorBackground = color;
    }

    public static void setID(int id) {
        mId = id;
    }

    public static void setImageString(String imageString) {
        mImageString = imageString;
    }

    public static void setNote(String note) {
        mNote = note;
    }

    public static void setReadModeImage(Bitmap bitmap) {
        mReadModeImage = bitmap;
    }

    public static void setReadModeText(String readModeText) {
        mReadModeText = readModeText;
    }

    public static void setSearchText(String searchText) {
        mSearchText = searchText;
    }

    public static void setSpanCount(int span) {
        mSpan = span;
    }

    public static void setTextColor(int color) {
        mColorText = color;
    }

}