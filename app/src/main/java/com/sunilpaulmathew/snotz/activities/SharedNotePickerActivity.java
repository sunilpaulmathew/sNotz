package com.sunilpaulmathew.snotz.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.NotePicker;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2020
 */
public class SharedNotePickerActivity extends AppCompatActivity {

    private String mNote = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_bar);

        ProgressBar mProgressBar = findViewById(R.id.progress);

        if (getIntent().getAction().equals(Intent.ACTION_SEND)) {
            // Handle notes shared by Other Apps
            Parcelable parcelable = getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
            if (parcelable != null) {
                if (!(parcelable instanceof Uri)) {
                    parcelable = Uri.parse(parcelable.toString());
                }
                Uri uri = (Uri) parcelable;
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    for (int result = bis.read(); result != -1; result = bis.read()) {
                        buf.write((byte) result);
                    }
                    mNote = buf.toString("UTF-8");
                } catch (IOException ignored) {}
            } else if (getIntent().getStringExtra(Intent.EXTRA_TEXT) != null) {
                mNote = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            }

            new NotePicker(mNote, mProgressBar, this).handleNotes();
        }
    }

}