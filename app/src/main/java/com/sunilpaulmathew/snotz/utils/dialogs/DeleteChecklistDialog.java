package com.sunilpaulmathew.snotz.utils.dialogs;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.Utils;

import java.io.File;

import in.sunilpaulmathew.sCommon.FileUtils.sFileUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on March 18, 2023
 */
public abstract class DeleteChecklistDialog {

    public DeleteChecklistDialog(File checklist, Context context) {
        new MaterialAlertDialogBuilder(context)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.warning)
                .setMessage(context.getString(R.string.delete_sure_question, checklist.getName()))
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> negativeButtonLister())
                .setCancelable(false)
                .setPositiveButton(R.string.delete, (dialogInterface, i) -> {
                    sFileUtils.delete(checklist);
                    Utils.reloadUI(context);
                }).show();
    }

    public abstract void negativeButtonLister();

}