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

    private static boolean mHiddenNotes = false, mTextColor = false;
    private static String mNote = null, mSearchText = null;

    public static BiometricPrompt getBiometricPrompt() {
        return mBiometricPrompt;
    }

    public static boolean isHiddenNote() {
        return mHiddenNotes;
    }

    public static boolean isTextColor() {
        return mTextColor;
    }

    public static RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public static RecyclerView initializeRecyclerView(int id, View view) {
        return mRecyclerView = view.findViewById(id);
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

    public static void isTextColor(boolean b) {
        mTextColor = b;
    }

    public static void setNote(String note) {
        mNote = note;
    }

    public static void setSearchText(String searchText) {
        mSearchText = searchText;
    }

}