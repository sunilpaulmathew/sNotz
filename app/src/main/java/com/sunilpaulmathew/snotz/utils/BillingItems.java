package com.sunilpaulmathew.snotz.utils;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 01, 2021
 */
public class BillingItems implements Serializable {

    private final String mTitle;
    private final Drawable mIcon;

    public BillingItems(String title, Drawable icon) {
        this.mTitle = title;
        this.mIcon = icon;
    }

    public String getTitle() {
        return mTitle;
    }

    public Drawable getIcon() {
        return mIcon;
    }

}