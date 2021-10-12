package com.sunilpaulmathew.snotz.utils;

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

    private static boolean mHiddenNotes = false, mReload = false, mWorking = false;
    private static int mColorBackground = -1, mColorText = -1, mId = -1;
    private static RecyclerView mRecyclerView;
    private static String mImageString = null, mNote = null, mSearchText = null;

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
        String mMonth = null;
        if (month == 0) {
            mMonth = months.get(0) + " ";
        } else if (month == 1) {
            mMonth = months.get(1) + " ";
        } else if (month == 2) {
            mMonth = months.get(2) + " ";
        } else if (month == 3) {
            mMonth = months.get(3) + " ";
        } else if (month == 4) {
            mMonth = months.get(4) + " ";
        } else if (month == 5) {
            mMonth = months.get(5) + " ";
        } else if (month == 6) {
            mMonth = months.get(6) + " ";
        } else if (month == 7) {
            mMonth = months.get(7) + " ";
        } else if (month == 8) {
            mMonth = months.get(8) + " ";
        } else if (month == 9) {
            mMonth = months.get(9) + " ";
        } else if (month == 10) {
            mMonth = months.get(10) + " ";
        } else if (month == 11) {
            mMonth = months.get(11) + " ";
        }
        String mTime;
        if (hour > 12) {
            mTime =  (hour - 12) + ":" + (min < 10 ? "0" + min : min) + " PM";
        } else {
            mTime = hour + ":" + (min < 10 ? "0" + min : min) + " AM";
        }
        return mMonth + " " + (int)day + ", " + (int)year + " " + mTime;
    }

    public static String getImageString() {
        return mImageString;
    }

    public static String getNote() {
        return mNote;
    }

    public static String getSearchText() {
        return mSearchText;
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

    public static void setSearchText(String searchText) {
        mSearchText = searchText;
    }

    public static void setTextColor(int color) {
        mColorText = color;
    }

}