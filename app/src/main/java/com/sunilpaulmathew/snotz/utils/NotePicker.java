package com.sunilpaulmathew.snotz.utils;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.snotz.MainActivity;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.activities.StartActivity;
import com.sunilpaulmathew.snotz.utils.dialogs.RestoreNoteDialog;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 30, 2021
 */
public class NotePicker {

    private final Activity mActivity;
    private final String mNote;
    private String mNewNote = null;

    public NotePicker(String note, Activity activity) {
        this.mNote = note;
        this.mActivity = activity;
    }

    public void handleNotes() {
        if (mNote != null) {
            try {
                if (sNotzUtils.validBackup(Encryption.decrypt(mNote))) {
                    mNewNote = Encryption.decrypt(mNote);
                } else if (sNotzUtils.validBackup(mNote)) {
                    mNewNote = mNote;
                }
            } catch (IllegalArgumentException ignored) {
            }

            if (mNewNote != null) {
                new RestoreNoteDialog(mNewNote, false, mActivity) {
                    @Override
                    public void negativeButtonLister() {
                    }

                    @Override
                    public void positiveButtonLister() {
                        Intent intent = new Intent(mActivity, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mActivity.startActivity(intent);
                        mActivity.finish();
                    }
                };
            } else {
                Intent mIntent = new Intent(mActivity, StartActivity.class);
                mIntent.putExtra("externalNote", mNote);
                mActivity.startActivity(mIntent);
                mActivity.finish();
            }
        } else {
            new MaterialAlertDialogBuilder(mActivity)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(R.string.note_editor)
                    .setMessage(mActivity.getString(R.string.file_path_error))
                    .setCancelable(false)
                    .setPositiveButton(R.string.cancel, (dialogInterface, i) -> mActivity.finish()).show();
        }
    }

}