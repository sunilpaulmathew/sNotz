package com.sunilpaulmathew.snotz.utils;

import android.content.Context;

import com.sunilpaulmathew.snotz.R;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 01, 2021
 */
public class sNotzColor {

    public static int getAccentColor(Context context) {
        return Utils.getInt("accent_color", context.getResources().getColor(R.color.color_teal), context);
    }

    public static int getTextColor(Context context) {
        return Utils.getInt("text_color", context.getResources().getColor(R.color.color_white), context);
    }

}