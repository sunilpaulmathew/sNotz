package com.sunilpaulmathew.snotz.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.adapters.CheckListAdapter;
import com.sunilpaulmathew.snotz.utils.AppSettings;
import com.sunilpaulmathew.snotz.utils.CheckLists;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzColor;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;
import com.sunilpaulmathew.snotz.utils.serializableItems.CheckListItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 27, 2024
 */
public class CreateWidgetActivity extends AppCompatActivity {

    private AppCompatEditText mContents;
    private AppCompatImageButton mSave;
    private boolean mChecklist = false;
    private static int mSelectedColorBg, mSelectedColorTxt;
    private final List<CheckListItems> mData = new ArrayList<>();
    private RecyclerView mRecyclerViewCheckList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createwidget);

        mContents = findViewById(R.id.contents);
        AppCompatImageButton mChecklistSwitch = findViewById(R.id.checklist);
        mSave = findViewById(R.id.save);
        ContentLoadingProgressBar mProgress = findViewById(R.id.progress);
        LinearLayoutCompat mColorLayout = findViewById(R.id.color_layout);
        MaterialCardView mColorBackground = findViewById(R.id.color_background);
        MaterialCardView mColorText = findViewById(R.id.color_text);
        MaterialTextView mTitle = findViewById(R.id.title);
        NestedScrollView mNestedScrollView = findViewById(R.id.scroll_view);
        mRecyclerViewCheckList = findViewById(R.id.recycler_view_checklist);

        mTitle.setTextColor(sNotzColor.getAppAccentColor(this));

        mProgress.setBackgroundColor(sCommonUtils.getColor(R.color.color_black, this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mProgress.setIndeterminateTintList(ColorStateList.valueOf(sNotzColor.getAppAccentColor(this)));
        }
        mSave.setColorFilter(sNotzColor.getAppAccentColor(this));
        mColorBackground.setCardBackgroundColor(sNotzColor.getAccentColor(this));
        mNestedScrollView.setBackgroundColor(sNotzColor.getAccentColor(this));
        mSelectedColorBg = sNotzColor.getAccentColor(this);
        mColorText.setCardBackgroundColor(sNotzColor.getTextColor(this));
        mContents.setTextColor(sNotzColor.getTextColor(this));
        mContents.setHintTextColor(sNotzColor.getTextColor(this));
        mSelectedColorTxt = sNotzColor.getTextColor(this);

        mContents.setTextSize(TypedValue.COMPLEX_UNIT_SP, sCommonUtils.getInt("font_size", 18, this));
        mContents.setTypeface(null, AppSettings.getStyle(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mContents.setTextCursorDrawable(sNotzUtils.getColoredDrawable(mContents.getCurrentTextColor(), R.drawable.ic_cursor, this));
        }

        mRecyclerViewCheckList.setLayoutManager(new GridLayoutManager(this, Utils.getSpanCount(this)));
        mRecyclerViewCheckList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerViewCheckList.setAdapter(new CheckListAdapter(mData));

        mColorBackground.setOnClickListener(v -> ColorPickerDialogBuilder
                .with(this)
                .setTitle(R.string.choose_color)
                .initialColor(mSelectedColorBg)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(selectedColor -> {
                })
                .setPositiveButton(R.string.ok, (dialog, selectedColor, allColors) -> {
                    mNestedScrollView.setBackgroundColor(selectedColor);
                    mColorBackground.setCardBackgroundColor(selectedColor);
                    mSelectedColorBg = selectedColor;
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                }).build().show());

        mColorText.setOnClickListener(v -> ColorPickerDialogBuilder
                .with(this)
                .setTitle(R.string.choose_color)
                .initialColor(mSelectedColorTxt)
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

        mContents.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (!mChecklist) {
                    if (s != null && !s.toString().trim().isEmpty()) {
                        if (!sCommonUtils.getBoolean("auto_save", false, CreateWidgetActivity.this)) {
                            mSave.setVisibility(View.VISIBLE);
                        }
                    } else {
                        mSave.setVisibility(View.GONE);
                    }
                }
            }
        });

        mChecklistSwitch.setOnClickListener(v -> {
            if (mChecklist) {
                mChecklist = false;
                mRecyclerViewCheckList.setVisibility(View.GONE);
                mChecklistSwitch.setImageDrawable(sCommonUtils.getDrawable(R.drawable.ic_checklist, this));
                mNestedScrollView.setVisibility(View.VISIBLE);
                mColorLayout.setVisibility(View.VISIBLE);
                mContents.setVisibility(View.VISIBLE);
                mSave.setVisibility(View.GONE);
                mData.clear();
            } else {
                mChecklist = true;
                mRecyclerViewCheckList.setVisibility(View.VISIBLE);
                mData.add(new CheckListItems("", false, false));
                mChecklistSwitch.setImageDrawable(sCommonUtils.getDrawable(R.drawable.ic_note, this));
                if (!sCommonUtils.getBoolean("auto_save", false, CreateWidgetActivity.this)) {
                    mSave.setVisibility(View.VISIBLE);
                }
                mNestedScrollView.setVisibility(View.GONE);
                mColorLayout.setVisibility(View.GONE);
                mContents.setVisibility(View.GONE);
                mContents.setText(null);
            }
        });

        mSave.setOnClickListener(v -> {
            if (!mChecklist && !Objects.requireNonNull(mContents.getText()).toString().trim().isEmpty()
                            || mChecklist && !CheckLists.getChecklists(mData).isEmpty()) {
                saveNote(getIntent());
            } else {
                finish();
            }
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (sCommonUtils.getBoolean("auto_save", false, CreateWidgetActivity.this) &&
                        (!mChecklist && !Objects.requireNonNull(mContents.getText()).toString().trim().isEmpty()
                                || mChecklist && !CheckLists.getChecklists(mData).isEmpty())) {
                    saveNote(getIntent());
                } else {
                    finish();
                }
            }
        });
    }

    private void saveNote(Intent intent) {
        intent.putExtra("note", mChecklist ? CheckLists.getChecklistString(mData) : Objects.requireNonNull(mContents.getText()).toString().trim());
        intent.putExtra("hidden", false);
        intent.putExtra("colorBackground", mChecklist ? android.R.color.transparent : mSelectedColorBg);
        intent.putExtra("colorText", mChecklist ? sNotzColor.getAppAccentColor(this) : mSelectedColorTxt);
        intent.putExtra("id", sNotzUtils.generateNoteID(this));
        sNotzColor.updateRandomColorCode(this);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}