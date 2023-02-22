package com.sunilpaulmathew.snotz.activities;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;

import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.interfaces.EditTextInterface;
import com.sunilpaulmathew.snotz.utils.Common;
import com.sunilpaulmathew.snotz.utils.QRCodeUtils;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 07, 2021
 */
public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);

        AppCompatImageButton mBack = findViewById(R.id.back_button);
        AppCompatImageButton mSave = findViewById(R.id.save_button);
        AppCompatImageButton mShare = findViewById(R.id.share_button);
        AppCompatImageView mImage = findViewById(R.id.image);

        if (Common.getReadModeImage() != null) {
            mImage.setImageBitmap(Common.getReadModeImage());
            mSave.setVisibility(View.VISIBLE);
            mShare.setVisibility(View.VISIBLE);
        } else if (Common.getImageString() != null) {
            mImage.setImageBitmap(sNotzUtils.stringToBitmap(Common.getImageString()));
        }

        mBack.setOnClickListener(v -> onBackPressed());

        mSave.setOnClickListener(v -> saveDialog());

        mShare.setOnClickListener(v -> new QRCodeUtils(Common.getNote(), null, this).shareQRCode(Common.getReadModeImage()).execute());
    }

    private void saveDialog() {
        if (Build.VERSION.SDK_INT < 29 && Utils.isPermissionDenied(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, this)) {
            Utils.requestPermission(new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, this);
            return;
        }
        new EditTextInterface("sNotz", getString(R.string.backup_notes_hint), this) {

            @Override
            public void positiveButtonLister(Editable s) {
                if (s != null && !s.toString().trim().isEmpty()) {
                    String fileName = s.toString().trim();
                    if (!fileName.endsWith(".png")) {
                        fileName += ".png";
                    }
                    if (fileName.contains(" ")) {
                        fileName = fileName.replace(" ", "_");
                    }
                    new QRCodeUtils(null, null, ImageViewActivity.this).saveQRCode(Common.getReadModeImage(), fileName);
                    onBackPressed();
                } else {
                    sCommonUtils.snackBar(findViewById(android.R.id.content), getString(R.string.text_empty)).show();
                }
            }
        }.show();
    }

    @Override
    public void onBackPressed() {
        if (!Common.isWorking()) {
            Common.setNote(null);
            Common.setReadModeImage(null);
            finish();
        }
    }
}