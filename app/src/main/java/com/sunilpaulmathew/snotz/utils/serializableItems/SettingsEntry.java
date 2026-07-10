package com.sunilpaulmathew.snotz.utils.serializableItems;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 01, 2021
 */
public class SettingsEntry implements Serializable {

    private String mDescription;
    private final String mTitle, mURL;
    private final Drawable mIcon;
    private final int id;

    public SettingsEntry(String title) {
        this.mTitle = title;
        this.mDescription = null;
        this.mIcon = null;
        this.mURL = null;
        this.id = 0;
    }

    public SettingsEntry(String title, String description, Drawable icon, String url) {
        this.mTitle = title;
        this.mDescription = description;
        this.mIcon = icon;
        this.mURL = url;
        this.id = 0;
    }

    public SettingsEntry(String title, String description, Drawable icon, int id) {
        this.mTitle = title;
        this.mDescription = description;
        this.mIcon = icon;
        this.mURL = null;
        this.id = id;
    }

    public void setDescription(String description) {
        this.mDescription = description;
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

    public int getId() {
        return id;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public boolean isTitle() {
        return this.mTitle != null && this.mDescription == null && this.mIcon == null
                && this.mURL == null && this.id == 0;
    }

    public boolean isWeblink() {
        return this.mURL != null;
    }

}