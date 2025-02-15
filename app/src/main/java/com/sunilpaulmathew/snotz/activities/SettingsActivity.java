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
import androidx.appcompat.app.AppCompatActivity;
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
import com.sunilpaulmathew.snotz.utils.Billing;
import com.sunilpaulmathew.snotz.utils.CheckLists;
import com.sunilpaulmathew.snotz.utils.Encryption;
import com.sunilpaulmathew.snotz.utils.Security;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzColor;
import com.sunilpaulmathew.snotz.utils.sNotzData;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;
import com.sunilpaulmathew.snotz.utils.serializableItems.SettingsItems;

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
import in.sunilpaulmathew.sCommon.Credits.sCreditsUtils;
import in.sunilpaulmathew.sCommon.FileUtils.sFileUtils;
import in.sunilpaulmathew.sCommon.ThemeUtils.sThemeUtils;
import in.sunilpaulmathew.sCommon.TranslatorUtils.sTranslatorUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 17, 2020
 */
public class SettingsActivity extends AppCompatActivity {

    private BiometricPrompt mBiometricPrompt;
    private boolean mClearNote = false, mHidden = false;
    private String mJSONString = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialTextView mTitle = findViewById(R.id.title);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        SettingsAdapter mRecycleViewAdapter = new SettingsAdapter(getData());
        mRecyclerView.setAdapter(mRecycleViewAdapter);

        mTitle.setTextColor(sNotzColor.getAppAccentColor(this));

        mRecycleViewAdapter.setOnItemClickListener((position, v) -> {
            if (getData().get(position).getUrl() != null) {
                sCommonUtils.launchUrl(getData().get(position).getUrl(), this);
            } else if (position == 0) {
                Intent settings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                settings.setData(uri);
                startActivity(settings);
                finish();
            } else if (position == 1) {
                sThemeUtils.setAppTheme(this);
            } else if (position == 2) {
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
            } else if (position == 4) {
                if (Utils.isFingerprintAvailable(this)) {
                    mBiometricPrompt.authenticate(Utils.showBiometricPrompt(this));
                } else {
                    if (Security.isPINEnabled(this)) {
                        Security.authenticate(false, mRecycleViewAdapter, position, this);
                    } else {
                        Security.setPIN(false, getString(R.string.pin_enter), mRecycleViewAdapter, position,  this);
                    }
                }
            } else if (position == 5) {
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
            } else if (position == 7) {
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
            } else if (position == 8) {
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
            } else if (position == 9) {
                if (sNotzColor.isRandomColorScheme(this)) {
                    sCommonUtils.saveInt("random_color", Integer.MIN_VALUE, this);
                } else {
                    sCommonUtils.saveInt("random_color", 0, this);
                }
                mRecycleViewAdapter.notifyItemRangeChanged(7, 3);
            } else if (position == 10) {
                sCommonUtils.saveBoolean("auto_save", !sCommonUtils.getBoolean("auto_save", false, this), this);
                mRecycleViewAdapter.notifyItemChanged(position);
            } else if (position == 11) {
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
            } else if (position == 12) {
                AppSettings.setRows(this);
            } else if (position == 13) {
                AppSettings.setFontSize(this);
            } else if (position == 14) {
                AppSettings.setFontStyle(this);
            } else if (position == 16) {
                if (sCommonUtils.getBoolean("auto_backup", true, this)) {
                    sCommonUtils.saveBoolean("auto_backup", false, this);
                } else {
                    sCommonUtils.saveBoolean("auto_backup", true, this);
                    AppSettings.backupData(new File(getFilesDir(),"snotz"), new File(getExternalFilesDir("autoBackup"),"autoBackup"), this);
                }
                mRecycleViewAdapter.notifyItemChanged(position);
            } else if (position == 17) {
                if (sNotzData.isNotesEmpty(this)) {
                    sCommonUtils.toast(getString(R.string.note_list_empty), this).show();
                } else {
                    AppSettings.showBackupOptions(this);
                }
            } else if (position == 18) {
                if (mJSONString != null) mJSONString = null;
                try {
                    Intent restore = new Intent(Intent.ACTION_GET_CONTENT);
                    restore.setType("text/plain");
                    restoreNotes.launch(restore);
                } catch (ActivityNotFoundException e) {
                    sCommonUtils.toast(e.getMessage(), this).show();
                }
            } else if (position == 19) {
                if (sNotzData.isNotesEmpty(this)) {
                    sCommonUtils.toast(getString(R.string.note_list_empty), this).show();
                } else {
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
                }
            } else if (position == 20) {
                Billing.showDonationMenu(this);
            } else if (position == 21) {
                Intent share_app = new Intent();
                share_app.setAction(Intent.ACTION_SEND);
                share_app.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                share_app.putExtra(Intent.EXTRA_TEXT, getString(R.string.shared_by_message, BuildConfig.VERSION_NAME));
                share_app.setType("text/plain");
                Intent shareIntent = Intent.createChooser(share_app, getString(R.string.share_with));
                startActivity(shareIntent);
            } else if (position == 22) {
                Intent welcome = new Intent(this, WelcomeActivity.class);
                startActivity(welcome);
                finish();
            } else if (position == 23) {
                new sTranslatorUtils(getString(R.string.app_name), "https://poeditor.com/join/project?hash=LOg2GmFfbV", this).show();
            } else if (position == 26) {
                new sCreditsUtils(AppSettings.getCredits(),
                        sCommonUtils.getDrawable(R.mipmap.ic_launcher, this),
                        sCommonUtils.getDrawable(R.drawable.ic_back, this),
                        sNotzColor.getAppAccentColor(this),
                        25, getString(R.string.app_name), getString(R.string.copyright_text),
                        BuildConfig.VERSION_NAME).launchCredits(this);
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

    private ArrayList<SettingsItems> getData() {
        ArrayList<SettingsItems> mData = new ArrayList<>();
        mData.add(new SettingsItems(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")", getString(R.string.copyright, getString(R.string.copyright_text)), sCommonUtils.getDrawable(R.drawable.ic_info, this), null));
        mData.add(new SettingsItems(getString(R.string.app_theme), sThemeUtils.getAppTheme(this), sCommonUtils.getDrawable(R.drawable.ic_theme, this), null));
        mData.add(new SettingsItems(getString(R.string.color_accent), getString(R.string.color_accent_summary), sCommonUtils.getDrawable(R.drawable.ic_rgb, this), null));
        mData.add(new SettingsItems(getString(R.string.security), null, null, null));
        if (Utils.isFingerprintAvailable(this)) {
            mData.add(new SettingsItems(getString(R.string.biometric_lock), getString(R.string.biometric_lock_summary), sCommonUtils.getDrawable(R.drawable.ic_fingerprint, this), null));
        } else {
            mData.add(new SettingsItems(getString(R.string.pin_protection), getString(R.string.pin_protection_message), sCommonUtils.getDrawable(R.drawable.ic_lock, this), null));
        }
        mData.add(new SettingsItems(getString(R.string.show_hidden_notes), getString(R.string.show_hidden_notes_summary), sCommonUtils.getDrawable(R.drawable.ic_eye, this), null));
        mData.add(new SettingsItems(getString(R.string.customize_note), null, null, null));
        mData.add(new SettingsItems(getString(R.string.note_color_background), getString(R.string.color_select_dialog, getString(R.string.note_color_background)), sCommonUtils.getDrawable(R.drawable.ic_color, this), null));
        mData.add(new SettingsItems(getString(R.string.note_color_text), getString(R.string.color_select_dialog, getString(R.string.note_color_text)), sCommonUtils.getDrawable(R.drawable.ic_text_color, this), null));
        mData.add(new SettingsItems(getString(R.string.note_color_random), getString(R.string.note_color_random_summary), sCommonUtils.getDrawable(R.drawable.ic_colorize, this), null));
        mData.add(new SettingsItems(getString(R.string.auto_save), getString(R.string.auto_save_summary), sCommonUtils.getDrawable(R.drawable.ic_save, this), null));
        mData.add(new SettingsItems(getString(R.string.check_list_widget_color), getString(R.string.check_list_widget_color_summary), sCommonUtils.getDrawable(R.drawable.ic_checklist, this), null));
        mData.add(new SettingsItems(getString(R.string.notes_in_row), AppSettings.getRows(this), sCommonUtils.getDrawable(R.drawable.ic_row, this), null));
        mData.add(new SettingsItems(getString(R.string.font_size), getString(R.string.font_size_summary, "" + sCommonUtils.getInt("font_size", 18, this)),
                sCommonUtils.getDrawable(R.drawable.ic_format_size, this), null));
        mData.add(new SettingsItems(getString(R.string.text_style), AppSettings.getFontStyle(this), sCommonUtils.getDrawable(R.drawable.ic_text_style, this), null));
        mData.add(new SettingsItems(getString(R.string.misc), null, null, null));
        mData.add(new SettingsItems(getString(R.string.backup_notes_auto), getString(R.string.backup_notes_auto_summary), sCommonUtils.getDrawable(R.drawable.ic_backup_auto, this), null));
        mData.add(new SettingsItems(getString(R.string.backup_notes), getString(R.string.backup_notes_summary), sCommonUtils.getDrawable(R.drawable.ic_backup, this), null));
        mData.add(new SettingsItems(getString(R.string.restore_notes), getString(R.string.restore_notes_summary), sCommonUtils.getDrawable(R.drawable.ic_restore, this), null));
        mData.add(new SettingsItems(getString(R.string.clear_notes), getString(R.string.clear_notes_summary), sCommonUtils.getDrawable(R.drawable.ic_clear, this), null));
        mData.add(new SettingsItems(getString(R.string.support_development), getString(R.string.donations_summary), sCommonUtils.getDrawable(R.drawable.ic_donate, this), null));
        mData.add(new SettingsItems(getString(R.string.invite_friends), getString(R.string.invite_friends_Summary), sCommonUtils.getDrawable(R.drawable.ic_share, this), null));
        mData.add(new SettingsItems(getString(R.string.welcome_note), getString(R.string.welcome_note_summary), sCommonUtils.getDrawable(R.drawable.ic_home, this), null));
        mData.add(new SettingsItems(getString(R.string.translations), getString(R.string.translations_summary), sCommonUtils.getDrawable(R.drawable.ic_translate, this), null));
        mData.add(new SettingsItems(getString(R.string.rate_us), getString(R.string.rate_us_Summary), sCommonUtils.getDrawable(R.drawable.ic_rate, this), "https://play.google.com/store/apps/details?id=com.sunilpaulmathew.snotz"));
        mData.add(new SettingsItems(getString(R.string.support), getString(R.string.support_summary), sCommonUtils.getDrawable(R.drawable.ic_support, this), "https://t.me/smartpack_kmanager"));
        mData.add(new SettingsItems(getString(R.string.credits), getString(R.string.credits_summary), sCommonUtils.getDrawable(R.drawable.ic_credits, this), null));
        mData.add(new SettingsItems(getString(R.string.faq), getString(R.string.faq_summary), sCommonUtils.getDrawable(R.drawable.ic_faq, this), "https://sunilpaulmathew.github.io/sNotz/faq/"));
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
                        if (sNotzUtils.validBackup(Encryption.decrypt(buf.toString("UTF-8")))) {
                            mJSONString = Encryption.decrypt(buf.toString("UTF-8"));
                        } else if (sNotzUtils.validBackup(buf.toString("UTF-8"))) {
                            mJSONString = buf.toString("UTF-8");
                        } else if (CheckLists.isValidCheckList(buf.toString("UTF-8"))) {
                            intent.putExtra("checkList", buf.toString("UTF-8"));
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        } else {
                            sCommonUtils.toast(getString(R.string.restore_error), this).show();
                            return;
                        }
                    } catch (IOException ignored) {
                        sCommonUtils.toast(getString(R.string.restore_error), this).show();
                    }

                    new MaterialAlertDialogBuilder(this)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(R.string.app_name)
                            .setMessage(getString(R.string.restore_notes_question))
                            .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                            })
                            .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                                intent.putExtra("dataBase", mJSONString);
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            }).show();
                }
            }
    );

}