package com.sunilpaulmathew.snotz.utils.serializableItems;

import java.io.Serializable;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 10, 2021
 */
public class CheckListItems implements Serializable {

    private String mTitle;
    private boolean mChecked, mModified;

    public CheckListItems(String title, boolean checked, boolean modified) {
        this.mTitle = title;
        this.mChecked = checked;
        this.mModified = modified;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public boolean isModified() {
        return mModified;
    }

    public String getTitle() {
        return mTitle;
    }

    public void isChecked(boolean b) {
        mChecked = b;
    }

    public void isModified(boolean b) {
        mModified = b;
    }

    public void setTitle(String title) {
        if (title.endsWith("\n")) {
            title = title.replace("\n","");
        }
        mTitle = title;
    }

}