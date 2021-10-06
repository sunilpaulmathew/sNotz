package com.sunilpaulmathew.snotz.utils;

import android.view.View;

import androidx.biometric.BiometricPrompt;
import androidx.recyclerview.widget.RecyclerView;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 01, 2021
 */
public class Common {

    public static BiometricPrompt mBiometricPrompt = null;
    public static RecyclerView mRecyclerView;

    private static boolean mHiddenNotes = false, mReload = false;
    private static int mColorBackground = -1, mColorText = -1, mId = -1;
    private static String mImageString = null, mNote = null, mSearchText = null;

    public static BiometricPrompt getBiometricPrompt() {
        return mBiometricPrompt;
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

    public static void isReloading(boolean b) {
        mReload = b;
    }

    public static void setSearchText(String searchText) {
        mSearchText = searchText;
    }

    public static void setTextColor(int color) {
        mColorText = color;
    }

}