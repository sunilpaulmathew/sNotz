package com.sunilpaulmathew.snotz.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.snotz.MainActivity;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.adapters.SettingsAdapter;
import com.sunilpaulmathew.snotz.interfaces.DialogEditTextListener;

import java.io.File;

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
        return sUtils.read(new File(context.getCacheDir(),"pin"));
    }

    public static void removePIN(Context context) {
        sUtils.delete(new File(context.getCacheDir(),"pin"));
        sUtils.saveBoolean("use_pin", false, context);
    }

    public static void setPIN(String pin, Context context) {
        sUtils.create(pin, new File(context.getCacheDir(),"pin"));
    }

    public static void setPIN(boolean verify, String title, SettingsAdapter adapter, Activity activity) {
        DialogEditTextListener.dialogEditText(null, title,
                (dialogInterface, i) -> {
                }, text -> {
                    if (text.length() != 4) {
                        if (verify) {
                            removePIN(activity);
                        }
                        sUtils.snackBar(activity.findViewById(android.R.id.content), activity.getString(R.string.pin_length_warning)).show();
                    } else if (!verify) {
                        setPIN(text, activity);
                        setPIN(true, activity.getString(R.string.pin_reenter), adapter, activity);
                    } else if (!text.equals(getPIN(activity))) {
                        new MaterialAlertDialogBuilder(activity)
                                .setMessage(activity.getString(R.string.pin_mismatch_message))
                                .setCancelable(false)
                                .setNegativeButton(R.string.cancel, (dialog, which) -> removePIN(activity))
                                .setPositiveButton(R.string.try_again, (dialog, which) -> setPIN(true,
                                        activity.getString(R.string.pin_reenter), adapter, activity)).show();
                    } else {
                        sUtils.saveBoolean("use_pin", true, activity);
                        sUtils.snackBar(activity.findViewById(android.R.id.content), activity.getString(R.string.pin_protection_status,
                                activity.getString(R.string.activated))).show();
                        adapter.notifyItemChanged(3);
                    }
                }, InputType.TYPE_CLASS_NUMBER,activity).setOnDismissListener(dialogInterface -> {
        }).show();
    }

    public static void authenticate(SettingsAdapter adapter, int position, Activity activity) {
        DialogEditTextListener.dialogEditText(null, activity.getString(R.string.authenticate),
                (dialogInterface, i) -> {
                }, text -> {
                    if (!text.equals(getPIN(activity))) {
                        new MaterialAlertDialogBuilder(activity)
                                .setMessage(activity.getString(R.string.pin_mismatch_message))
                                .setCancelable(false)
                                .setNegativeButton(R.string.cancel, (dialog, which) -> activity.finish())
                                .setPositiveButton(R.string.try_again, (dialog, which) -> authenticate(adapter, position, activity)).show();
                    } else {
                        if (position == 4) {
                            sUtils.saveBoolean("hidden_note", !sUtils.getBoolean("hidden_note", false, activity), activity);
                        } else {
                            sUtils.delete(new File(activity.getFilesDir().getPath(),"snotz"));
                        }
                        Utils.reloadUI(activity);
                        if (adapter != null) {
                            adapter.notifyItemChanged(position);
                        }
                    }
                }, InputType.TYPE_CLASS_NUMBER,activity).setOnDismissListener(dialogInterface -> {
        }).show();
    }

    public static void authenticate(boolean remove, SettingsAdapter adapter, Activity activity) {
        DialogEditTextListener.dialogEditText(null, activity.getString(R.string.authenticate),
                (dialogInterface, i) -> {
                }, text -> {
                    if (!text.equals(getPIN(activity))) {
                        new MaterialAlertDialogBuilder(activity)
                                .setMessage(activity.getString(R.string.pin_mismatch_message))
                                .setCancelable(false)
                                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                                })
                                .setPositiveButton(R.string.try_again, (dialog, which) -> authenticate(remove, adapter, activity)).show();
                    } else {
                        if (remove) {
                            removePIN(activity);
                            adapter.notifyItemChanged(3);
                            sUtils.snackBar(activity.findViewById(android.R.id.content), activity.getString(R.string.pin_protection_status,
                                    activity.getString(R.string.deactivated))).show();
                        } else {
                            // Launch MainActivity
                            launchMainActivity(activity);
                        }
                    }
                }, InputType.TYPE_CLASS_NUMBER,activity).setOnDismissListener(dialogInterface -> {
                    if (!remove) {
                        activity.finish();
                    }
        }).show();
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
        activity.startActivity(mainActivity);
        activity.finish();
    }

}