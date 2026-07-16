package com.sunilpaulmathew.snotz.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;

import com.sunilpaulmathew.snotz.MainActivity;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.adapters.SettingsAdapter;
import com.sunilpaulmathew.snotz.interfaces.AuthenticatorInterface;
import com.sunilpaulmathew.snotz.utils.dialogs.RestoreNoteDialog;

import java.io.File;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.FileUtils.sFileUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 07, 2021
 */
public class Security {

    public static boolean isBiometricEnabled(Context context) {
        return Utils.isFingerprintAvailable(context) && sCommonUtils.getBoolean("use_biometric", false, context);
    }

    public static boolean isHiddenNotesUnlocked(Context context) {
        return sCommonUtils.getBoolean("hidden_note",false, context);
    }

    public static boolean isPINEnabled(Context context) {
        return sFileUtils.exist(new File(context.getCacheDir(),"pin")) && sCommonUtils
                .getBoolean("use_pin", false, context);
    }

    public static boolean isScreenLocked(Context context) {
        return isBiometricEnabled(context) || isPINEnabled(context);
    }

    public static String getPIN(Context context) {
        if (sFileUtils.exist(new File(context.getCacheDir(),"pin"))) {
            return sFileUtils.read(new File(context.getCacheDir(), "pin"));
        } else {
            return null;
        }
    }

    public static void removePIN(Context context) {
        sFileUtils.delete(new File(context.getCacheDir(),"pin"));
        sCommonUtils.saveBoolean("use_pin", false, context);
    }

    public static void setPIN(String pin, Context context) {
        sFileUtils.create(pin, new File(context.getCacheDir(),"pin"));
    }

    public static void setPIN(boolean verify, String title, SettingsAdapter adapter, int position, Activity activity) {
        new AuthenticatorInterface(false, title, activity) {

            @Override
            public void positiveButtonLister(Editable authText) {
                if (!verify) {
                    setPIN(authText.toString().trim(), activity);
                    setPIN(true, activity.getString(R.string.pin_reenter), adapter, position, activity);
                } else if (authText.toString().trim().equals(getPIN(activity))) {
                    sCommonUtils.saveBoolean("use_pin", true, activity);
                    sCommonUtils.toast(activity.getString(R.string.pin_protection_status, activity.getString(R.string.activated)), activity).show();
                    adapter.notifyItemChanged(position);
                }
            }
        }.show();
    }

    public static void authenticate(boolean login, SettingsAdapter adapter, int position, Activity activity) {
        new AuthenticatorInterface(login, activity.getString(R.string.authenticate), activity) {

            @Override
            public void positiveButtonLister(Editable authText) {
                if (authText != null && authText.toString().trim().length() == 4
                        && authText.toString().trim().equals(getPIN(activity))) {
                    if (login) {
                        // Launch MainActivity
                        launchMainActivity(activity);
                    } else if (position == 5) {
                        removePIN(activity);
                        sCommonUtils.toast(activity.getString(R.string.pin_protection_status, activity.getString(R.string.deactivated)), activity).show();
                    } else if (position == 6) {
                        sCommonUtils.saveBoolean("hidden_note", !sCommonUtils.getBoolean("hidden_note", false, activity), activity);
                        AppSettings.setReloadIntent(activity.getIntent(), activity);
                        activity.finish();
                    } else if (position == 18) {
                        sFileUtils.delete(new File(activity.getFilesDir(),"snotz"));
                        AppSettings.setReloadIntent(activity.getIntent(), activity);
                        activity.finish();
                    }
                    if (adapter != null) {
                        adapter.notifyItemChanged(position);
                    }
                }
            }
        }.show();
    }

    public static void launchMainActivity(Activity activity) {
        int extraNoteId = activity.getIntent().getIntExtra("noteId", Integer.MIN_VALUE);
        String externalNote = activity.getIntent().getStringExtra("externalNote");

        Intent mainActivity = new Intent(activity, MainActivity.class);
        if (extraNoteId != Integer.MIN_VALUE) {
            mainActivity.putExtra("noteId", extraNoteId);
        } else if (externalNote != null) {
            mainActivity.putExtra("externalNote", externalNote);
        }

        if (sCommonUtils.getBoolean("auto_backup", true, activity)) {
            // Manage auto-backup
            File sNotz = new File(activity.getFilesDir(),"snotz");
            File sNotzAutoBackup = new File(activity.getExternalFilesDir("autoBackup"),"autoBackup");

            if (sNotz.exists() && !sNotzData.getRawData(activity).isEmpty() && sNotz.length() != sNotzAutoBackup.length()) {
                AppSettings.backupData(sNotz, sNotzAutoBackup, activity);
            } else if ((!sNotz.exists() || sNotzData.getRawData(activity).isEmpty()) && sNotzAutoBackup.exists()) {
                new RestoreNoteDialog(sFileUtils.read(sNotzAutoBackup), true, activity) {
                    @Override
                    public void negativeButtonLister() {
                        start(mainActivity, activity);
                    }

                    @Override
                    public void positiveButtonLister() {
                        start(mainActivity, activity);
                    }
                };
                return;
            }
        }

        start(mainActivity, activity);
    }

    private static void start(Intent intent, Activity activity) {
        activity.startActivity(intent);
        activity.finish();
    }

}