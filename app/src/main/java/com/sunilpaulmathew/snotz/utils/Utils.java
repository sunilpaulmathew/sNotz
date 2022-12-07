package com.sunilpaulmathew.snotz.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.biometric.BiometricPrompt;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import com.sunilpaulmathew.snotz.BuildConfig;
import com.sunilpaulmathew.snotz.MainActivity;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.adapters.NotesAdapter;

import in.sunilpaulmathew.sCommon.Utils.sPackageUtils;
import in.sunilpaulmathew.sCommon.Utils.sUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2020
 */
public class Utils {

    public static boolean isFingerprintAvailable(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            return FingerprintManagerCompat.from(context).hasEnrolledFingerprints();
        }
        return false;
    }

    public static boolean isNotDonated(Context context) {
        if (BuildConfig.DEBUG) return false;
        return !sPackageUtils.isPackageInstalled("com.smartpack.donate", context);
    }

    public static BiometricPrompt.PromptInfo showBiometricPrompt(Context context) {
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle(context.getString(R.string.authenticate))
                .setNegativeButtonText(context.getString(R.string.cancel))
                .build();
    }

    public static boolean isSmallScreenSize(Activity activity) {
        int ratio;
        int dpi = activity.getResources().getDisplayMetrics().densityDpi;
        if (sUtils.getOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE) {
            ratio = dpi / 150;
        } else {
            ratio = dpi / 250;
        }
        return Common.getSpanCount() > ratio;
    }

    public static int getSpanCount(Activity activity) {
        int rows = sUtils.getInt("span_count", 0, activity);
        if (rows == 0) {
            return sUtils.isTablet(activity) ? sUtils.getOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2 :
                    sUtils.getOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE ? 2 : 1;
        } else {
            return rows;
        }
    }

    public static void reloadUI(Context context) {
        try {
            Common.getRecyclerView().setAdapter(new NotesAdapter(sNotzData.getData(context)));
        } catch (NullPointerException ignored) {}
    }

    public static void restartApp(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void toggleKeyboard(AppCompatEditText textView, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (textView.requestFocus()) {
            imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void useBiometric(Activity activity) {
        if (sUtils.getBoolean("use_biometric", false, activity)) {
            sUtils.saveBoolean("use_biometric", false, activity);
            sUtils.snackBar(activity.findViewById(android.R.id.content), activity.getString(R.string.biometric_lock_status,
                    activity.getString(R.string.deactivated))).show();
        } else {
            sUtils.saveBoolean("use_biometric", true, activity);
            sUtils.snackBar(activity.findViewById(android.R.id.content), activity.getString(R.string.biometric_lock_status,
                    activity.getString(R.string.activated))).show();
        }
    }

}