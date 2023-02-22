package com.sunilpaulmathew.snotz.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;

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

        AppCompatImageButton mArrowFront = findViewById(R.id.arrow_front);
        AppCompatImageButton mArrowBack = findViewById(R.id.arrow_back);
        AppCompatImageView mFABClick = findViewById(R.id.click_fab);
        AppCompatImageView mNoteClick = findViewById(R.id.click_note);
        AppCompatImageView mNoteLongClick = findViewById(R.id.longclick_note);
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
                mNote.setText(getString(R.string.welcome_note_screen_three));
                mOne.setTextColor(sCommonUtils.getInt("accent_color", sCommonUtils.getColor(R.color.color_teal, this), this));
                mTwo.setTextColor(sCommonUtils.getInt("accent_color", sCommonUtils.getColor(R.color.color_teal, this), this));
                mThree.setTextColor(sCommonUtils.getColor(R.color.color_red, this));
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
            if (mFABClick.getVisibility() == View.VISIBLE &&
                    mFABClickMessage.getVisibility() == View.VISIBLE) {
                finish();
            } else if (mNoteClick.getVisibility() == View.VISIBLE &&
                    mNoteClickMessage.getVisibility() == View.VISIBLE) {
                mNoteClick.setVisibility(View.GONE);
                mNoteClickMessage.setVisibility(View.GONE);
                mFABClick.setVisibility(View.VISIBLE);
                mFABClickMessage.setVisibility(View.VISIBLE);
                mNote.setText(getString(R.string.welcome_note_screen_one));
                mOne.setTextColor(sCommonUtils.getColor(R.color.color_orange, this));
                mTwo.setTextColor(sCommonUtils.getInt("accent_color", sCommonUtils.getColor(R.color.color_teal, this), this));
                mThree.setTextColor(sCommonUtils.getInt("accent_color", sCommonUtils.getColor(R.color.color_teal, this), this));
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

}