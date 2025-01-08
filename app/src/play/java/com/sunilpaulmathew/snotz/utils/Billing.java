package com.sunilpaulmathew.snotz.utils;

import android.app.Activity;
import android.content.Intent;

import com.sunilpaulmathew.snotz.activities.BillingActivity;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 05, 2021
 */
public class Billing {

    public static void showDonationMenu(Activity activity) {
        Intent donations = new Intent(activity, BillingActivity.class);
        activity.startActivity(donations);
    }

}