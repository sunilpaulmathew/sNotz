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
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.BuildConfig;
import com.sunilpaulmathew.snotz.R;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executor;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 17, 2020
 */

public class SettingsActivity extends AppCompatActivity {

    private AppCompatImageButton mBack;
    private ArrayList <RecycleViewItem> mData = new ArrayList<>();
    private String mPath;

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mBack = findViewById(R.id.back_button);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        RecycleViewAdapter mRecycleViewAdapter = new RecycleViewAdapter(mData);
        mRecyclerView.setAdapter(mRecycleViewAdapter);

        mData.add(new RecycleViewItem(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")", BuildConfig.APPLICATION_ID, getResources().getDrawable(R.mipmap.ic_launcher_round)));
        mData.add(new RecycleViewItem(getString(R.string.biometric_lock), getString(R.string.biometric_lock_summary), getResources().getDrawable(R.drawable.ic_fingerprint)));
        mData.add(new RecycleViewItem(getString(R.string.show_hidden_notes), getString(R.string.show_hidden_notes_summary), getResources().getDrawable(R.drawable.ic_eye)));
        mData.add(new RecycleViewItem(getString(R.string.note_color_background), getString(R.string.color_select_dialog, getString(R.string.note_color_background)), getResources().getDrawable(R.drawable.ic_color)));
        mData.add(new RecycleViewItem(getString(R.string.note_color_text), getString(R.string.color_select_dialog, getString(R.string.note_color_text)), getResources().getDrawable(R.drawable.ic_text)));
        mData.add(new RecycleViewItem(getString(R.string.backup_notes), getString(R.string.backup_notes_summary), getResources().getDrawable(R.drawable.ic_backup)));
        mData.add(new RecycleViewItem(getString(R.string.restore_notes), getString(R.string.restore_notes_summary), getResources().getDrawable(R.drawable.ic_restore)));
        mData.add(new RecycleViewItem(getString(R.string.clear_notes), getString(R.string.clear_notes_summary), getResources().getDrawable(R.drawable.ic_clear)));
        mData.add(new RecycleViewItem(getString(R.string.donations), getString(R.string.donations_summary), getResources().getDrawable(R.drawable.ic_donate)));
        mData.add(new RecycleViewItem(getString(R.string.invite_friends), getString(R.string.invite_friends_Summary), getResources().getDrawable(R.drawable.ic_share)));
        mData.add(new RecycleViewItem(getString(R.string.welcome_note), getString(R.string.welcome_note_summary), getResources().getDrawable(R.drawable.ic_home)));
        mData.add(new RecycleViewItem(getString(R.string.rate_us), getString(R.string.rate_us_Summary), getResources().getDrawable(R.drawable.ic_rate)));
        mData.add(new RecycleViewItem(getString(R.string.support), getString(R.string.support_summary), getResources().getDrawable(R.drawable.ic_support)));
        mData.add(new RecycleViewItem(getString(R.string.faq), getString(R.string.faq_summary), getResources().getDrawable(R.drawable.ic_faq)));

        mRecycleViewAdapter.setOnItemClickListener((position, v) -> {
            if (position == 0) {
                Intent settings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                settings.setData(uri);
                startActivity(settings);
                finish();
            } else if (position == 1) {
                if (Utils.isFingerprintAvailable(this)) {
                    Utils.mBiometricPrompt.authenticate(Utils.mPromptInfo);
                } else {
                    Utils.showSnackbar(mRecyclerView, getString(R.string.biometric_lock_unavailable));
                }
                mRecycleViewAdapter.notifyDataSetChanged();
            } else if (position == 2) {
                if (Utils.getBoolean("use_biometric", false, this) && Utils.isFingerprintAvailable(this)) {
                    Utils.mHiddenNotes = true;
                    Utils.mBiometricPrompt.authenticate(Utils.mPromptInfo);
                } else {
                    Utils.manageHiddenNotes(this);
                }
                mRecycleViewAdapter.notifyDataSetChanged();
            } else if (position == 3) {
                sNotzColor.colorDialog(sNotzColor.getColors(this).indexOf(sNotzColor.setAccentColor("note_background", this)), "note_background", this);
            } else if (position == 4) {
                Utils.mTextColor = true;
                sNotzColor.colorDialog(sNotzColor.getColors(this).indexOf(sNotzColor.setAccentColor("text_color", this)), "text_color", this);
            } else if (position == 5) {
                if (Utils.existFile(getFilesDir().getPath() + "/snotz")) {
                    Utils.create(Utils.readFile(getFilesDir().getPath() + "/snotz"), Environment.getExternalStorageDirectory().toString() + "/snotz.backup/");
                    Utils.showSnackbar(mBack, getString(R.string.backup_notes_message, Environment.getExternalStorageDirectory().toString() + "/snotz.backup/"));
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
                    startActivityForResult(restore, 0);
                }
            } else if (position == 7) {
                if (Utils.existFile(getFilesDir().getPath() + "/snotz")) {
                    new MaterialAlertDialogBuilder(this)
                            .setMessage(getString(R.string.clear_notes_message))
                            .setNegativeButton(R.string.cancel, (dialog, which) -> {
                            })
                            .setPositiveButton(R.string.delete, (dialog, which) -> {
                                Utils.deleteFile(getFilesDir().getPath() + "/snotz");
                                Utils.reloadUI(this);
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
            } else if (position == 11) {
                Utils.launchURL(mBack, "https://play.google.com/store/apps/details?id=com.sunilpaulmathew.snotz", this);
            } else if (position == 12) {
                Utils.launchURL(mBack, "https://t.me/smartpack_kmanager", this);
            } else if (position == 13) {
                Utils.launchURL(mBack, "https://ko-fi.com/post/sNotz-FAQ-H2H42H6A8", this);
            }
        });

        mBack.setOnClickListener(v -> onBackPressed());

        Executor executor = ContextCompat.getMainExecutor(this);
        Utils.mBiometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Utils.showSnackbar(mBack, getString(R.string.authentication_error, errString));
                mRecycleViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                if (Utils.mHiddenNotes) {
                    Utils.manageHiddenNotes(SettingsActivity.this);
                } else {
                    Utils.useBiometric(mBack, SettingsActivity.this);
                }
                mRecycleViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Utils.showSnackbar(mBack, getString(R.string.authentication_failed));
                mRecycleViewAdapter.notifyDataSetChanged();
            }
        });

        Utils.showBiometricPrompt(this);
    }

    private static class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

        private ArrayList<RecycleViewItem> data;

        private static ClickListener mClickListener;

        public RecycleViewAdapter(ArrayList<RecycleViewItem> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public RecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_recycle_view_settings, parent, false);
            return new RecycleViewAdapter.ViewHolder(rowItem);
        }

        @Override
        public void onBindViewHolder(@NonNull RecycleViewAdapter.ViewHolder holder, int position) {
            holder.mTitle.setText(this.data.get(position).getTitle());
            holder.mDescription.setText(this.data.get(position).getDescription());
            holder.mIcon.setImageDrawable(this.data.get(position).getIcon());
            if (position == 0) {
                holder.mIcon.setColorFilter(null);
            } else {
                holder.mIcon.setColorFilter(sNotzColor.getAccentColor(holder.mIcon.getContext()));
            }
            if (position == 1) {
                holder.mCheckBox.setVisibility(View.VISIBLE);
                holder.mCheckBox.setChecked(Utils.getBoolean("use_biometric", false, holder.mCheckBox.getContext()));
                holder.mCheckBox.setOnClickListener(v -> {
                    if (Utils.isFingerprintAvailable(holder.mCheckBox.getContext())) {
                        Utils.mBiometricPrompt.authenticate(Utils.mPromptInfo);
                    } else {
                        Utils.showSnackbar(holder.mCheckBox, holder.mCheckBox.getContext().getString(R.string.biometric_lock_unavailable));
                    }
                    notifyDataSetChanged();
                });
            } else if (position == 2) {
                holder.mCheckBox.setVisibility(View.VISIBLE);
                holder.mCheckBox.setChecked(Utils.getBoolean("hidden_note", false, holder.mCheckBox.getContext()));
                holder.mCheckBox.setOnClickListener(v -> {
                    if (Utils.getBoolean("use_biometric", false, holder.mCheckBox.getContext()) && Utils.isFingerprintAvailable(holder.mCheckBox.getContext())) {
                        Utils.mHiddenNotes = true;
                        Utils.mBiometricPrompt.authenticate(Utils.mPromptInfo);
                    } else {
                        Utils.manageHiddenNotes(holder.mCheckBox.getContext());
                    }
                    notifyDataSetChanged();
                });
            }
            if (!Utils.isFingerprintAvailable(holder.mTitle.getContext()) && position == 1) {
                holder.mTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.mDescription.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            }
            if (!Utils.existFile(holder.mTitle.getContext().getFilesDir().getPath() + "/snotz")) {
                if (position == 5 || position == 7) {
                    holder.mTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.mDescription.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }
        }

        @Override
        public int getItemCount() {
            return this.data.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private AppCompatImageView mIcon;
            private MaterialCheckBox mCheckBox;
            private MaterialTextView mTitle;
            private MaterialTextView mDescription;

            public ViewHolder(View view) {
                super(view);
                view.setOnClickListener(this);
                this.mIcon = view.findViewById(R.id.icon);
                this.mTitle = view.findViewById(R.id.title);
                this.mDescription = view.findViewById(R.id.description);
                this.mCheckBox = view.findViewById(R.id.checkbox);
            }

            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(getAdapterPosition(), view);
            }
        }

        public void setOnItemClickListener(ClickListener clickListener) {
            RecycleViewAdapter.mClickListener = clickListener;
        }

        public interface ClickListener {
            void onItemClick(int position, View v);
        }
    }

    private static class RecycleViewItem implements Serializable {
        private String mTitle;
        private String mDescription;
        private Drawable mIcon;

        public RecycleViewItem(String title, String description, Drawable icon) {
            this.mTitle = title;
            this.mDescription = description;
            this.mIcon = icon;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getDescription() {
            return mDescription;
        }

        public Drawable getIcon() {
            return mIcon;
        }
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
            new MaterialAlertDialogBuilder(this)
                    .setMessage(getString(R.string.restore_notes_question, new File(mPath).getName()))
                    .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    })
                    .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                        if (Utils.existFile(getFilesDir().getPath() + "/snotz")) {
                            Utils.create(Objects.requireNonNull(Utils.readFile(getFilesDir().getPath() + "/snotz")).replace("}]", "}," +
                                    sNotz.getNotesFromBackup(mPath) + "]"), getFilesDir().getPath() + "/snotz");
                        } else {
                            Utils.create(Utils.readFile(mPath), getFilesDir().getPath() + "/snotz");
                        }
                        Utils.reloadUI(this);
                        onBackPressed();
                    })
                    .show();
        }
    }

}