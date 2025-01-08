package com.sunilpaulmathew.snotz.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.sNotzColor;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 08, 2025
 */
public class NotificationRequestActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_request);

        AppCompatImageView mIcon = findViewById(R.id.icon);
        MaterialTextView mAccept = findViewById(R.id.accept);
        MaterialTextView mDeny = findViewById(R.id.deny);
        MaterialTextView mText = findViewById(R.id.text);

        mIcon.setColorFilter(sNotzColor.getAppAccentColor(this));
        mText.setTextColor(sNotzColor.getAppAccentColor(this));
        mAccept.setTextColor(sCommonUtils.getColor(android.R.color.holo_green_dark, this));
        mDeny.setTextColor(sCommonUtils.getColor(android.R.color.holo_red_dark, this));

        mText.setText(getString(R.string.notification_request_title) + "\n\n" + getString(R.string.notification_request_summary));

        mAccept.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        });

        mDeny.setOnClickListener(v -> finish());
    }

    private final ActivityResultLauncher<String> requestPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result) {
                    finish();
                } else {
                    sCommonUtils.toast(getString(R.string.notification_request_denied_toast), this).show();
                }
            }
    );

}