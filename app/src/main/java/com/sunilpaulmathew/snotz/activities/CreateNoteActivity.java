package com.sunilpaulmathew.snotz.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.AppSettings;
import com.sunilpaulmathew.snotz.utils.Common;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzColor;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;

import java.io.IOException;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2020
 */
public class CreateNoteActivity extends AppCompatActivity {

    private AppCompatEditText mContents;
    private AppCompatImageView mImage;
    private Bitmap mBitmap = null;
    private int mSelectedColorBg, mSelectedColorTxt;
    private ProgressBar mProgress;
    private String mNote = null;
    private SwitchCompat mHidden;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createnote);

        AppCompatImageButton mAdd = findViewById(R.id.add);
        AppCompatImageButton mBack = findViewById(R.id.back_button);
        AppCompatImageButton mSave = findViewById(R.id.save_button);
        AppCompatImageButton mReadingMode = findViewById(R.id.reading_mode);
        mImage = findViewById(R.id.image);
        mContents = findViewById(R.id.contents);
        mProgress = findViewById(R.id.progress);
        MaterialCardView mColorBackground = findViewById(R.id.color_background);
        MaterialCardView mColorText = findViewById(R.id.color_text);
        NestedScrollView mScrollView = findViewById(R.id.scroll_view);
        mHidden = findViewById(R.id.hidden);

        if (Utils.getBoolean("auto_save", false, this)) {
            mSave.setVisibility(View.GONE);
        }

        if (Common.getBackgroundColor() != 123456789) {
            mColorBackground.setCardBackgroundColor(Common.getBackgroundColor());
            mScrollView.setBackgroundColor(Common.getBackgroundColor());
            mSelectedColorBg = Common.getBackgroundColor();
        } else {
            mColorBackground.setCardBackgroundColor(sNotzColor.getAccentColor(this));
            mScrollView.setBackgroundColor(sNotzColor.getAccentColor(this));
            mSelectedColorBg = sNotzColor.getAccentColor(this);
        }
        if (Common.getTextColor() != 123456789) {
            mColorText.setCardBackgroundColor(Common.getTextColor());
            mContents.setTextColor(Common.getTextColor());
            mContents.setHintTextColor(Common.getTextColor());
            mSelectedColorTxt = Common.getTextColor();
        } else {
            mColorText.setCardBackgroundColor(sNotzColor.getTextColor(this));
            mContents.setTextColor(sNotzColor.getTextColor(this));
            mContents.setHintTextColor(sNotzColor.getTextColor(this));
            mSelectedColorTxt = sNotzColor.getTextColor(this);
        }

        mContents.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.getInt("font_size", 18, this));
        mContents.setTypeface(null, AppSettings.getStyle(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mContents.setTextCursorDrawable(sNotzUtils.getColoredDrawable(mContents.getCurrentTextColor(), R.drawable.ic_cursor, this));
        }

        if (Utils.getBoolean("allow_images", false, this)) {
            mAdd.setVisibility(View.VISIBLE);
        }

        if (Common.getImageString() != null) {
            mImage.setImageBitmap(sNotzUtils.stringToBitmap(Common.getImageString()));
            if (sNotzUtils.stringToBitmap(Common.getImageString()) != null) {
                mImage.setVisibility(View.VISIBLE);
                mImage.setOnClickListener(v -> {
                    Intent imageView = new Intent(this, ImageViewActivity.class);
                    startActivity(imageView);
                });
                mBitmap = sNotzUtils.stringToBitmap(Common.getImageString());
            }
        }

        if (Common.getExternalNote() != null) {
            mNote = Common.getExternalNote();
            mContents.setText(Common.getExternalNote());
            mSelectedColorBg = sNotzColor.getAccentColor(this);
            mContents.setHintTextColor(sNotzColor.getTextColor(this));
            mColorBackground.setCardBackgroundColor(sNotzColor.getAccentColor(this));
            mScrollView.setBackgroundColor(sNotzColor.getAccentColor(this));
            mColorText.setCardBackgroundColor(sNotzColor.getTextColor(this));
            mContents.setTextColor(sNotzColor.getTextColor(this));
            mSelectedColorTxt = sNotzColor.getTextColor(this);
            mHidden.setChecked(false);
        } else if (Common.getNote() != null) {
            mHidden.setChecked(Common.isHiddenNote());
            mContents.setText(Common.getNote());
            mNote = Common.getNote();
        } else {
            mReadingMode.setVisibility(View.GONE);
        }

        mColorBackground.setOnClickListener(v -> ColorPickerDialogBuilder
                .with(this)
                .setTitle(R.string.choose_color)
                .initialColor(sNotzColor.getAccentColor(this))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(selectedColor -> {
                })
                .setPositiveButton(R.string.ok, (dialog, selectedColor, allColors) -> {
                    mScrollView.setBackgroundColor(selectedColor);
                    mColorBackground.setCardBackgroundColor(selectedColor);
                    mSelectedColorBg = selectedColor;
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                }).build().show());

        mColorText.setOnClickListener(v -> ColorPickerDialogBuilder
                .with(this)
                .setTitle(R.string.choose_color)
                .initialColor(sNotzColor.getAccentColor(this))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(selectedColor -> {
                })
                .setPositiveButton(R.string.ok, (dialog, selectedColor, allColors) -> {
                    mContents.setTextColor(selectedColor);
                    mContents.setHintTextColor(selectedColor);
                    mColorText.setCardBackgroundColor(selectedColor);
                    mSelectedColorTxt = selectedColor;
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                }).build().show());

        mBack.setOnClickListener(v -> onBackPressed());

        mAdd.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, mAdd);
            Menu menu = popupMenu.getMenu();
            menu.add(Menu.NONE, 0, Menu.NONE, getString(mBitmap == null ? R.string.image_add : R.string.image_replace));
            if (mBitmap != null) {
                menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.image_remove));
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 0:
                        if (Build.VERSION.SDK_INT < 29 && Utils.isPermissionDenied(this)) {
                            ActivityCompat.requestPermissions(this, new String[] {
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                        } else {
                            Intent addImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            try {
                                startActivityForResult(addImage, 0);
                            } catch (ActivityNotFoundException ignored) {}
                        }
                        break;
                    case 1:
                        mImage.setImageBitmap(null);
                        mImage.setVisibility(View.GONE);
                        mBitmap = null;
                }
                return false;
            });
            popupMenu.show();
        });

        mReadingMode.setOnClickListener(v ->
                new MaterialAlertDialogBuilder(this)
                        .setMessage(getString(R.string.reading_mode_message) + (isUnsavedNote() ? "\n\n" + getString(
                                R.string.reading_mode_warning) : ""))
                        .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                        })
                        .setPositiveButton(R.string.go_ahead, (dialogInterface, i) -> {
                            Intent readOnlyMode = new Intent(this, ReadNoteActivity.class);
                            Common.setReadModeText(Common.getNote());
                            if (mBitmap != null) {
                                Common.setReadModeImage(mBitmap);
                            } else {
                                Common.setReadModeImage(null);
                            }
                            startActivity(readOnlyMode);
                            finish();
                        }).show());

        mSave.setOnClickListener(v -> saveNote());
    }

    private void saveNote() {
        if (mContents.getText() == null || mContents.getText().toString().trim().isEmpty()) {
            Utils.showSnackbar(findViewById(R.id.contents), getString(R.string.text_empty));
            return;
        }
        if (Common.getNote() != null) {
            sNotzUtils.updateNote(mContents.getText(), (mBitmap != null ? sNotzUtils.bitmapToBase64(mBitmap, this) : null), Common.getID(), mSelectedColorBg,
                    mSelectedColorTxt, mHidden.isChecked(),  mProgress,this).execute();
        } else if (Utils.exist(getFilesDir().getPath() + "/snotz")) {
            sNotzUtils.addNote(mContents.getText(), (mBitmap != null ? sNotzUtils.bitmapToBase64(mBitmap, this) : null), mSelectedColorBg, mSelectedColorTxt, mHidden.isChecked(), mProgress, this).execute();
        } else {
            sNotzUtils.initializeNotes(mContents.getText(), (mBitmap != null ? sNotzUtils.bitmapToBase64(mBitmap, this) : null), mSelectedColorBg, mSelectedColorTxt, mHidden.isChecked(), mProgress, this).execute();
        }
        exit(this);
    }

    private boolean isUnsavedNote() {
        return mNote != null && mContents.getText() != null && !mNote.equals(mContents.getText().toString().trim()) || mNote == null
                && mContents.getText() != null && !mContents.getText().toString().trim().isEmpty();
    }

    private static void exit(Activity activity) {
        if (Common.getNote() != null) Common.setNote(null);
        if (Common.getImageString() != null) Common.setImageString(null);
        if (Common.isHiddenNote()) Common.isHiddenNote(false);
        if (Common.getBackgroundColor() != 123456789) Common.setBackgroundColor(123456789);
        if (Common.getID() != -1) Common.setID(-1);
        if (Common.getTextColor() != 123456789) Common.setTextColor(123456789);
        activity.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && data != null && resultCode == RESULT_OK){
            if (data.getData() != null) {
                try {
                    float mRatio;
                    mImage.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()));
                    mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    int mSize = sNotzUtils.getMaxSize(this);
                    int mHeight, mWidth;
                    if (mBitmap.getHeight() > mBitmap.getWidth()) {
                        mRatio = (float) (mBitmap.getHeight() / mBitmap.getWidth());
                        mHeight = mSize;
                        mWidth = (int) (mSize / mRatio);
                    } else if (mBitmap.getHeight() == mBitmap.getWidth()) {
                        mHeight = mSize;
                        mWidth = mSize;
                    } else {
                        mRatio = (float) (mBitmap.getWidth() / mBitmap.getHeight());
                        mHeight = (int) (mSize / mRatio);
                        mWidth = mSize;
                    }
                    LinearLayoutCompat.LayoutParams mLayoutParams = new LinearLayoutCompat.LayoutParams(mWidth, mHeight);
                    mLayoutParams.gravity = Gravity.CENTER;
                    mImage.setLayoutParams(mLayoutParams);
                    mImage.setVisibility(View.VISIBLE);
                } catch (IOException ignored) {}
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 0 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent addImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            try {
                startActivityForResult(addImage, 0);
            } catch (ActivityNotFoundException ignored) {}
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        Utils.toggleKeyboard(mContents, this);
    }

    @Override
    public void onBackPressed() {
        if (isUnsavedNote()) {
            if (Utils.getBoolean("auto_save", false, this)) {
                saveNote();
            } else {
                new MaterialAlertDialogBuilder(this)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(R.string.app_name)
                        .setMessage(getString(R.string.discard_note))
                        .setCancelable(false)
                        .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                        })
                        .setPositiveButton(R.string.discard, (dialogInterface, i) -> exit(this)).show();
            }
        } else {
            exit(this);
        }
    }

}