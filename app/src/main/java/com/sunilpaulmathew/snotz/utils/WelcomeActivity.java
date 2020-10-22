/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.snotz.utils;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.sunilpaulmathew.snotz.R;

import java.text.DateFormat;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 21, 2020
 */

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        AppCompatImageButton mArrowFront = findViewById(R.id.arrow_front);
        AppCompatImageButton mArrowBack = findViewById(R.id.arrow_back);
        AppCompatImageView mFABClick = findViewById(R.id.click_fab);
        AppCompatImageView mNoteClick = findViewById(R.id.click_note);
        AppCompatImageView mNoteLongClick = findViewById(R.id.longclick_note);
        AppCompatTextView mOne = findViewById(R.id.one);
        AppCompatTextView mTwo = findViewById(R.id.two);
        AppCompatTextView mThree = findViewById(R.id.three);
        AppCompatTextView mSkip = findViewById(R.id.skip);
        AppCompatTextView mFABClickMessage = findViewById(R.id.click_fab_message);
        AppCompatTextView mNoteClickMessage = findViewById(R.id.click_note_message);
        AppCompatTextView mNoteLongClickMessage = findViewById(R.id.longclick_note_message);
        AppCompatTextView mDate = findViewById(R.id.date);

        mOne.setTextColor(ContextCompat.getColor(this, R.color.color_orange));
        mTwo.setTextColor(sNotzColor.getAccentColor(this));
        mThree.setTextColor(sNotzColor.getAccentColor(this));

        mSkip.setOnClickListener(v -> finish());
        mArrowFront.setOnClickListener(v -> {
            if (mNoteLongClick.getVisibility() == View.VISIBLE &&
                    mNoteLongClickMessage.getVisibility() == View.VISIBLE) {
                finish();
            } else if (mNoteClick.getVisibility() == View.VISIBLE &&
                    mNoteClickMessage.getVisibility() == View.VISIBLE) {
                mNoteClick.setVisibility(View.GONE);
                mNoteClickMessage.setVisibility(View.GONE);
                mNoteLongClick.setVisibility(View.VISIBLE);
                mNoteLongClickMessage.setVisibility(View.VISIBLE);
                mOne.setTextColor(sNotzColor.getAccentColor(this));
                mTwo.setTextColor(sNotzColor.getAccentColor(this));
                mThree.setTextColor(ContextCompat.getColor(this, R.color.color_red));
            } else if (mFABClick.getVisibility() == View.VISIBLE &&
                    mFABClickMessage.getVisibility() == View.VISIBLE) {
                mFABClick.setVisibility(View.GONE);
                mFABClickMessage.setVisibility(View.GONE);
                mNoteClick.setVisibility(View.VISIBLE);
                mNoteClickMessage.setVisibility(View.VISIBLE);
                mOne.setTextColor(sNotzColor.getAccentColor(this));
                mTwo.setTextColor(ContextCompat.getColor(this, R.color.color_blue));
                mThree.setTextColor(sNotzColor.getAccentColor(this));
            }
        });
        mArrowBack.setOnClickListener(v -> {
            if (mFABClick.getVisibility() == View.VISIBLE &&
                    mFABClickMessage.getVisibility() == View.VISIBLE) {
                finish();
            } else if (mNoteClick.getVisibility() == View.VISIBLE &&
                    mNoteClickMessage.getVisibility() == View.VISIBLE) {
                mNoteClick.setVisibility(View.GONE);
                mNoteClickMessage.setVisibility(View.GONE);
                mFABClick.setVisibility(View.VISIBLE);
                mFABClickMessage.setVisibility(View.VISIBLE);
                mOne.setTextColor(ContextCompat.getColor(this, R.color.color_orange));
                mTwo.setTextColor(sNotzColor.getAccentColor(this));
                mThree.setTextColor(sNotzColor.getAccentColor(this));
            } else if (mNoteLongClick.getVisibility() == View.VISIBLE &&
                    mNoteLongClickMessage.getVisibility() == View.VISIBLE) {
                mNoteLongClick.setVisibility(View.GONE);
                mNoteLongClickMessage.setVisibility(View.GONE);
                mNoteClick.setVisibility(View.VISIBLE);
                mNoteClickMessage.setVisibility(View.VISIBLE);
                mOne.setTextColor(sNotzColor.getAccentColor(this));
                mTwo.setTextColor(ContextCompat.getColor(this, R.color.color_blue));
                mThree.setTextColor(sNotzColor.getAccentColor(this));
            }
        });
        mDate.setText(DateFormat.getDateTimeInstance().format(System.currentTimeMillis()));
    }

}