package com.sunilpaulmathew.snotz.activities;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.sNotzColor;

import java.text.DateFormat;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.ThemeUtils.sThemeUtils;

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
        AppCompatImageButton mExpand = findViewById(R.id.expand);
        MaterialButton mSearch = findViewById(R.id.search_button);
        MaterialButton mSort = findViewById(R.id.sort_button);
        MaterialButton mQRCode = findViewById(R.id.qrcode_button);
        MaterialButton mInfo = findViewById(R.id.info_button);
        MaterialButton mSettings = findViewById(R.id.settings_button);
        AppCompatImageView mFABClick = findViewById(R.id.click_fab);
        AppCompatImageView mNoteClick = findViewById(R.id.click_note);
        AppCompatImageView mNoteLongClick = findViewById(R.id.longclick_note);
        LinearLayoutCompat mWelcomeLayout = findViewById(R.id.layout_welcome);
        MaterialCardView mCardNote = findViewById(R.id.card_note);
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

        mNote.setTextColor(sNotzColor.getTextColor(this));
        mOne.setTextColor(sCommonUtils.getColor(R.color.color_orange, this));
        mTwo.setTextColor(getTextColorNormal());
        mThree.setTextColor(getTextColorNormal());
        mAddButton.setColorFilter(sNotzColor.getAppAccentColor(this));
        mArrowBack.setColorFilter(sNotzColor.getAppAccentColor(this));
        mArrowFront.setColorFilter(sNotzColor.getAppAccentColor(this));
        mSettings.setIconTint(ColorStateList.valueOf(sNotzColor.getAppAccentColor(this)));
        mSearch.setIconTint(ColorStateList.valueOf(sNotzColor.getAppAccentColor(this)));
        mQRCode.setIconTint(ColorStateList.valueOf(sNotzColor.getAppAccentColor(this)));
        mInfo.setIconTint(ColorStateList.valueOf(sNotzColor.getAppAccentColor(this)));
        mAddButton.setColorFilter(sNotzColor.getAppAccentColor(this));
        mSkip.setTextColor(sNotzColor.getAppAccentColor(this));
        mSort.setIconTint(ColorStateList.valueOf(sNotzColor.getAppAccentColor(this)));
        mTitle.setTextColor(sNotzColor.getAppAccentColor(this));
        mWelcomeLayout.setBackgroundColor(sNotzColor.getAppAccentColor(this));

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
                mOne.setTextColor(getTextColorNormal());
                mTwo.setTextColor(getTextColorNormal());
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
                mOne.setTextColor(getTextColorNormal());
                mTwo.setTextColor(sCommonUtils.getColor(R.color.color_blue, this));
                mThree.setTextColor(getTextColorNormal());
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
                mTwo.setTextColor(getTextColorNormal());
                mThree.setTextColor(getTextColorNormal());
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
                mOne.setTextColor(getTextColorNormal());
                mTwo.setTextColor(sCommonUtils.getColor(R.color.color_blue, this));
                mThree.setTextColor(sNotzColor.getAccentColor(this));
            }
        });

        mCardNote.setCardBackgroundColor(sNotzColor.getAccentColor(this));
        mDate.setText(DateFormat.getDateTimeInstance().format(System.currentTimeMillis()));
        mDate.setTextColor(getTextColorNormal());
        mExpand.setColorFilter(getTextColorNormal());
    }

    private int getTextColorNormal() {
        return sCommonUtils.getColor(sThemeUtils.isDarkTheme(this) ? R.color.color_white : R.color.color_black, this);
    }

}