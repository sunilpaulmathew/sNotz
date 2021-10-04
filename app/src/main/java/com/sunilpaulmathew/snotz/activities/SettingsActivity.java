package com.sunilpaulmathew.snotz.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.snotz.BuildConfig;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.adapters.SettingsAdapter;
import com.sunilpaulmathew.snotz.utils.Common;
import com.sunilpaulmathew.snotz.utils.SettingsItems;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.Executor;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 17, 2020
 */
public class SettingsActivity extends AppCompatActivity {

    private AppCompatImageButton mBack;
    private final ArrayList <SettingsItems> mData = new ArrayList<>();
    private String mJSONString = null;

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mBack = findViewById(R.id.back_button);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        SettingsAdapter mRecycleViewAdapter = new SettingsAdapter(mData);
        mRecyclerView.setAdapter(mRecycleViewAdapter);

        mData.add(new SettingsItems(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")", "Copyright: © 2021-2022, sunilpaulmathew", getResources().getDrawable(R.drawable.ic_info), null));
        mData.add(new SettingsItems(getString(R.string.biometric_lock), getString(R.string.biometric_lock_summary), getResources().getDrawable(R.drawable.ic_fingerprint), null));
        mData.add(new SettingsItems(getString(R.string.show_hidden_notes), getString(R.string.show_hidden_notes_summary), getResources().getDrawable(R.drawable.ic_eye), null));
        mData.add(new SettingsItems(getString(R.string.note_color_background), getString(R.string.color_select_dialog, getString(R.string.note_color_background)), getResources().getDrawable(R.drawable.ic_color), null));
        mData.add(new SettingsItems(getString(R.string.note_color_text), getString(R.string.color_select_dialog, getString(R.string.note_color_text)), getResources().getDrawable(R.drawable.ic_text), null));
        mData.add(new SettingsItems(getString(R.string.backup_notes), getString(R.string.backup_notes_summary), getResources().getDrawable(R.drawable.ic_backup), null));
        mData.add(new SettingsItems(getString(R.string.restore_notes), getString(R.string.restore_notes_summary), getResources().getDrawable(R.drawable.ic_restore), null));
        mData.add(new SettingsItems(getString(R.string.clear_notes), getString(R.string.clear_notes_summary), getResources().getDrawable(R.drawable.ic_clear), null));
        mData.add(new SettingsItems(getString(R.string.donations), getString(R.string.donations_summary), getResources().getDrawable(R.drawable.ic_donate), null));
        mData.add(new SettingsItems(getString(R.string.invite_friends), getString(R.string.invite_friends_Summary), getResources().getDrawable(R.drawable.ic_share), null));
        mData.add(new SettingsItems(getString(R.string.welcome_note), getString(R.string.welcome_note_summary), getResources().getDrawable(R.drawable.ic_home), null));
        mData.add(new SettingsItems(getString(R.string.translations), getString(R.string.translations_summary), getResources().getDrawable(R.drawable.ic_translate), "https://poeditor.com/join/project?hash=LOg2GmFfbV"));
        mData.add(new SettingsItems(getString(R.string.rate_us), getString(R.string.rate_us_Summary), getResources().getDrawable(R.drawable.ic_rate), "https://play.google.com/store/apps/details?id=com.sunilpaulmathew.snotz"));
        mData.add(new SettingsItems(getString(R.string.support), getString(R.string.support_summary), getResources().getDrawable(R.drawable.ic_support), "https://t.me/smartpack_kmanager"));
        mData.add(new SettingsItems(getString(R.string.faq), getString(R.string.faq_summary), getResources().getDrawable(R.drawable.ic_faq), "https://ko-fi.com/post/sNotz-FAQ-H2H42H6A8"));

        mRecycleViewAdapter.setOnItemClickListener((position, v) -> {
            if (mData.get(position).getUrl() != null) {
                Utils.launchURL(mBack, mData.get(position).getUrl(), this);
            } else if (position == 0) {
                Intent settings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                settings.setData(uri);
                startActivity(settings);
                finish();
            } else if (position == 1) {
                if (Utils.isFingerprintAvailable(this)) {
                    Common.getBiometricPrompt().authenticate(Utils.showBiometricPrompt(this));
                } else {
                    Utils.showSnackbar(mRecyclerView, getString(R.string.biometric_lock_unavailable));
                }
                mRecycleViewAdapter.notifyItemChanged(position);
            } else if (position == 2) {
                if (Utils.getBoolean("use_biometric", false, this) && Utils.isFingerprintAvailable(this)) {
                    Common.isHiddenNote(true);
                    Common.getBiometricPrompt().authenticate(Utils.showBiometricPrompt(this));
                } else {
                    Utils.saveBoolean("hidden_note", !Utils.getBoolean("hidden_note", false, this), this);
                    Common.isHiddenNote(false);
                    Utils.reloadUI(this).execute();
                }
                mRecycleViewAdapter.notifyItemChanged(position);
            } else if (position == 3) {
                ColorPickerDialogBuilder
                        .with(this)
                        .setTitle(R.string.choose_color)
                        .initialColor(Utils.getInt("accent_color", getResources().getColor(R.color.color_teal), this))
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(selectedColor -> {
                        })
                        .setPositiveButton(R.string.ok, (dialog, selectedColor, allColors) -> {
                            Utils.saveInt("accent_color", selectedColor, this);
                            Utils.reloadUI(this).execute();
                            Common.isReloading(true);
                        })
                        .setNegativeButton(R.string.cancel, (dialog, which) -> {
                        }).build().show();
            } else if (position == 4) {
                ColorPickerDialogBuilder
                        .with(this)
                        .setTitle(R.string.choose_color)
                        .initialColor(Utils.getInt("text_color", getResources().getColor(R.color.color_white), this))
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(selectedColor -> {
                        })
                        .setPositiveButton(R.string.ok, (dialog, selectedColor, allColors) -> {
                            Utils.saveInt("text_color", selectedColor, this);
                            Utils.reloadUI(this).execute();
                            Common.isReloading(true);
                        })
                        .setNegativeButton(R.string.cancel, (dialog, which) -> {
                        }).build().show();
            } else if (position == 5) {
                if (Utils.exist(getFilesDir().getPath() + "/snotz")) {
                    new MaterialAlertDialogBuilder(this).setItems(getResources().getStringArray(
                            R.array.backup_options), (dialogInterface, i) -> {
                        switch (i) {
                            case 0:
                                saveDialog(".backup", Utils.read(getFilesDir().getPath() + "/snotz"));
                                break;
                            case 1:
                                saveDialog(".txt", sNotzUtils.sNotzToText(this));
                                break;
                        }
                    }).setOnDismissListener(dialogInterface -> {
                    }).show();
                } else {
                    Utils.showSnackbar(mRecyclerView, getString(R.string.note_list_empty));
                }
            } else if (position == 6) {
                if (Utils.isPermissionDenied(this)) {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    Intent restore = new Intent(Intent.ACTION_GET_CONTENT);
                    restore.setType("*/*");
                    restore.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(restore, 0);
                }
            } else if (position == 7) {
                if (Utils.exist(getFilesDir().getPath() + "/snotz")) {
                    new MaterialAlertDialogBuilder(this)
                            .setMessage(getString(R.string.clear_notes_message))
                            .setNegativeButton(R.string.cancel, (dialog, which) -> {
                            })
                            .setPositiveButton(R.string.delete, (dialog, which) -> {
                                Utils.delete(getFilesDir().getPath() + "/snotz");
                                Utils.reloadUI(this).execute();
                                onBackPressed();
                            })
                            .show();
                } else {
                    Utils.showSnackbar(mRecyclerView, getString(R.string.note_list_empty));
                }
            } else if (position == 8) {
                Intent donations = new Intent(this, BillingActivity.class);
                startActivity(donations);
                finish();
            } else if (position == 9) {
                Intent share_app = new Intent();
                share_app.setAction(Intent.ACTION_SEND);
                share_app.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                share_app.putExtra(Intent.EXTRA_TEXT, getString(R.string.shared_by_message, BuildConfig.VERSION_NAME));
                share_app.setType("text/plain");
                Intent shareIntent = Intent.createChooser(share_app, getString(R.string.share_with));
                startActivity(shareIntent);
            } else if (position == 10) {
                Intent welcome = new Intent(this, WelcomeActivity.class);
                startActivity(welcome);
                finish();
            }
        });

        mBack.setOnClickListener(v -> onBackPressed());

        Executor executor = ContextCompat.getMainExecutor(this);
        Common.mBiometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Utils.showSnackbar(mBack, getString(R.string.authentication_error, errString));
                mRecycleViewAdapter.notifyItemRangeChanged(1,2);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                if (Common.isHiddenNote()) {
                    Utils.saveBoolean("hidden_note", !Utils.getBoolean("hidden_note", false, SettingsActivity.this), SettingsActivity.this);
                    Common.isHiddenNote(false);
                    Utils.reloadUI(SettingsActivity.this).execute();
                } else {
                    Utils.useBiometric(mBack, SettingsActivity.this);
                }
                mRecycleViewAdapter.notifyItemRangeChanged(1,2);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Utils.showSnackbar(mBack, getString(R.string.authentication_failed));
                mRecycleViewAdapter.notifyItemRangeChanged(1,2);
            }
        });

        Utils.showBiometricPrompt(this);
    }

    private void saveDialog(String type, String sNotz) {
        if (Build.VERSION.SDK_INT < 30 && Utils.isPermissionDenied(this)) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
        }
        Utils.dialogEditText(null,
                (dialogInterface, i) -> {
                }, text -> {
                    if (text.isEmpty()) {
                        Utils.showSnackbar(mBack, getString(R.string.text_empty));
                        return;
                    }
                    if (!text.endsWith(type)) {
                        text += type;
                    }
                    if (text.contains(" ")) {
                        text = text.replace(" ", "_");
                    }
                    if (Build.VERSION.SDK_INT >= 30) {
                        try {
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.MediaColumns.DISPLAY_NAME, text);
                            values.put(MediaStore.MediaColumns.MIME_TYPE, "*/*");
                            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                            Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                            OutputStream outputStream = getContentResolver().openOutputStream(uri);
                            outputStream.write(sNotz.getBytes());
                            outputStream.close();
                        } catch (IOException ignored) {
                        }
                    } else {
                        Utils.create(sNotz, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + text);
                    }
                    Utils.showSnackbar(mBack, getString(R.string.backup_notes_message, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + text));
                }, this).setOnDismissListener(dialogInterface -> {
        }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent restore = new Intent(Intent.ACTION_GET_CONTENT);
            restore.setType("*/*");
            restore.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(restore, 0);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            assert uri != null;
            File mSelectedFile = null;
            if (Utils.isDocumentsUI(uri)) {
                @SuppressLint("Recycle")
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    mSelectedFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
                }
            } else {
                mSelectedFile = new File(uri.getPath());
            }
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                for (int result = bis.read(); result != -1; result = bis.read()) {
                    buf.write((byte) result);
                }
                mJSONString = buf.toString("UTF-8");
            } catch (IOException ignored) {}

            if (mJSONString == null || !sNotzUtils.validBackup(mJSONString)) {
                Utils.showSnackbar(mBack, getString(R.string.restore_error));
                return;
            }
            new MaterialAlertDialogBuilder(this)
                    .setMessage(getString(R.string.restore_notes_question, mSelectedFile != null ?
                            mSelectedFile.getName() : "backup"))
                    .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    })
                    .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                        sNotzUtils.restoreNotes(mJSONString, this);
                        Utils.reloadUI(this).execute();
                        finish();
                    }).show();
        }
    }

}