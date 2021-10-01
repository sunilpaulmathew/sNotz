package com.sunilpaulmathew.snotz.utils;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 01, 2021
 */
public class SettingsItems implements Serializable {

    private final String mTitle, mDescription, mURL;
    private final Drawable mIcon;

    public SettingsItems(String title, String description, Drawable icon, String url) {
        this.mTitle = title;
        this.mDescription = description;
        this.mURL = url;
        this.mIcon = icon;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getUrl() {
        return mURL;
    }

    public Drawable getIcon() {
        return mIcon;
    }

}