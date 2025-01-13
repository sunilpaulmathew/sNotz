package com.sunilpaulmathew.snotz.activities;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.adapters.ColorCustomizationsAdapter;
import com.sunilpaulmathew.snotz.utils.Security;
import com.sunilpaulmathew.snotz.utils.sNotzColor;
import com.sunilpaulmathew.snotz.utils.serializableItems.CheckListItems;

import java.util.ArrayList;

import in.sunilpaulmathew.colorpicker.ColorPickerDialog;
import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on February 25, 2023
 */
public class ColorCustomizationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_customizations);

        MaterialButton mStartButton = findViewById(R.id.start);
        MaterialTextView mTitle = findViewById(R.id.title);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ColorCustomizationsAdapter mRecycleViewAdapter = new ColorCustomizationsAdapter(getData());
        mRecyclerView.setAdapter(mRecycleViewAdapter);

        mTitle.setTextColor(sNotzColor.getAppAccentColor(this));

        mStartButton.setOnClickListener(v -> Security.launchMainActivity(this));

        mRecycleViewAdapter.setOnItemClickListener((position, v) -> ColorPickerDialog
                .with(this)
                .setTitle(R.string.choose_color)
                .initialColor(sNotzColor.getDefaultColor(position, this))
                .density(12)
                .setOnColorSelectedListener(selectedColor -> {
                })
                .setPositiveButton(R.string.ok, (dialog, selectedColor, allColors) -> {
                    setColor(position, selectedColor, this);
                    recreate();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {}
                ).build().show()
        );

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Security.launchMainActivity(ColorCustomizationsActivity.this);
            }
        });
    }

    private ArrayList<CheckListItems> getData() {
        ArrayList<CheckListItems> mData = new ArrayList<>();
        mData.add(new CheckListItems(getString(R.string.color_accent), false, false));
        mData.add(new CheckListItems(getString(R.string.note_color_background), false, false));
        mData.add(new CheckListItems(getString(R.string.note_color_text), false, false));
        mData.add(new CheckListItems(getString(R.string.check_list_widget_color), false, false));
        return mData;
    }

    private static void setColor(int position, int color, Context context) {
        if (position == 0) {
            sCommonUtils.saveInt("app_accent_color", color, context);
        } else if (position == 1) {
            sCommonUtils.saveInt("accent_color", color, context);
        } else if (position == 2) {
            sCommonUtils.saveInt("text_color", color, context);
        } else {
            sCommonUtils.saveInt("checklist_color", color, context);
        }
    }

}