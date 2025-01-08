package com.sunilpaulmathew.snotz.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sunilpaulmathew.snotz.MainActivity;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.activities.StartActivity;
import com.sunilpaulmathew.snotz.utils.serializableItems.sNotzItems;

import java.io.File;

import in.sunilpaulmathew.sCommon.CommonUtils.sExecutor;
import in.sunilpaulmathew.sCommon.FileUtils.sFileUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 30, 2021
 */
public class NotePicker {

    private final Activity mActivity;
    private final ProgressBar mProgressBar;
    private final String mNote;
    private String mNewNote = null;

    public NotePicker(String note, ProgressBar progressBar, Activity activity) {
        this.mNote = note;
        this.mProgressBar = progressBar;
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
            } catch (IllegalArgumentException ignored) {}
            if (mNewNote != null) {
                new MaterialAlertDialogBuilder(mActivity)
                        .setCancelable(false)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(R.string.app_name)
                        .setMessage(mActivity.getString(R.string.restore_notes_question))
                        .setNegativeButton(mActivity.getString(R.string.cancel), (dialogInterface, i) -> mActivity.finish())
                        .setPositiveButton(mActivity.getString(R.string.yes), (dialogInterface, i) ->
                                restoreNote().execute())
                        .show();
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

    private sExecutor restoreNote() {
        return new sExecutor() {
            private int i = 0;

            @Override
            public void onPreExecute() {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void doInBackground() {
                JsonObject mJSONObject = new JsonObject();
                JsonArray mJSONArray = new JsonArray();
                if (sFileUtils.exist(new File(mActivity.getFilesDir(),"snotz"))) {
                    for (sNotzItems items : sNotzData.getRawData(mActivity)) {
                        mJSONArray.add(sNotzUtils.getNote(Integer.MIN_VALUE, items));
                    }
                    i = sNotzUtils.generateNoteID(mActivity);
                }

                for (sNotzItems items : sNotzUtils.getNotesFromBackup(mNewNote, mActivity)) {
                    mJSONArray.add(sNotzUtils.getNote(i, items));
                    i++;
                }
                mJSONObject.add("sNotz", mJSONArray);
                sFileUtils.create(mJSONObject.toString(), new File(mActivity.getFilesDir(),"snotz"));
            }

            @Override
            public void onPostExecute() {
                mProgressBar.setVisibility(View.GONE);
                Intent intent = new Intent(mActivity, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mActivity.startActivity(intent);
                mActivity.finish();
            }
        };
    }

}