package com.sunilpaulmathew.snotz.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.Common;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzColor;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;

import java.io.File;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2020
 */
public class CreateNoteActivity extends AppCompatActivity {

    private AppCompatEditText mContents;
    private NestedScrollView mScrollView;
    private String mExternalNote = null, mNote = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createnote);
        AppCompatImageButton mBack = findViewById(R.id.back_button);
        mBack.setOnClickListener(v -> onBackPressed());
        AppCompatImageButton mSave = findViewById(R.id.save_button);
        mContents = findViewById(R.id.contents);
        mScrollView = findViewById(R.id.scroll_view);
        mScrollView.setBackgroundColor(sNotzColor.getAccentColor(this));
        mContents.setTextColor(sNotzColor.getTextColor(this));
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
            if (mExternalNote != null && Utils.exist(mExternalNote)) {
                if (Build.VERSION.SDK_INT < 30 && Utils.isPermissionDenied(this)) {
                    LinearLayout mPermissionLayout = findViewById(R.id.permission_layout);
                    MaterialCardView mPermissionGrant = findViewById(R.id.grant_card);
                    MaterialTextView mPermissionText = findViewById(R.id.permission_text);
                    mPermissionText.setText(getString(R.string.permission_denied_message));
                    mPermissionLayout.setVisibility(View.VISIBLE);
                    mScrollView.setVisibility(View.GONE);
                    mSave.setVisibility(View.GONE);
                    mPermissionGrant.setOnClickListener(v -> {
                        ActivityCompat.requestPermissions(this, new String[] {
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        finish();
                    });
                    return;
                }
                if (sNotzUtils.validBackup(Utils.read(mExternalNote))) {
                    new MaterialAlertDialogBuilder(this)
                            .setMessage(getString(R.string.restore_notes_question, new File(mExternalNote).getName()))
                            .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                            })
                            .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                                sNotzUtils.restoreNotes(Utils.read(mExternalNote), this);
                                Utils.restartApp(this);
                            })
                            .show();
                } else {
                    mContents.setText(Utils.read(mExternalNote));
                    mNote = Utils.read(mExternalNote);
                }
            } else {
                new MaterialAlertDialogBuilder(this)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(R.string.note_editor)
                        .setMessage(getString(R.string.file_path_error))
                        .setCancelable(false)
                        .setPositiveButton(R.string.cancel, (dialogInterface, i) -> finish()).show();
            }
        } else if (Common.getNote() != null) {
            mContents.setText(Common.getNote());
            mNote = Common.getNote();
        }

        mContents.setOnClickListener(v -> mScrollView.setAlpha(1));

        mBack.setOnClickListener(v -> onBackPressed());
        mSave.setOnClickListener(v -> {
            if (mContents.getText() == null || mContents.getText().toString().trim().isEmpty()) {
                Utils.showSnackbar(mScrollView, getString(R.string.text_empty));
                return;
            }
            if (Common.getNote() != null) {
                sNotzUtils.updateNote(mContents.getText(), Common.getNote(), this);
            } else if (Utils.exist(getFilesDir().getPath() + "/snotz")) {
                sNotzUtils.addNote(mContents.getText(), this);
            } else {
                sNotzUtils.initializeNotes(mContents.getText(), this);
            }
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
        if (Common.getNote() != null) Common.setNote(null);
        super.onBackPressed();
    }

}