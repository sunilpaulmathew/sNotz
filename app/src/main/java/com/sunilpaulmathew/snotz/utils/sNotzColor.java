package com.sunilpaulmathew.snotz.utils;

import android.content.Context;
import android.content.res.TypedArray;

import com.google.android.material.color.DynamicColors;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.serializableItems.RandomColorItems;

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

    public static int getAppAccentColor(Context context) {
        return sCommonUtils.getInt("app_accent_color", getMaterial3Colors(0, sCommonUtils.getColor(R.color.color_teal, context), context), context);
    }

    public static int getAccentColor(Context context) {
        int mRandomColor = sCommonUtils.getInt("random_color", Integer.MIN_VALUE, context);
        if (isRandomColorScheme(context)) {
            return getRandomColors(context).get(mRandomColor).getBackgroundColor();
        } else {
            return sCommonUtils.getInt("accent_color", getMaterial3Colors(0, sCommonUtils.getColor(R.color.color_teal, context), context), context);
        }
    }

    public static int getDefaultColor(int position, Context context) {
        if (position == 0) {
            return sCommonUtils.getInt("app_accent_color", getAppAccentColor(context), context);
        } else if (position == 1) {
            return sCommonUtils.getInt("accent_color", getAccentColor(context), context);
        } else if (position == 2) {
            return sCommonUtils.getInt("text_color", getTextColor(context), context);
        } else {
            return sCommonUtils.getInt("checklist_color", sNotzColor.getMaterial3Colors(0, sCommonUtils.getColor(R.color.color_teal, context), context), context);
        }
    }

    /*
    index values: 0 - colorPrimary; 1 - colorOnPrimary; 2 - colorSecondary; 3 - colorAccent
     */
    public static int getMaterial3Colors(int index, int defaultColor, Context context) {
        int material3Color = defaultColor;
        if (DynamicColors.isDynamicColorAvailable()) {
            Context dynamicClrCtx = DynamicColors.wrapContextIfAvailable(context, R.style.Theme_Material3_DynamicColors_DayNight);
            TypedArray ta = dynamicClrCtx.obtainStyledAttributes(new int[] {
                    R.attr.colorPrimary,
                    R.attr.colorOnPrimary,
                    R.attr.colorSecondary,
                    R.attr.colorAccent
            });
            material3Color = ta.getColor(index, defaultColor);
            ta.recycle();
        }
        return material3Color;
    }

    public static int getTextColor(Context context) {
        int mRandomColor = sCommonUtils.getInt("random_color", Integer.MIN_VALUE, context);
        if (isRandomColorScheme(context)) {
            return getRandomColors(context).get(mRandomColor).getTextColor();
        } else {
            return sCommonUtils.getInt("text_color", getMaterial3Colors(1, sCommonUtils.getColor(R.color.color_white, context), context), context);
        }
    }

    private static List<RandomColorItems> getRandomColors(Context context) {
        List<RandomColorItems> mRandomColors = new ArrayList<>();
        if (DynamicColors.isDynamicColorAvailable()) {
            mRandomColors.add(new RandomColorItems(getMaterial3Colors(0, sCommonUtils.getColor(R.color.color_teal, context), context), getMaterial3Colors(1, sCommonUtils.getColor(R.color.color_white, context), context)));
        }
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
            if (mRandomColor == getRandomColors(context).size() - 1) {
                sCommonUtils.saveInt("random_color", 0, context);
            } else {
                sCommonUtils.saveInt("random_color", mRandomColor + 1, context);
            }
        }
    }

}