package com.sunilpaulmathew.snotz.utils.serializableItems;

import java.io.Serializable;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 31, 2021
 */
public class RandomColorEntry implements Serializable {

    private final int mBackground, mText;

    public RandomColorEntry(int background, int text) {
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