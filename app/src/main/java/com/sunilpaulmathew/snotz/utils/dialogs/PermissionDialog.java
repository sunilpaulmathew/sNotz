package com.sunilpaulmathew.snotz.utils.dialogs;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on March 18, 2023
 */
public class PermissionDialog {

    private final MaterialAlertDialogBuilder mDialogBuilder;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public PermissionDialog(Context context) {
        mDialogBuilder = new MaterialAlertDialogBuilder(context)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.warning)
                .setMessage(context.getString(R.string.permission_notification_message))
                .setCancelable(false)
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                })
                .setPositiveButton(R.string.permission_request, (dialogInterface, i) -> Utils.requestPermission(new String[] {
                        Manifest.permission.POST_NOTIFICATIONS
                }, (Activity) context));
    }

    public void show() {
        mDialogBuilder.show();
    }

}