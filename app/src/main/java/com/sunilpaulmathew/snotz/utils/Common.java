package com.sunilpaulmathew.snotz.utils;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 01, 2021
 */
public class Common {

    public static boolean isTextMatched(String note, String searchText) {
        for (int a = 0; a < note.length() - searchText.length() + 1; a++) {
            if (searchText.equalsIgnoreCase(note.substring(a, a + searchText.length()))) {
                return true;
            }
        }
        return false;
    }

    public static String getAdjustedTime(double year, double month, double day, int hour, int min) {
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.getDefault());
        List<String> months = new ArrayList<>();
        Collections.addAll(months, dfs.getMonths());
        String mMonth = months.get((int) month) + " ";
        String mTime;
        if (hour > 12) {
            mTime =  (hour - 12) + ":" + (min < 10 ? "0" + min : min) + " PM";
        } else {
            mTime = hour + ":" + (min < 10 ? "0" + min : min) + " AM";
        }
        return mMonth + " " + (int) day + ", " + (int) year + " " + mTime;
    }

}