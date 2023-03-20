package com.sunilpaulmathew.snotz.utils.dialogs;

import android.content.Context;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.snotz.R;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on March 18, 2023
 */
public abstract class DeleteNoteDialog {

    public DeleteNoteDialog(String note, Context context) {
        String[] sNotzContents = note.split("\\s+");
        new MaterialAlertDialogBuilder(context)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.warning)
                .setMessage(context.getString(R.string.delete_sure_question, sNotzContents.length <= 2 ?
                        note : sNotzContents[0] + " " + sNotzContents[1] + " " + sNotzContents[2] + "..."))
                .setCancelable(false)
                .setNegativeButton(R.string.cancel, (dialog, which) -> negativeButtonLister())
                .setPositiveButton(R.string.delete, (dialog, which) -> positiveButtonLister())
                .show();
    }

    public abstract void negativeButtonLister();

    public abstract void positiveButtonLister();

}