package com.sunilpaulmathew.snotz.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.sunilpaulmathew.snotz.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2020
 */

public class CreateNoteActivity extends AppCompatActivity {

    private AppCompatEditText mContents;
    private NestedScrollView mScrollView;
    private String mExternalNote = null, mJSONNew, mNote = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createnote);
        AppCompatImageButton mBack = findViewById(R.id.back_button);
        mBack.setOnClickListener(v -> onBackPressed());
        AppCompatImageButton mSave = findViewById(R.id.save_button);
        mContents = findViewById(R.id.contents);
        mScrollView = findViewById(R.id.scroll_view);
        Snackbar snackBar = Snackbar.make(mScrollView, getString(R.string.note_invalid_warning), Snackbar.LENGTH_INDEFINITE);
        mScrollView.setBackgroundColor(sNotzColor.setAccentColor("note_background", this));
        mContents.setTextColor(sNotzColor.setAccentColor("text_color", this));
        mContents.setHintTextColor(Utils.isDarkTheme(this) ? Color.WHITE : Color.BLACK);

        // Handle notes picked from File Manager
        if (getIntent().getData() != null) {
            Uri uri = getIntent().getData();
            assert uri != null;
            File file = new File(Objects.requireNonNull(uri.getPath()));
            if (Utils.isDocumentsUI(uri)) {
                @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    mExternalNote = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" +
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } else {
                mExternalNote = Utils.getPath(file);
            }
            if (mExternalNote != null && Utils.existFile(mExternalNote)) {
                if (Utils.isPermissionDenied(this)) {
                    ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    finish();
                }
                if (sNotz.validBackup(Utils.readFile(mExternalNote))) {
                    new MaterialAlertDialogBuilder(this)
                            .setMessage(getString(R.string.restore_notes_question, new File(mExternalNote).getName()))
                            .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                            })
                            .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                                if (Utils.existFile(getFilesDir().getPath() + "/snotz")) {
                                    Utils.create(Objects.requireNonNull(Utils.readFile(getFilesDir().getPath() + "/snotz")).replace("}]", "}," +
                                            sNotz.getNotesFromBackup(Utils.readFile(mExternalNote)) + "]"), getFilesDir().getPath() + "/snotz");
                                } else {
                                    Utils.create(Utils.readFile(mExternalNote), getFilesDir().getPath() + "/snotz");
                                }
                                Utils.restartApp(this);
                            })
                            .show();
                } else {
                    mContents.setText(Utils.readFile(mExternalNote));
                    mNote = Utils.readFile(mExternalNote);
                }
            } else {
                new MaterialAlertDialogBuilder(this)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(R.string.note_editor)
                        .setMessage(getString(R.string.file_path_error))
                        .setCancelable(false)
                        .setPositiveButton(R.string.cancel, (dialogInterface, i) -> {
                            finish();
                        }).show();
            }
        } else if (Utils.mName != null) {
            mContents.setText(sNotz.getNote(Utils.mName));
            mNote = sNotz.getNote(Utils.mName);
        }

        mContents.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (sNotz.isNoteInvalid(s.toString())) {
                    mContents.setTextColor(Color.RED);
                    snackBar.setAction(R.string.dismiss, v -> snackBar.dismiss());
                    snackBar.show();
                } else {
                    snackBar.dismiss();
                    mContents.setTextColor(sNotzColor.setAccentColor("text_color", CreateNoteActivity.this));
                }
            }
        });
        mContents.setOnClickListener(v -> {
            mScrollView.setAlpha(1);
        });

        mBack.setOnClickListener(v -> onBackPressed());
        mSave.setOnClickListener(v -> {
            if (mContents.getText() == null || mContents.getText().toString().isEmpty()) {
                Utils.showSnackbar(mScrollView, getString(R.string.text_empty));
                return;
            }
            if (sNotz.isNoteInvalid(mContents.getText().toString())) {
                new MaterialAlertDialogBuilder(this)
                        .setMessage(R.string.note_saving_error)
                        .setPositiveButton(R.string.dismiss, (dialog, which) -> {
                        })
                        .show();
                return;
            }
            String mJSON = getFilesDir().getPath() + "/snotz";
            if (Utils.mName != null) {
                mJSONNew = Objects.requireNonNull(Utils.readFile(mJSON))
                        .replace(Utils.mName,"{\"note\":\"" + mContents.getText() +
                        "\",\"date\":\"" + DateFormat.getDateTimeInstance().format(System.currentTimeMillis()) +
                        "\",\"hidden\":" + sNotz.isHidden(Utils.mName) + "}");
            } else if (Utils.existFile(mJSON)) {
                try {
                    JSONObject note = new JSONObject();
                    note.put("note", mContents.getText());
                    note.put("date", DateFormat.getDateTimeInstance().format(System.currentTimeMillis()));
                    note.put("hidden", false);
                    String newNote = note.toString();
                    mJSONNew = Objects.requireNonNull(Utils.readFile(mJSON))
                                .replace("}]", "}," + newNote + "]");
                } catch (JSONException ignored) {
                }
            } else {
                try {
                    JSONObject obj = new JSONObject();
                    JSONArray sNotz = new JSONArray();
                    JSONObject note = new JSONObject();
                    note.put("note", mContents.getText());
                    note.put("date", DateFormat.getDateTimeInstance().format(System.currentTimeMillis()));
                    note.put("hidden", false);
                    sNotz.put(note);
                    obj.put("sNotz", sNotz);
                    mJSONNew = obj.toString();
                } catch (JSONException ignored) {
                }
            }
            Utils.create(mJSONNew, mJSON);
            if (mExternalNote != null) {
                Utils.restartApp(this);
            } else {
                Utils.reloadUI(this);
            }
            finish();
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        Utils.toggleKeyboard(mContents, this);
        Utils.showSnackbar(mScrollView, getString(R.string.click_again_message));
    }

    @Override
    public void onBackPressed() {
        if (mNote != null && mContents.getText() != null && !mNote.equals(mContents.getText().toString()) || mNote == null
                && mContents.getText() != null && !mContents.getText().toString().isEmpty()) {
            new MaterialAlertDialogBuilder(this)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(R.string.app_name)
                    .setMessage(getString(R.string.discard_note))
                    .setCancelable(false)
                    .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                    })
                    .setPositiveButton(R.string.discard, (dialogInterface, i) -> finish()).show();
            return;
        }
        if (Utils.mName != null) Utils.mName = null;
        super.onBackPressed();
    }

}