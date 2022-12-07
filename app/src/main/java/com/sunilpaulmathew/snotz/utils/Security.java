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
import java.util.concurrent.Executors;

import in.sunilpaulmathew.sCommon.Utils.sExecutor;
import in.sunilpaulmathew.sCommon.Utils.sUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 07, 2021
 */
public class Security {

    public static boolean isBiometricEnabled(Context context) {
        return sUtils.getBoolean("use_biometric", false, context);
    }

    public static boolean isHiddenNotesUnlocked(Context context) {
        return sUtils.getBoolean("hidden_note",false, context);
    }

    public static boolean isPINEnabled(Context context) {
        return sUtils.exist(new File(context.getCacheDir(),"pin")) && sUtils
                .getBoolean("use_pin", false, context);
    }

    public static boolean isScreenLocked(Context context) {
        return isBiometricEnabled(context) || isPINEnabled(context);
    }

    public static String getPIN(Context context) {
        if (sUtils.exist(new File(context.getCacheDir(),"pin"))) {
            return sUtils.read(new File(context.getCacheDir(), "pin"));
        } else {
            return null;
        }
    }

    public static void removePIN(Context context) {
        sUtils.delete(new File(context.getCacheDir(),"pin"));
        sUtils.saveBoolean("use_pin", false, context);
    }

    public static void setPIN(String pin, Context context) {
        sUtils.create(pin, new File(context.getCacheDir(),"pin"));
    }

    public static void setPIN(boolean verify, String title, SettingsAdapter adapter, Activity activity) {
        new AuthenticatorInterface(false, title, activity) {

            @Override
            public void positiveButtonLister(Editable authText) {
                if (!verify) {
                    setPIN(authText.toString().trim(), activity);
                    setPIN(true, activity.getString(R.string.pin_reenter), adapter, activity);
                } else if (authText.toString().trim().equals(getPIN(activity))) {
                    sUtils.saveBoolean("use_pin", true, activity);
                    sUtils.snackBar(activity.findViewById(android.R.id.content), activity.getString(R.string.pin_protection_status,
                            activity.getString(R.string.activated))).show();
                    adapter.notifyItemChanged(3);
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
                    } else if (position == 3) {
                        removePIN(activity);
                        sUtils.snackBar(activity.findViewById(android.R.id.content), activity.getString(R.string.pin_protection_status,
                                activity.getString(R.string.deactivated))).show();
                    } else if (position == 4) {
                        sUtils.saveBoolean("hidden_note", !sUtils.getBoolean("hidden_note", false, activity), activity);
                        Utils.reloadUI(activity);
                        activity.finish();
                    } else {
                        sUtils.delete(new File(activity.getFilesDir(),"snotz"));
                        Utils.reloadUI(activity);
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
        int extraNoteId = activity.getIntent().getIntExtra(sNotzWidgets.getNoteID(), sNotzWidgets.getInvalidNoteId());
        String extraCheckListPath = activity.getIntent().getStringExtra(sNotzWidgets.getChecklistPath());
        String externalNote = activity.getIntent().getStringExtra(sNotzUtils.getExternalNote());

        Intent mainActivity = new Intent(activity, MainActivity.class);
        if (extraCheckListPath != null) {
            mainActivity.putExtra(sNotzWidgets.getChecklistPath(), extraCheckListPath);
        } else if (extraNoteId != sNotzWidgets.getInvalidNoteId()) {
            mainActivity.putExtra(sNotzWidgets.getNoteID(), extraNoteId);
        } else if (externalNote != null) {
            mainActivity.putExtra(sNotzUtils.getExternalNote(), externalNote);
        }

        // Manage auto-backup
        File sNotz = new File(activity.getFilesDir(),"snotz");
        File sNotzAutoBackup = new File(activity.getExternalFilesDir("autoBackup"),"autoBackup");

        if (sNotz.exists() && sNotzData.getRawData(activity).size() > 0 && sNotz.length() != sNotzAutoBackup.length()) {
            Executors.newSingleThreadExecutor().execute(() -> sUtils.copy(sNotz, sNotzAutoBackup));
        } else if ((!sNotz.exists() || sNotzData.getRawData(activity).size() == 0) && sNotzAutoBackup.exists()) {
            new MaterialAlertDialogBuilder(activity)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(R.string.app_name)
                    .setMessage(activity.getString(R.string.restore_backup_message))
                    .setCancelable(false)
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                        sUtils.delete(sNotzAutoBackup);
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
                                    sUtils.copy(sNotzAutoBackup, sNotz);
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

        activity.startActivity(mainActivity);
        activity.finish();
    }

}