package com.sunilpaulmathew.snotz.utils;

import android.content.Context;

import com.sunilpaulmathew.snotz.R;

import in.sunilpaulmathew.sCommon.Utils.sUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 01, 2021
 */
public class sNotzColor {

    public static int getAccentColor(Context context) {
        return sUtils.getInt("accent_color", sUtils.getColor(R.color.color_teal, context), context);
    }

    public static int getTextColor(Context context) {
        return sUtils.getInt("text_color", sUtils.getColor(R.color.color_white, context), context);
    }

}