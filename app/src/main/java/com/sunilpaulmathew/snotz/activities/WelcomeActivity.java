package com.sunilpaulmathew.snotz.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.sNotzColor;

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
        MaterialTextView mNote = findViewById(R.id.note);
        MaterialTextView mOne = findViewById(R.id.one);
        MaterialTextView mTwo = findViewById(R.id.two);
        MaterialTextView mThree = findViewById(R.id.three);
        MaterialTextView mSkip = findViewById(R.id.skip);
        MaterialTextView mFABClickMessage = findViewById(R.id.click_fab_message);
        MaterialTextView mNoteClickMessage = findViewById(R.id.click_note_message);
        MaterialTextView mNoteLongClickMessage = findViewById(R.id.longclick_note_message);
        MaterialTextView mDate = findViewById(R.id.date);

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
                mNote.setText(getString(R.string.welcome_note_screen_three));
                mOne.setTextColor(sNotzColor.getAccentColor(this));
                mTwo.setTextColor(sNotzColor.getAccentColor(this));
                mThree.setTextColor(ContextCompat.getColor(this, R.color.color_red));
            } else if (mFABClick.getVisibility() == View.VISIBLE &&
                    mFABClickMessage.getVisibility() == View.VISIBLE) {
                mFABClick.setVisibility(View.GONE);
                mFABClickMessage.setVisibility(View.GONE);
                mNoteClick.setVisibility(View.VISIBLE);
                mNoteClickMessage.setVisibility(View.VISIBLE);
                mNote.setText(getString(R.string.welcome_note_screen_two));
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
                mNote.setText(getString(R.string.welcome_note_screen_one));
                mOne.setTextColor(ContextCompat.getColor(this, R.color.color_orange));
                mTwo.setTextColor(sNotzColor.getAccentColor(this));
                mThree.setTextColor(sNotzColor.getAccentColor(this));
            } else if (mNoteLongClick.getVisibility() == View.VISIBLE &&
                    mNoteLongClickMessage.getVisibility() == View.VISIBLE) {
                mNoteLongClick.setVisibility(View.GONE);
                mNoteLongClickMessage.setVisibility(View.GONE);
                mNoteClick.setVisibility(View.VISIBLE);
                mNoteClickMessage.setVisibility(View.VISIBLE);
                mNote.setText(getString(R.string.welcome_note_screen_two));
                mOne.setTextColor(sNotzColor.getAccentColor(this));
                mTwo.setTextColor(ContextCompat.getColor(this, R.color.color_blue));
                mThree.setTextColor(sNotzColor.getAccentColor(this));
            }
        });
        mDate.setText(DateFormat.getDateTimeInstance().format(System.currentTimeMillis()));
    }

}