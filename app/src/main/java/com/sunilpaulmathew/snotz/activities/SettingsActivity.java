package com.sunilpaulmathew.snotz.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.BuildConfig;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.adapters.SettingsAdapter;
import com.sunilpaulmathew.snotz.utils.AppSettings;
import com.sunilpaulmathew.snotz.utils.CheckLists;
import com.sunilpaulmathew.snotz.utils.Encryption;
import com.sunilpaulmathew.snotz.utils.Security;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.dialogs.RestoreNoteDialog;
import com.sunilpaulmathew.snotz.utils.sNotzColor;
import com.sunilpaulmathew.snotz.utils.sNotzData;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;
import com.sunilpaulmathew.snotz.utils.serializableItems.SettingsEntry;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executor;

import in.sunilpaulmathew.colorpicker.ColorPickerDialog;
import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.Dialog.sSingleChoiceDialog;
import in.sunilpaulmathew.sCommon.FileUtils.sFileUtils;
import in.sunilpaulmathew.sCommon.ThemeUtils.sThemeUtils;
import in.sunilpaulmathew.sCommon.TranslatorUtils.sTranslatorUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 17, 2020
 */
public class SettingsActivity extends BaseActivity {

    private BiometricPrompt mBiometricPrompt;
    private boolean mClearNote = false, mHidden = false;
    private String mJSONString = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings, R.id.layout_main);

        MaterialTextView mTitle = findViewById(R.id.title);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        SettingsAdapter mRecycleViewAdapter = new SettingsAdapter(getData());
        mRecyclerView.setAdapter(mRecycleViewAdapter);

        mTitle.setTextColor(sNotzColor.getAppAccentColor(this));

        mRecycleViewAdapter.setOnItemClickListener((settingsEntry, position) -> {
            if (settingsEntry.isWeblink()) {
                sCommonUtils.launchUrl(settingsEntry.getUrl(), this);
            } else {
                int itemID = settingsEntry.getId();
                if (itemID == 1) {
                    Intent settings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                    settings.setData(uri);
                    startActivity(settings);
                    finish();
                } else if (itemID == 2) {
                    new sSingleChoiceDialog(R.drawable.ic_theme, getString(R.string.app_theme),
                            new String[] {
                                    getString(R.string.app_theme_auto),
                                    getString(R.string.app_theme_dark),
                                    getString(R.string.app_theme_light)
                            }, sCommonUtils.getInt("appTheme", 0, this), this) {

                        @Override
                        public void onItemSelected(int itemPosition) {
                            if (itemPosition == sCommonUtils.getInt("appTheme", 0, SettingsActivity.this)) {
                                return;
                            }
                            switch (itemPosition) {
                                case 2:
                                    sCommonUtils.saveInt("appTheme", 2, SettingsActivity.this);
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                    break;
                                case 1:
                                    sCommonUtils.saveInt("appTheme", 1, SettingsActivity.this);
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                    break;
                                default:
                                    sCommonUtils.saveInt("appTheme", 0, SettingsActivity.this);
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                                    break;
                            }
                            mRecycleViewAdapter.updateItem(position, sThemeUtils.getAppTheme(SettingsActivity.this));
                        }
                    }.show();
                } else if (itemID == 3) {
                    ColorPickerDialog
                            .with(this)
                            .setTitle(R.string.choose_color)
                            .initialColor(sNotzColor.getAppAccentColor(this))
                            .density(12)
                            .setOnColorSelectedListener(selectedColor -> {
                            })
                            .setPositiveButton(R.string.ok, (dialog, selectedColor, allColors) -> {
                                sCommonUtils.saveInt("app_accent_color", selectedColor, this);
                                AppSettings.setRecreateIntent(getIntent(), this);
                            })
                            .setNegativeButton(R.string.cancel, (dialog, which) -> {
                            }).build().show();
                } else if (itemID == 4) {
                    mBiometricPrompt.authenticate(Utils.showBiometricPrompt(this));
                } else if (itemID == 5) {
                    if (Security.isPINEnabled(this)) {
                        Security.authenticate(false, mRecycleViewAdapter, position, this);
                    } else {
                        Security.setPIN(false, getString(R.string.pin_enter), mRecycleViewAdapter, position,  this);
                    }
                } else if (itemID == 6) {
                    if (sCommonUtils.getBoolean("use_biometric", false, this) && Utils.isFingerprintAvailable(this)) {
                        mHidden = true;
                        mBiometricPrompt.authenticate(Utils.showBiometricPrompt(this));
                    } else if (Security.isPINEnabled(this)) {
                        Security.authenticate(false, mRecycleViewAdapter, position,this);
                    } else {
                        sCommonUtils.saveBoolean("hidden_note", !sCommonUtils.getBoolean("hidden_note", false, this), this);
                        mRecycleViewAdapter.notifyItemChanged(position);
                        AppSettings.setReloadIntent(getIntent(),this);
                    }
                } else if (itemID == 7) {
                    if (sNotzColor.isRandomColorScheme(this)) {
                        sCommonUtils.toast(getString(R.string.note_color_random_message), this).show();
                        return;
                    }
                    ColorPickerDialog
                            .with(this)
                            .setTitle(R.string.choose_color)
                            .initialColor(sCommonUtils.getInt("accent_color", sNotzColor.getAccentColor(this), this))
                            .density(12)
                            .setOnColorSelectedListener(selectedColor -> {
                            })
                            .setPositiveButton(R.string.ok, (dialog, selectedColor, allColors) -> {
                                sCommonUtils.saveInt("accent_color", selectedColor, this);
                                sCommonUtils.toast(getString(R.string.choose_color_message, getString(R.string.note_color_background)), this).show();
                                AppSettings.setReloadIntent(getIntent(),this);
                            })
                            .setNegativeButton(R.string.cancel, (dialog, which) -> {
                            }).build().show();
                } else if (itemID == 8) {
                    if (sNotzColor.isRandomColorScheme(this)) {
                        sCommonUtils.toast(getString(R.string.note_color_random_message), this).show();
                        return;
                    }
                    ColorPickerDialog
                            .with(this)
                            .setTitle(R.string.choose_color)
                            .initialColor(sCommonUtils.getInt("text_color", sNotzColor.getTextColor(this), this))
                            .density(12)
                            .setOnColorSelectedListener(selectedColor -> {
                            })
                            .setPositiveButton(R.string.ok, (dialog, selectedColor, allColors) -> {
                                sCommonUtils.saveInt("text_color", selectedColor, this);
                                sCommonUtils.toast(getString(R.string.choose_color_message, getString(R.string.note_color_text)), this).show();
                                AppSettings.setReloadIntent(getIntent(),this);
                                mRecycleViewAdapter.notifyItemChanged(position);
                            })
                            .setNegativeButton(R.string.cancel, (dialog, which) -> {
                            }).build().show();
                } else if (itemID == 9) {
                    if (sNotzColor.isRandomColorScheme(this)) {
                        sCommonUtils.saveInt("random_color", Integer.MIN_VALUE, this);
                    } else {
                        sCommonUtils.saveInt("random_color", 0, this);
                    }
                    mRecycleViewAdapter.updateData(getData());
                } else if (itemID == 10) {
                    sCommonUtils.saveBoolean("auto_save", !sCommonUtils.getBoolean("auto_save", false, this), this);
                    mRecycleViewAdapter.notifyItemChanged(position);
                } else if (itemID == 11) {
                    ColorPickerDialog
                            .with(this)
                            .setTitle(R.string.choose_color)
                            .initialColor(sCommonUtils.getInt("checklist_color", sNotzColor.getMaterial3Colors(
                                    0, sCommonUtils.getColor(R.color.color_teal, this), this), this))
                            .density(12)
                            .setOnColorSelectedListener(selectedColor -> {
                            })
                            .setPositiveButton(R.string.ok, (dialog, selectedColor, allColors) -> {
                                sCommonUtils.saveInt("checklist_color", selectedColor, this);
                                Utils.updateWidgets(this);
                                mRecycleViewAdapter.notifyItemChanged(position);
                            })
                            .setNegativeButton(R.string.cancel, (dialog, which) -> {
                            }).build().show();
                } else if (itemID == 12) {
                    AppSettings.setRows(this);
                } else if (itemID == 13) {
                    AppSettings.setFontSize(this);
                } else if (itemID == 14) {
                    AppSettings.setFontStyle(this);
                } else if (itemID == 15) {
                    if (sCommonUtils.getBoolean("auto_backup", true, this)) {
                        sCommonUtils.saveBoolean("auto_backup", false, this);
                    } else {
                        sCommonUtils.saveBoolean("auto_backup", true, this);
                        AppSettings.backupData(new File(getFilesDir(),"snotz"), new File(getExternalFilesDir("autoBackup"),"autoBackup"), this);
                    }
                    mRecycleViewAdapter.notifyItemChanged(position);
                } else if (itemID == 16) {
                    AppSettings.showBackupOptions(this);
                } else if (itemID == 17) {
                    if (mJSONString != null) mJSONString = null;
                    try {
                        Intent restore = new Intent(Intent.ACTION_GET_CONTENT);
                        restore.setType("text/plain");
                        restoreNotes.launch(restore);
                    } catch (ActivityNotFoundException e) {
                        sCommonUtils.toast(e.getMessage(), this).show();
                    }
                } else if (itemID == 18) {
                    new MaterialAlertDialogBuilder(this)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(R.string.warning)
                            .setMessage(getString(R.string.clear_notes_message))
                            .setNegativeButton(R.string.cancel, (dialog, which) -> {
                            })
                            .setPositiveButton(R.string.delete, (dialog, which) -> {
                                if (sCommonUtils.getBoolean("use_biometric", false, this) && Utils.isFingerprintAvailable(this)) {
                                    mClearNote = true;
                                    mBiometricPrompt.authenticate(Utils.showBiometricPrompt(this));
                                } else if (Security.isPINEnabled(this)) {
                                    Security.authenticate(false, mRecycleViewAdapter, position,this);
                                } else {
                                    sFileUtils.delete(new File(getFilesDir(),"snotz"));
                                    mRecycleViewAdapter.notifyItemChanged(position);
                                    AppSettings.setReloadIntent(getIntent(),this);
                                    finish();
                                }
                            }).show();
                } else if (itemID == 19) {
                    Intent share_app = new Intent();
                    share_app.setAction(Intent.ACTION_SEND);
                    share_app.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    share_app.putExtra(Intent.EXTRA_TEXT, getString(R.string.shared_by_message, BuildConfig.VERSION_NAME));
                    share_app.setType("text/plain");
                    Intent shareIntent = Intent.createChooser(share_app, getString(R.string.share_with));
                    startActivity(shareIntent);
                } else if (itemID == 20) {
                    Intent welcome = new Intent(this, WelcomeActivity.class);
                    startActivity(welcome);
                    finish();
                } else if (itemID == 21) {
                    new sTranslatorUtils(getString(R.string.app_name), "https://poeditor.com/join/project?hash=LOg2GmFfbV", this).show();
                }
            }
        });

        Executor executor = ContextCompat.getMainExecutor(this);
        mBiometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                sCommonUtils.toast(getString(R.string.authentication_error, errString), SettingsActivity.this).show();
                mRecycleViewAdapter.notifyItemRangeChanged(4, 2);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                if (mHidden) {
                    sCommonUtils.saveBoolean("hidden_note", !sCommonUtils.getBoolean("hidden_note", false, SettingsActivity.this), SettingsActivity.this);
                    mHidden = false;
                    AppSettings.setReloadIntent(getIntent(),SettingsActivity.this);
                    mRecycleViewAdapter.notifyItemChanged(5);
                } else if (mClearNote) {
                    mClearNote = false;
                    sFileUtils.delete(new File(getFilesDir(),"snotz"));
                    AppSettings.setReloadIntent(getIntent(),SettingsActivity.this);
                    mRecycleViewAdapter.notifyItemChanged(19);
                } else {
                    Utils.useBiometric(SettingsActivity.this);
                    mRecycleViewAdapter.notifyItemChanged(4);
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                sCommonUtils.toast(getString(R.string.authentication_failed), SettingsActivity.this).show();
                mRecycleViewAdapter.notifyItemRangeChanged(4, 2);
            }
        });
    }

    private ArrayList<SettingsEntry> getData() {
        ArrayList<SettingsEntry> mData = new ArrayList<>();
        mData.add(new SettingsEntry(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")", getString(R.string.copyright, getString(R.string.copyright_text)), sCommonUtils.getDrawable(R.drawable.ic_info, this), 1));
        mData.add(new SettingsEntry(getString(R.string.app_theme), sThemeUtils.getAppTheme(this), sCommonUtils.getDrawable(R.drawable.ic_theme, this), 2));
        mData.add(new SettingsEntry(getString(R.string.color_accent), getString(R.string.color_accent_summary), sCommonUtils.getDrawable(R.drawable.ic_rgb, this), 3));
        mData.add(new SettingsEntry(getString(R.string.security)));
        if (Utils.isFingerprintAvailable(this)) {
            mData.add(new SettingsEntry(getString(R.string.biometric_lock), getString(R.string.biometric_lock_summary), sCommonUtils.getDrawable(R.drawable.ic_fingerprint, this), 4));
        } else {
            mData.add(new SettingsEntry(getString(R.string.pin_protection), getString(R.string.pin_protection_message), sCommonUtils.getDrawable(R.drawable.ic_lock, this), 5));
        }
        mData.add(new SettingsEntry(getString(R.string.show_hidden_notes), getString(R.string.show_hidden_notes_summary), sCommonUtils.getDrawable(R.drawable.ic_eye, this), 6));
        mData.add(new SettingsEntry(getString(R.string.customize_note)));
        if (!sNotzColor.isRandomColorScheme(this)) {
            mData.add(new SettingsEntry(getString(R.string.note_color_background), getString(R.string.color_select_dialog, getString(R.string.note_color_background)), sCommonUtils.getDrawable(R.drawable.ic_color, this), 7));
            mData.add(new SettingsEntry(getString(R.string.note_color_text), getString(R.string.color_select_dialog, getString(R.string.note_color_text)), sCommonUtils.getDrawable(R.drawable.ic_text_color, this), 8));
        }
        mData.add(new SettingsEntry(getString(R.string.note_color_random), getString(R.string.note_color_random_summary), sCommonUtils.getDrawable(R.drawable.ic_colorize, this), 9));
        mData.add(new SettingsEntry(getString(R.string.auto_save), getString(R.string.auto_save_summary), sCommonUtils.getDrawable(R.drawable.ic_save, this), 10));
        mData.add(new SettingsEntry(getString(R.string.check_list_widget_color), getString(R.string.check_list_widget_color_summary), sCommonUtils.getDrawable(R.drawable.ic_checklist, this), 11));
        mData.add(new SettingsEntry(getString(R.string.notes_in_row), AppSettings.getRows(this), sCommonUtils.getDrawable(R.drawable.ic_row, this), 12));
        mData.add(new SettingsEntry(getString(R.string.font_size), getString(R.string.font_size_summary, "" + sCommonUtils.getInt("font_size", 18, this)),
                sCommonUtils.getDrawable(R.drawable.ic_format_size, this), 13));
        mData.add(new SettingsEntry(getString(R.string.text_style), AppSettings.getFontStyle(this), sCommonUtils.getDrawable(R.drawable.ic_text_style, this), 14));
        mData.add(new SettingsEntry(getString(R.string.misc)));
        mData.add(new SettingsEntry(getString(R.string.backup_notes_auto), getString(R.string.backup_notes_auto_summary), sCommonUtils.getDrawable(R.drawable.ic_backup_auto, this), 15));
        if (!sNotzData.isNotesEmpty(this)) {
            mData.add(new SettingsEntry(getString(R.string.backup_notes), getString(R.string.backup_notes_summary), sCommonUtils.getDrawable(R.drawable.ic_backup, this), 16));
        }
        mData.add(new SettingsEntry(getString(R.string.restore_notes), getString(R.string.restore_notes_summary), sCommonUtils.getDrawable(R.drawable.ic_restore, this), 17));
        if (!sNotzData.isNotesEmpty(this)) {
            mData.add(new SettingsEntry(getString(R.string.clear_notes), getString(R.string.clear_notes_summary), sCommonUtils.getDrawable(R.drawable.ic_clear, this), 18));
        }
        mData.add(new SettingsEntry(getString(R.string.invite_friends), getString(R.string.invite_friends_Summary), sCommonUtils.getDrawable(R.drawable.ic_share, this), 19));
        mData.add(new SettingsEntry(getString(R.string.welcome_note), getString(R.string.welcome_note_summary), sCommonUtils.getDrawable(R.drawable.ic_home, this), 20));
        mData.add(new SettingsEntry(getString(R.string.translations), getString(R.string.translations_summary), sCommonUtils.getDrawable(R.drawable.ic_translate, this), 21));
        mData.add(new SettingsEntry(getString(R.string.rate_us), getString(R.string.rate_us_Summary), sCommonUtils.getDrawable(R.drawable.ic_rate, this), "https://play.google.com/store/apps/details?id=com.sunilpaulmathew.snotz"));
        mData.add(new SettingsEntry(getString(R.string.support), getString(R.string.support_summary), sCommonUtils.getDrawable(R.drawable.ic_support, this), "https://t.me/smartpack_kmanager"));
        mData.add(new SettingsEntry(getString(R.string.credits), getString(R.string.credits_summary), sCommonUtils.getDrawable(R.drawable.ic_credits, this), "https://github.com/sunilpaulmathew/sNotz/blob/master/Credits.md"));
        mData.add(new SettingsEntry(getString(R.string.faq), getString(R.string.faq_summary), sCommonUtils.getDrawable(R.drawable.ic_faq, this), "https://sunilpaulmathew.github.io/sNotz/faq/"));
        return mData;
    }

    private final ActivityResultLauncher<Intent> restoreNotes = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent intent = result.getData();
                    Uri uri = intent.getData();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(Objects.requireNonNull(uri));
                        BufferedInputStream bis = new BufferedInputStream(inputStream);
                        ByteArrayOutputStream buf = new ByteArrayOutputStream();
                        for (int jsonResult = bis.read(); jsonResult != -1; jsonResult = bis.read()) {
                            buf.write((byte) jsonResult);
                        }

                        if (CheckLists.isValidCheckList(buf.toString("UTF-8"))) {
                            intent.putExtra("checkList", buf.toString("UTF-8"));
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        } else {
                            if (sNotzUtils.validBackup(Encryption.decrypt(buf.toString("UTF-8")))) {
                                mJSONString = Encryption.decrypt(buf.toString("UTF-8"));
                            } else if (sNotzUtils.validBackup(buf.toString("UTF-8"))) {
                                mJSONString = buf.toString("UTF-8");
                            } else {
                                sCommonUtils.toast(getString(R.string.restore_error), this).show();
                                return;
                            }

                            new RestoreNoteDialog(mJSONString, false, this) {
                                @Override
                                public void negativeButtonLister() {
                                }

                                @Override
                                public void positiveButtonLister() {
                                    intent.putExtra("reload", true);
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                }
                            };
                        }
                    } catch (IOException ignored) {
                        sCommonUtils.toast(getString(R.string.restore_error), this).show();
                    }
                }
            }
    );

}