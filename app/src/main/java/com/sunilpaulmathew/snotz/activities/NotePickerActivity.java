package com.sunilpaulmathew.snotz.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.interfaces.DialogEditTextListener;
import com.sunilpaulmathew.snotz.utils.AsyncTasks;
import com.sunilpaulmathew.snotz.utils.CheckLists;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzData;
import com.sunilpaulmathew.snotz.utils.sNotzItems;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;
import com.sunilpaulmathew.snotz.utils.sNotzWidgets;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2020
 */
public class NotePickerActivity extends AppCompatActivity {

    private String mNote = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_bar);

        ProgressBar mProgressBar = findViewById(R.id.progress);

        if (getIntent().getData() != null) {
            // Handle notes picked from File Manager
            try {
                InputStream inputStream = getContentResolver().openInputStream(getIntent().getData());
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                for (int result = bis.read(); result != -1; result = bis.read()) {
                    buf.write((byte) result);
                }
                mNote = buf.toString("UTF-8");
            } catch (IOException ignored) {}

            if (mNote != null) {
                if (sNotzUtils.validBackup(mNote)) {
                    new MaterialAlertDialogBuilder(this)
                            .setCancelable(false)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(R.string.app_name)
                            .setMessage(getString(R.string.restore_notes_question))
                            .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> finish())
                            .setPositiveButton(getString(R.string.yes), (dialogInterface, i) ->
                                    new AsyncTasks() {
                                        private Activity mActivity = null;
                                        private int i = 0;

                                        @Override
                                        public void onPreExecute() {
                                            mActivity = NotePickerActivity.this;
                                            mProgressBar.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void doInBackground() {
                                            try {
                                                TimeUnit.SECONDS.sleep(5);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            JsonObject mJSONObject = new JsonObject();
                                            JsonArray mJSONArray = new JsonArray();
                                            if (Utils.exist(getFilesDir().getPath() + "/snotz")) {
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

                                            if (sNotzUtils.validBackup(mNote)) {
                                                for (sNotzItems items : sNotzUtils.getNotesFromBackup(mNote, mActivity)) {
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
                                            }
                                            mJSONObject.add("sNotz", mJSONArray);
                                            Utils.create(mJSONObject.toString(), getFilesDir().getPath() + "/snotz");
                                        }

                                        @Override
                                        public void onPostExecute() {
                                            mProgressBar.setVisibility(View.GONE);
                                            Utils.restartApp(mActivity);
                                        }
                                    }.execute())
                            .show();
                } else if (CheckLists.isValidCheckList(mNote)) {
                    importCheckList();
                } else {
                    Intent mIntent = new Intent(this, StartActivity.class);
                    mIntent.putExtra(sNotzUtils.getExternalNote(), mNote);
                    startActivity(mIntent);
                    finish();
                }
            } else {
                new MaterialAlertDialogBuilder(this)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(R.string.note_editor)
                        .setMessage(getString(R.string.file_path_error))
                        .setCancelable(false)
                        .setPositiveButton(R.string.cancel, (dialogInterface, i) -> finish()).show();
            }
        }
    }
    private void importCheckList() {
        DialogEditTextListener.dialogEditText(null, getString(R.string.check_list_import_question),
                (dialogInterface, i) -> {
                }, text -> {
                    if (text.isEmpty()) {
                        makeToast(R.string.check_list_name_empty_message).show();
                        return;
                    }
                    if (Utils.exist(new File(getExternalFilesDir("checklists"), text).getAbsolutePath())) {
                        new MaterialAlertDialogBuilder(this)
                                .setMessage(getString(R.string.check_list_exist_warning))
                                .setNegativeButton(getString(R.string.change_name), (dialogInterface, i) -> importCheckList())
                                .setPositiveButton(getString(R.string.replace), (dialogInterface, i) ->
                                        launchCheckList(getExternalFilesDir("checklists") + "/" + text))
                                .show();
                        return;
                    }
                    launchCheckList(getExternalFilesDir("checklists") + "/" + text);
                    finish();
                }, -1,this).setOnDismissListener(dialogInterface -> finish()).show();
    }

    private void launchCheckList(String path) {
        Utils.create(mNote, path);
        Intent mIntent = new Intent(this, StartActivity.class);
        mIntent.putExtra(sNotzWidgets.getChecklistPath(), path);
        startActivity(mIntent);
        finish();
    }

    private Toast makeToast(int message) {
        return Toast.makeText(this, message, Toast.LENGTH_LONG);
    }

}