package com.sunilpaulmathew.snotz.utils;

import android.content.Context;

import com.sunilpaulmathew.snotz.R;

import java.util.ArrayList;
import java.util.List;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 01, 2021
 */
public class sNotzColor {

    public static boolean isRandomColorScheme(Context context) {
        return sCommonUtils.getInt("random_color", Integer.MIN_VALUE, context) != Integer.MIN_VALUE;
    }

    public static int getAccentColor(Context context) {
        int mRandomColor = sCommonUtils.getInt("random_color", Integer.MIN_VALUE, context);
        if (isRandomColorScheme(context)) {
            return getRandomColors().get(mRandomColor).getBackgroundColor();
        } else {
            return sCommonUtils.getInt("accent_color", sCommonUtils.getColor(R.color.color_teal, context), context);
        }
    }

    public static int getTextColor(Context context) {
        int mRandomColor = sCommonUtils.getInt("random_color", Integer.MIN_VALUE, context);
        if (isRandomColorScheme(context)) {
            return getRandomColors().get(mRandomColor).getTextColor();
        } else {
            return sCommonUtils.getInt("text_color", sCommonUtils.getColor(R.color.color_white, context), context);
        }
    }

    private static List<RandomColorItems> getRandomColors() {
        List<RandomColorItems> mRandomColors = new ArrayList<>();
        mRandomColors.add(new RandomColorItems(-14803290,-3020033));
        mRandomColors.add(new RandomColorItems(-5350593,-10223826));
        mRandomColors.add(new RandomColorItems(-14244198,-1));
        mRandomColors.add(new RandomColorItems(-9629786,-32768));
        mRandomColors.add(new RandomColorItems(-5855714,-16760833));
        mRandomColors.add(new RandomColorItems(-5898240,-16711745));
        mRandomColors.add(new RandomColorItems(-689408,-16776971));
        mRandomColors.add(new RandomColorItems(-4194816,-16645630));
        mRandomColors.add(new RandomColorItems(-8388353,-12517632));
        mRandomColors.add(new RandomColorItems(-5855578,-655360));
        return mRandomColors;
    }

    public static void updateRandomColorCode(Context context) {
        int mRandomColor = sCommonUtils.getInt("random_color", Integer.MIN_VALUE, context);
        if (isRandomColorScheme(context)) {
            if (mRandomColor == getRandomColors().size() - 1) {
                sCommonUtils.saveInt("random_color", 0, context);
            } else {
                sCommonUtils.saveInt("random_color", mRandomColor + 1, context);
            }
        }
    }

}