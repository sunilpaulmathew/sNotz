package com.sunilpaulmathew.snotz.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.snotz.MainActivity;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.adapters.SettingsAdapter;
import com.sunilpaulmathew.snotz.interfaces.AuthenticatorInterface;

import java.io.File;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.CommonUtils.sExecutor;
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
                    } else if (position == 4) {
                        removePIN(activity);
                        sCommonUtils.toast(activity.getString(R.string.pin_protection_status, activity.getString(R.string.deactivated)), activity).show();
                    } else if (position == 5) {
                        sCommonUtils.saveBoolean("hidden_note", !sCommonUtils.getBoolean("hidden_note", false, activity), activity);
                        AppSettings.setReloadIntent(activity.getIntent(), activity);
                        activity.finish();
                    } else {
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
                new MaterialAlertDialogBuilder(activity)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(R.string.app_name)
                        .setMessage(activity.getString(R.string.restore_backup_message))
                        .setCancelable(false)
                        .setNegativeButton(R.string.cancel, (dialog, which) -> {
                            sFileUtils.delete(sNotzAutoBackup);
                            activity.startActivity(mainActivity);
                            activity.finish();
                        })
                        .setPositiveButton(R.string.restore, (dialog, which) ->
                                new sExecutor() {
                                    @Override
                                    public void onPreExecute() {
                                    }

                                    @Override
                                    public void doInBackground() {
                                        sFileUtils.copy(sNotzAutoBackup, sNotz);
                                    }

                                    @Override
                                    public void onPostExecute() {
                                        activity.startActivity(mainActivity);
                                        activity.finish();
                                    }
                                }.execute()
                        ).show();
                return;
            }
        }

        activity.startActivity(mainActivity);
        activity.finish();
    }

}