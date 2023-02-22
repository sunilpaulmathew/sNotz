package com.sunilpaulmathew.snotz.utils;

import android.app.Activity;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 05, 2021
 */
public class Billing {

    public static void showDonationMenu(Activity activity) {
        sCommonUtils.launchUrl("https://smartpack.github.io/donation/", activity);
    }

}