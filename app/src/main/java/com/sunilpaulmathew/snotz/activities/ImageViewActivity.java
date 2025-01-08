package com.sunilpaulmathew.snotz.activities;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.interfaces.EditTextInterface;
import com.sunilpaulmathew.snotz.utils.QRCodeUtils;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzColor;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 07, 2021
 */
public class ImageViewActivity extends AppCompatActivity {

    public static final String SHARE_SAVE_INTENT = "share_save", IMAGE_INTENT = "image";
    private static Bitmap mBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);

        AppCompatImageButton mSave = findViewById(R.id.save_button);
        AppCompatImageButton mShare = findViewById(R.id.share_button);
        AppCompatImageView mImage = findViewById(R.id.image);
        MaterialTextView mTitle = findViewById(R.id.title);

        mBitmap = getIntent().getParcelableExtra(IMAGE_INTENT);
        boolean mSaveAndShare = getIntent().getBooleanExtra(SHARE_SAVE_INTENT, false);

        mSave.setColorFilter(sNotzColor.getAppAccentColor(this));
        mShare.setColorFilter(sNotzColor.getAppAccentColor(this));
        mTitle.setTextColor(sNotzColor.getAppAccentColor(this));

        if (mBitmap != null) {
            mImage.setImageBitmap(mBitmap);
            if (mSaveAndShare) {
                mSave.setVisibility(View.VISIBLE);
                mShare.setVisibility(View.VISIBLE);
            }
        }

        mSave.setOnClickListener(v -> saveDialog());

        mShare.setOnClickListener(v -> new QRCodeUtils(null, null, this).shareQRCode(mBitmap).execute());
    }

    private void saveDialog() {
        if (Build.VERSION.SDK_INT < 29 && Utils.isPermissionDenied(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, this)) {
            Utils.requestPermission(new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, this);
            return;
        }
        new EditTextInterface("sNotz", getString(R.string.qr_code_save_hint), this) {

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
                    new QRCodeUtils(null, null, ImageViewActivity.this).saveQRCode(mBitmap, fileName);
                    finish();
                } else {
                    sCommonUtils.toast(getString(R.string.text_empty), ImageViewActivity.this).show();
                }
            }
        }.show();
    }

}