package com.sunilpaulmathew.snotz.utils;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.activities.StartActivity;
import com.sunilpaulmathew.snotz.interfaces.EditTextInterface;

import java.io.File;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
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
            } else if (CheckLists.isValidCheckList(mNote)) {
                importCheckList();
            } else {
                Intent mIntent = new Intent(mActivity, StartActivity.class);
                mIntent.putExtra(sNotzUtils.getExternalNote(), mNote);
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
                        JsonObject note = new JsonObject();
                        note.addProperty("note", items.getNote());
                        note.addProperty("date", items.getTimeStamp());
                        note.addProperty("image", items.getImageString());
                        note.addProperty("hidden", items.isHidden());
                        note.addProperty("colorBackground", items.getColorBackground());
                        note.addProperty("colorText", items.getColorText());
                        note.addProperty("noteID", items.getNoteID());
                        mJSONArray.add(note);
                    }
                    i = sNotzUtils.generateNoteID(mActivity);
                }

                for (sNotzItems items : sNotzUtils.getNotesFromBackup(mNewNote, mActivity)) {
                    JsonObject note = new JsonObject();
                    note.addProperty("note", items.getNote());
                    note.addProperty("date", items.getTimeStamp());
                    note.addProperty("image", items.getImageString());
                    note.addProperty("hidden", items.isHidden());
                    note.addProperty("colorBackground", items.getColorBackground());
                    note.addProperty("colorText", items.getColorText());
                    note.addProperty("noteID", i);
                    i++;
                    mJSONArray.add(note);
                }
                mJSONObject.add("sNotz", mJSONArray);
                sFileUtils.create(mJSONObject.toString(), new File(mActivity.getFilesDir(),"snotz"));
            }

            @Override
            public void onPostExecute() {
                mProgressBar.setVisibility(View.GONE);
                Utils.restartApp(mActivity);
            }
        };
    }
    private void importCheckList() {
        new EditTextInterface(null, mActivity.getString(R.string.check_list_import_question), mActivity) {

            @Override
            public void positiveButtonLister(Editable s) {
                if (s != null && !s.toString().trim().isEmpty()) {
                    if (sFileUtils.exist(new File(mActivity.getExternalFilesDir("checklists"), s.toString().trim()))) {
                        new MaterialAlertDialogBuilder(mActivity)
                                .setMessage(mActivity.getString(R.string.check_list_exist_warning))
                                .setNegativeButton(mActivity.getString(R.string.change_name), (dialogInterface, i) -> importCheckList())
                                .setPositiveButton(mActivity.getString(R.string.replace), (dialogInterface, i) ->
                                        launchCheckList(mActivity.getExternalFilesDir("checklists") + "/" + s.toString().trim()))
                                .show();
                    } else {
                        launchCheckList(mActivity.getExternalFilesDir("checklists") + "/" + s.toString().trim());
                        mActivity.finish();
                    }
                } else {
                    sCommonUtils.toast(mActivity.getString(R.string.check_list_name_empty_message), mActivity).show();
                }
            }
        }.show();
    }

    private void launchCheckList(String path) {
        sFileUtils.create(mNote, new File(path));
        Intent mIntent = new Intent(mActivity, StartActivity.class);
        mIntent.putExtra(sNotzWidgets.getChecklistPath(), path);
        mActivity.startActivity(mIntent);
        mActivity.finish();
    }

}