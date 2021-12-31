package com.sunilpaulmathew.snotz.utils;

import java.io.Serializable;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 31, 2021
 */
public class RandomColorItems implements Serializable {

    private final int mBackground, mText;

    public RandomColorItems(int background, int text) {
        this.mBackground = background;
        this.mText = text;
    }

    public int getBackgroundColor() {
        return mBackground;
    }

    public int getTextColor() {
        return mText;
    }

}