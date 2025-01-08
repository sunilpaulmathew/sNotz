package com.sunilpaulmathew.snotz.activities;

import android.os.Bundle;
import android.util.TypedValue;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.AppSettings;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.ThemeUtils.sThemeUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2021
 */
public class ReadNoteActivity extends AppCompatActivity {

    public static final String NOTE_INTENT = "note";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readnote);

        MaterialTextView mContents = findViewById(R.id.contents);
        NestedScrollView mScrollView = findViewById(R.id.scroll_view);

        String mNote = getIntent().getStringExtra(NOTE_INTENT);

        if (mNote != null) {
            mContents.setText(mNote);
        }

        if (sThemeUtils.isDarkTheme(this)) {
            mContents.setTextColor(sCommonUtils.getColor(R.color.color_white, this));
            mScrollView.setBackgroundColor(sCommonUtils.getColor(R.color.color_black, this));
        } else {
            mContents.setTextColor(sCommonUtils.getColor(R.color.color_black, this));
            mScrollView.setBackgroundColor(sCommonUtils.getColor(R.color.color_white, this));
        }

        mContents.setTextSize(TypedValue.COMPLEX_UNIT_SP, sCommonUtils.getInt("font_size", 18, this));
        mContents.setTypeface(null, AppSettings.getStyle(this));
    }

}