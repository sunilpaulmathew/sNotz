package com.sunilpaulmathew.snotz.activities;

import android.os.Bundle;
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
public class NotePickerActivity extends AppCompatActivity {

    private String mNote = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_bar);

        ProgressBar mProgressBar = findViewById(R.id.progress);

        if (getIntent().getData() != null) {
            // Handle notes picked from File Manager
            try {
                InputStream inputStream = getContentResolver().openInputStream(getIntent().getData());
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                for (int result = bis.read(); result != -1; result = bis.read()) {
                    buf.write((byte) result);
                }
                mNote = buf.toString("UTF-8");
            } catch (IOException ignored) {}

            new NotePicker(mNote, mProgressBar, this).handleNotes();
        }
    }

}