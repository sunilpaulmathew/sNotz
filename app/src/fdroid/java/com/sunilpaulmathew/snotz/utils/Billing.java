package com.sunilpaulmathew.snotz.utils;

import android.app.Activity;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 05, 2021
 */
public class Billing {

    public static void showDonationMenu(Activity activity) {
        Utils.launchURL("https://smartpack.github.io/donation/", activity);
    }

}