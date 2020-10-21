/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.snotz.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sunilpaulmathew.snotz.BuildConfig;
import com.sunilpaulmathew.snotz.R;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.Executor;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 17, 2020
 */

public class SettingsActivity extends AppCompatActivity {

    private AppCompatImageButton mBack;
    private BiometricPrompt mBiometricPrompt;
    private CheckBox mCheckBoxBiometric;
    private CheckBox mCheckBoxHidden;
    private String mPath;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mBack = findViewById(R.id.back_button);
        AppCompatTextView mAppName = findViewById(R.id.app_name);
        AppCompatTextView mApID = findViewById(R.id.app_id);
        FrameLayout mAppTitle = findViewById(R.id.app_title);
        FrameLayout mBiometric = findViewById(R.id.biometric_lock);
        FrameLayout mHiddenNotes = findViewById(R.id.hidden_notes);
        FrameLayout mBgColor = findViewById(R.id.background_color);
        FrameLayout mTxtColor = findViewById(R.id.text_color);
        FrameLayout mBackupNotes = findViewById(R.id.backup_notes);
        FrameLayout mRestoreNotes = findViewById(R.id.restore_notes);
        FrameLayout mClearNotes = findViewById(R.id.clear_notes);
        FrameLayout mInvite = findViewById(R.id.invite_friends);
        FrameLayout mRateApp = findViewById(R.id.rate_us);
        FrameLayout mSupport = findViewById(R.id.support);
        FrameLayout mFAQ = findViewById(R.id.faq);
        mCheckBoxBiometric = findViewById(R.id.checkbox_biometric);
        mCheckBoxHidden = findViewById(R.id.checkbox_hidden);
        AppCompatTextView mBgColorSummary = findViewById(R.id.background_color_summary);
        AppCompatTextView mTxtColorSummary = findViewById(R.id.text_color_summary);

        if (!Utils.isFingerprintAvailable(this)) {
            mBiometric.setVisibility(View.GONE);
        }
        if (Utils.existFile(getFilesDir().getPath() + "/snotz")) {
            mBackupNotes.setVisibility(View.VISIBLE);
            mClearNotes.setVisibility(View.VISIBLE);
        } else {
            mRestoreNotes.setVisibility(View.VISIBLE);
        }

        mBack.setOnClickListener(v -> onBackPressed());
        mAppName.setText(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")");
        mAppTitle.setOnClickListener(v -> {
            Intent settings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
            settings.setData(uri);
            startActivity(settings);
            onBackPressed();
        });
        mApID.setText(BuildConfig.APPLICATION_ID);
        mBiometric.setOnClickListener(v -> mBiometricPrompt.authenticate(Utils.mPromptInfo));
        mCheckBoxBiometric.setChecked(Utils.getBoolean("use_biometric", false, this));
        mCheckBoxBiometric.setOnClickListener(v -> mBiometricPrompt.authenticate(Utils.mPromptInfo));
        mHiddenNotes.setOnClickListener(v -> hiddenNotes());
        mCheckBoxHidden.setChecked(Utils.getBoolean("hidden_note", false, this));
        mCheckBoxHidden.setOnClickListener(v -> hiddenNotes());
        mBgColor.setOnClickListener(v -> sNotzColor.colorDialog(sNotzColor.getColors(this).indexOf(sNotzColor.setAccentColor("note_background", this)), "note_background", this));
        mTxtColor.setOnClickListener(v -> {
            Utils.mTextColor = true;
            sNotzColor.colorDialog(sNotzColor.getColors(this).indexOf(sNotzColor.setAccentColor("text_color", this)), "text_color", this);
        });
        mBgColorSummary.setText(getString(R.string.color_select_dialog, getString(R.string.note_color_background)));
        mTxtColorSummary.setText(getString(R.string.color_select_dialog, getString(R.string.note_color_text)));

        mBackupNotes.setOnClickListener(v -> {
            Utils.create(Utils.readFile(getFilesDir().getPath() + "/snotz"), Environment.getExternalStorageDirectory().toString() + "/snotz.backup/");
            Utils.showSnackbar(mBack, getString(R.string.backup_notes_message, Environment.getExternalStorageDirectory().toString() + "/snotz.backup/"));
        });
        mRestoreNotes.setOnClickListener(v -> {
            if (Utils.isPermissionDenied(this)) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                Intent restore = new Intent(Intent.ACTION_GET_CONTENT);
                restore.setType("*/*");
                startActivityForResult(restore, 0);
            }
        });

        mClearNotes.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.clear_notes_message))
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    })
                    .setPositiveButton(R.string.delete, (dialog, which) -> {
                        Utils.deleteFile(getFilesDir().getPath() + "/snotz");
                        Utils.reloadUI(this);
                        onBackPressed();
                    })
                    .show();
        });
        mInvite.setOnClickListener(v -> {
            Intent share_app = new Intent();
            share_app.setAction(Intent.ACTION_SEND);
            share_app.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            share_app.putExtra(Intent.EXTRA_TEXT, getString(R.string.shared_by_message, BuildConfig.VERSION_NAME));
            share_app.setType("text/plain");
            Intent shareIntent = Intent.createChooser(share_app, getString(R.string.share_with));
            startActivity(shareIntent);
        });
        mRateApp.setOnClickListener(v -> Utils.launchURL(mBack, "https://play.google.com/store/apps/details?id=com.sunilpaulmathew.snotz", this));
        mSupport.setOnClickListener(v -> Utils.launchURL(mBack, "https://t.me/smartpack_kmanager", this));
        mFAQ.setOnClickListener(v -> Utils.launchURL(mBack, "https://ko-fi.com/post/sNotz-FAQ-H2H42H6A8", this));

        Executor executor = ContextCompat.getMainExecutor(this);
        mBiometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Utils.showSnackbar(mBack, getString(R.string.authentication_error, errString));
                mCheckBoxBiometric.setChecked(Utils.getBoolean("use_biometric", false, SettingsActivity.this));
                mCheckBoxHidden.setChecked(Utils.getBoolean("hidden_note", false, SettingsActivity.this));
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                if (Utils.mHiddenNotes) {
                    Utils.manageHiddenNotes(SettingsActivity.this);
                } else {
                    Utils.useBiometric(mBack, SettingsActivity.this);
                }
                mCheckBoxBiometric.setChecked(Utils.getBoolean("use_biometric", false, SettingsActivity.this));
                mCheckBoxHidden.setChecked(Utils.getBoolean("hidden_note", false, SettingsActivity.this));
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Utils.showSnackbar(mBack, getString(R.string.authentication_failed));
                mCheckBoxBiometric.setChecked(Utils.getBoolean("use_biometric", false, SettingsActivity.this));
                mCheckBoxHidden.setChecked(Utils.getBoolean("hidden_note", false, SettingsActivity.this));
            }
        });

        Utils.showBiometricPrompt(this);
    }

    private void hiddenNotes() {
        if (Utils.getBoolean("use_biometric", false, this) && Utils.isFingerprintAvailable(this)) {
            Utils.mHiddenNotes = true;
            mBiometricPrompt.authenticate(Utils.mPromptInfo);
        } else {
            Utils.manageHiddenNotes(this);
        }
        mCheckBoxHidden.setChecked(Utils.getBoolean("hidden_note", false, this));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            assert uri != null;
            File file = new File(Objects.requireNonNull(uri.getPath()));
            if (Utils.isDocumentsUI(uri)) {
                @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    mPath = Environment.getExternalStorageDirectory().toString() + "/Download/" +
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } else {
                mPath = Utils.getPath(file);
            }
            if (!sNotz.validBackup(mPath)) {
                Utils.showSnackbar(mBack, getString(R.string.restore_error));
                return;
            }
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.restore_notes_question, new File(mPath).getName()))
                    .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    })
                    .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                        Utils.create(Utils.readFile(mPath), getFilesDir().getPath() + "/snotz");
                        Utils.reloadUI(this);
                        onBackPressed();
                    })
                    .show();
        }
    }

}