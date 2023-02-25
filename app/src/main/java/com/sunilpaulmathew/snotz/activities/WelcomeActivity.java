package com.sunilpaulmathew.snotz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.sNotzColor;

import java.text.DateFormat;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 21, 2020
 */
public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        AppCompatImageButton mAddButton = findViewById(R.id.add_button);
        AppCompatImageButton mArrowFront = findViewById(R.id.arrow_front);
        AppCompatImageButton mArrowBack = findViewById(R.id.arrow_back);
        AppCompatImageButton mSearch = findViewById(R.id.search_button);
        AppCompatImageButton mSort = findViewById(R.id.sort_button);
        AppCompatImageButton mMenu = findViewById(R.id.settings_button);
        AppCompatImageView mFABClick = findViewById(R.id.click_fab);
        AppCompatImageView mNoteClick = findViewById(R.id.click_note);
        AppCompatImageView mNoteLongClick = findViewById(R.id.longclick_note);
        LinearLayoutCompat mWelcomeLayout = findViewById(R.id.layout_welcome);
        MaterialTextView mTitle = findViewById(R.id.title);
        MaterialTextView mNote = findViewById(R.id.note);
        MaterialTextView mOne = findViewById(R.id.one);
        MaterialTextView mTwo = findViewById(R.id.two);
        MaterialTextView mThree = findViewById(R.id.three);
        MaterialTextView mSkip = findViewById(R.id.skip);
        MaterialTextView mFABClickMessage = findViewById(R.id.click_fab_message);
        MaterialTextView mNoteClickMessage = findViewById(R.id.click_note_message);
        MaterialTextView mNoteLongClickMessage = findViewById(R.id.longclick_note_message);
        MaterialTextView mDate = findViewById(R.id.date);

        mOne.setTextColor(sCommonUtils.getColor(R.color.color_orange, this));
        mTwo.setTextColor(sCommonUtils.getInt("accent_color", sCommonUtils.getColor(R.color.color_teal, this), this));
        mThree.setTextColor(sCommonUtils.getInt("accent_color", sCommonUtils.getColor(R.color.color_teal, this), this));
        mAddButton.setColorFilter(sNotzColor.getAppAccentColor(this));
        mArrowBack.setColorFilter(sNotzColor.getAppAccentColor(this));
        mArrowFront.setColorFilter(sNotzColor.getAppAccentColor(this));
        mMenu.setColorFilter(sNotzColor.getAppAccentColor(this));
        mSearch.setColorFilter(sNotzColor.getAppAccentColor(this));
        mAddButton.setColorFilter(sNotzColor.getAppAccentColor(this));
        mSkip.setTextColor(sNotzColor.getAppAccentColor(this));
        mSort.setColorFilter(sNotzColor.getAppAccentColor(this));
        mTitle.setTextColor(sNotzColor.getAppAccentColor(this));
        mWelcomeLayout.setBackgroundColor(sNotzColor.getAppAccentColor(this));

        mSkip.setOnClickListener(v -> exit());
        mArrowFront.setOnClickListener(v -> {
            if (mNoteLongClick.getVisibility() == View.VISIBLE &&
                    mNoteLongClickMessage.getVisibility() == View.VISIBLE) {
                exit();
            } else if (mNoteClick.getVisibility() == View.VISIBLE &&
                    mNoteClickMessage.getVisibility() == View.VISIBLE) {
                mNoteClick.setVisibility(View.GONE);
                mNoteClickMessage.setVisibility(View.GONE);
                mNoteLongClick.setVisibility(View.VISIBLE);
                mNoteLongClickMessage.setVisibility(View.VISIBLE);
                mNote.setText(getString(R.string.welcome_note_screen_three));
                mOne.setTextColor(sCommonUtils.getInt("accent_color", sCommonUtils.getColor(R.color.color_teal, this), this));
                mTwo.setTextColor(sCommonUtils.getInt("accent_color", sCommonUtils.getColor(R.color.color_teal, this), this));
                mThree.setTextColor(sCommonUtils.getColor(R.color.color_red, this));
                if (!mArrowBack.isClickable()) {
                    mArrowBack.setClickable(true);
                }
            } else if (mFABClick.getVisibility() == View.VISIBLE &&
                    mFABClickMessage.getVisibility() == View.VISIBLE) {
                mFABClick.setVisibility(View.GONE);
                mFABClickMessage.setVisibility(View.GONE);
                mNoteClick.setVisibility(View.VISIBLE);
                mNoteClickMessage.setVisibility(View.VISIBLE);
                mNote.setText(getString(R.string.welcome_note_screen_two));
                mOne.setTextColor(sCommonUtils.getInt("accent_color", sCommonUtils.getColor(R.color.color_teal, this), this));
                mTwo.setTextColor(sCommonUtils.getColor(R.color.color_blue, this));
                mThree.setTextColor(sCommonUtils.getInt("accent_color", sCommonUtils.getColor(R.color.color_teal, this), this));
            }
        });
        mArrowBack.setOnClickListener(v -> {
            if (mNoteClick.getVisibility() == View.VISIBLE &&
                    mNoteClickMessage.getVisibility() == View.VISIBLE) {
                mNoteClick.setVisibility(View.GONE);
                mNoteClickMessage.setVisibility(View.GONE);
                mFABClick.setVisibility(View.VISIBLE);
                mFABClickMessage.setVisibility(View.VISIBLE);
                mNote.setText(getString(R.string.welcome_note_screen_one));
                mOne.setTextColor(sCommonUtils.getColor(R.color.color_orange, this));
                mTwo.setTextColor(sCommonUtils.getInt("accent_color", sCommonUtils.getColor(R.color.color_teal, this), this));
                mThree.setTextColor(sCommonUtils.getInt("accent_color", sCommonUtils.getColor(R.color.color_teal, this), this));
                if (mArrowBack.isClickable()) {
                    mArrowBack.setClickable(false);
                }
            } else if (mNoteLongClick.getVisibility() == View.VISIBLE &&
                    mNoteLongClickMessage.getVisibility() == View.VISIBLE) {
                mNoteLongClick.setVisibility(View.GONE);
                mNoteLongClickMessage.setVisibility(View.GONE);
                mNoteClick.setVisibility(View.VISIBLE);
                mNoteClickMessage.setVisibility(View.VISIBLE);
                mNote.setText(getString(R.string.welcome_note_screen_two));
                mOne.setTextColor(sCommonUtils.getInt("accent_color", sCommonUtils.getColor(R.color.color_teal, this), this));
                mTwo.setTextColor(sCommonUtils.getColor(R.color.color_blue, this));
                mThree.setTextColor(sCommonUtils.getInt("accent_color", sCommonUtils.getColor(R.color.color_teal, this), this));
            }
        });
        mDate.setText(DateFormat.getDateTimeInstance().format(System.currentTimeMillis()));
    }

    private void exit() {
        if (!sCommonUtils.getBoolean("color_customized", false, this)) {
            Intent colorCustomizations = new Intent(this, ColorCustomizationsActivity.class);
            startActivity(colorCustomizations);
            finish();
        } else {
            finish();
        }
    }

}