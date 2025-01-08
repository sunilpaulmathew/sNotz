package com.sunilpaulmathew.snotz.utils;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;

import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.providers.WidgetProvider;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2020
 */
public class Utils {

    /*
     * Credits: https://2012atulsharma.medium.com/implementing-biometric-authentication-in-java-android-bf8aa2f4d762
     */
    public static boolean isFingerprintAvailable(Context context) {
        BiometricManager biometricManager = BiometricManager.from(context);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                return true;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                return false;
        }
        return false;
    }

    public static boolean isPermissionDenied(String permission, Context context) {
        return (context.checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED);
    }

    public static BiometricPrompt.PromptInfo showBiometricPrompt(Context context) {
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle(context.getString(R.string.authenticate))
                .setNegativeButtonText(context.getString(R.string.cancel))
                .build();
    }

    public static boolean isSmallScreenSize(int spanCount, Activity activity) {
        int ratio;
        int dpi = activity.getResources().getDisplayMetrics().densityDpi;
        if (sCommonUtils.getOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE) {
            ratio = dpi / 150;
        } else {
            ratio = dpi / 250;
        }
        return spanCount > ratio;
    }

    public static int getSpanCount(Activity activity) {
        int rows = sCommonUtils.getInt("span_count", 0, activity);
        if (rows == 0) {
            return sCommonUtils.isTablet(activity) ? sCommonUtils.getOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2 :
                    sCommonUtils.getOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE ? 2 : 1;
        } else {
            return rows;
        }
    }

    public static void requestPermission(String[] permissions, Activity activity) {
        ActivityCompat.requestPermissions(activity, permissions, 0);
    }

    public static void toggleKeyboard(AppCompatEditText textView, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (textView.requestFocus()) {
            imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void updateWidgets(Context context) {
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(
                context, WidgetProvider.class));
        WidgetProvider mWidgetProvider = new WidgetProvider();
        mWidgetProvider.onUpdate(context, AppWidgetManager.getInstance(context), ids);
    }

    public static void useBiometric(Context context) {
        if (sCommonUtils.getBoolean("use_biometric", false, context)) {
            sCommonUtils.saveBoolean("use_biometric", false, context);
            sCommonUtils.toast(context.getString(R.string.biometric_lock_status, context.getString(R.string.deactivated)), context).show();
        } else {
            sCommonUtils.saveBoolean("use_biometric", true, context);
            sCommonUtils.toast(context.getString(R.string.biometric_lock_status, context.getString(R.string.activated)), context).show();
        }
    }

}