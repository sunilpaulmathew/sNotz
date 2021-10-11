package com.sunilpaulmathew.snotz.utils;

import java.io.Serializable;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 10, 2021
 */
public class CheckListItems implements Serializable {

    private String mTitle;
    private boolean mChecked;

    public CheckListItems(String title, boolean checked) {
        this.mTitle = title;
        this.mChecked = checked;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public String getTitle() {
        return mTitle;
    }

    public void isChecked(boolean b) {
        mChecked = b;
    }

    public void setTitle(String title) {
        if (title.endsWith("\n")) {
            title = title.replace("\n","");
        }
        mTitle = title;
    }

}