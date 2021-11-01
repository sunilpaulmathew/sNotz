package com.sunilpaulmathew.snotz.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.activities.StartActivity;
import com.sunilpaulmathew.snotz.interfaces.DialogEditTextListener;

import java.io.File;

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

    private AsyncTasks restoreNote() {
        return new AsyncTasks() {
            private int i = 0;

            @Override
            public void onPreExecute() {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void doInBackground() {
                JsonObject mJSONObject = new JsonObject();
                JsonArray mJSONArray = new JsonArray();
                if (Utils.exist(mActivity.getFilesDir().getPath() + "/snotz")) {
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
                Utils.create(mJSONObject.toString(), mActivity.getFilesDir().getPath() + "/snotz");
            }

            @Override
            public void onPostExecute() {
                mProgressBar.setVisibility(View.GONE);
                Utils.restartApp(mActivity);
            }
        };
    }
    private void importCheckList() {
        DialogEditTextListener.dialogEditText(null, mActivity.getString(R.string.check_list_import_question),
                (dialogInterface, i) -> {
                }, text -> {
                    if (text.isEmpty()) {
                        makeToast(R.string.check_list_name_empty_message).show();
                        return;
                    }
                    if (Utils.exist(new File(mActivity.getExternalFilesDir("checklists"), text).getAbsolutePath())) {
                        new MaterialAlertDialogBuilder(mActivity)
                                .setMessage(mActivity.getString(R.string.check_list_exist_warning))
                                .setNegativeButton(mActivity.getString(R.string.change_name), (dialogInterface, i) -> importCheckList())
                                .setPositiveButton(mActivity.getString(R.string.replace), (dialogInterface, i) ->
                                        launchCheckList(mActivity.getExternalFilesDir("checklists") + "/" + text))
                                .show();
                        return;
                    }
                    launchCheckList(mActivity.getExternalFilesDir("checklists") + "/" + text);
                    mActivity.finish();
                }, -1, mActivity).setOnDismissListener(dialogInterface -> mActivity.finish()).show();
    }

    private void launchCheckList(String path) {
        Utils.create(mNote, path);
        Intent mIntent = new Intent(mActivity, StartActivity.class);
        mIntent.putExtra(sNotzWidgets.getChecklistPath(), path);
        mActivity.startActivity(mIntent);
        mActivity.finish();
    }

    private Toast makeToast(int message) {
        return Toast.makeText(mActivity, message, Toast.LENGTH_LONG);
    }

}