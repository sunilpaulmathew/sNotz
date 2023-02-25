package com.sunilpaulmathew.snotz.activities;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.adapters.ColorCustomizationsAdapter;
import com.sunilpaulmathew.snotz.utils.CheckListItems;
import com.sunilpaulmathew.snotz.utils.Common;
import com.sunilpaulmathew.snotz.utils.sNotzColor;

import java.util.ArrayList;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on February 25, 2023
 */
public class ColorCustomizationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_customizations);

        MaterialCardView mStartCard = findViewById(R.id.start_card);
        MaterialTextView mTitle = findViewById(R.id.title);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ColorCustomizationsAdapter mRecycleViewAdapter = new ColorCustomizationsAdapter(getData());
        mRecyclerView.setAdapter(mRecycleViewAdapter);

        mStartCard.setCardBackgroundColor(sNotzColor.getAppAccentColor(this));
        mTitle.setTextColor(sNotzColor.getAppAccentColor(this));

        mStartCard.setOnClickListener(v -> {
            if (!sCommonUtils.getBoolean("color_customized", false, this)) {
                sCommonUtils.saveBoolean("color_customized", true, this);
            }
            finish();
        });

        mRecycleViewAdapter.setOnItemClickListener((position, v) -> ColorPickerDialogBuilder
                .with(this)
                .setTitle(R.string.choose_color)
                .initialColor(sNotzColor.getDefaultColor(position, this))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(selectedColor -> {
                })
                .setPositiveButton(R.string.ok, (dialog, selectedColor, allColors) -> {
                    setColor(position, selectedColor, this);
                    if (!Common.isReloading()) {
                        Common.isReloading(true);
                    }
                    recreate();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {}
                ).build().show()
        );
    }

    private ArrayList<CheckListItems> getData() {
        ArrayList<CheckListItems> mData = new ArrayList<>();
        mData.add(new CheckListItems(getString(R.string.color_accent_summary), false));
        mData.add(new CheckListItems(getString(R.string.color_select_dialog, getString(R.string.note_color_background)), false));
        mData.add(new CheckListItems(getString(R.string.color_select_dialog, getString(R.string.note_color_text)), false));
        mData.add(new CheckListItems(getString(R.string.check_list_widget_color_summary), false));
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

    @Override
    public void onBackPressed() {
        if (!sCommonUtils.getBoolean("color_customized", false, this)) {
            sCommonUtils.saveBoolean("color_customized", true, this);
        }
        super.onBackPressed();
    }

}