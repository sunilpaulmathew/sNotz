package com.sunilpaulmathew.snotz.utils;

import android.app.Activity;

import in.sunilpaulmathew.sCommon.Utils.sUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 05, 2021
 */
public class Billing {

    public static void showDonationMenu(Activity activity) {
        sUtils.launchUrl("https://smartpack.github.io/donation/", activity);
    }

}