package com.sunilpaulmathew.snotz.fragments;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.adapters.WidgetNotesAdapter;
import com.sunilpaulmathew.snotz.providers.WidgetProvider;
import com.sunilpaulmathew.snotz.utils.AppSettings;
import com.sunilpaulmathew.snotz.utils.Common;
import com.sunilpaulmathew.snotz.utils.sNotzColor;
import com.sunilpaulmathew.snotz.utils.sNotzData;
import com.sunilpaulmathew.snotz.utils.sNotzItems;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.sunilpaulmathew.sCommon.Utils.sExecutor;
import in.sunilpaulmathew.sCommon.Utils.sUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 19, 2021
 * Based mainly on the following Stack Overflow discussions
 * Reference 1: https://stackoverflow.com/questions/34588501/how-to-create-an-android-widget-with-options
 * Reference 2: https://stackoverflow.com/questions/6264809/how-to-build-a-simple-android-widget
 */
public class WidgetNotesFragment extends Fragment {

    private static int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID, mSelectedColorBg, mSelectedColorTxt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_widget_notes, container, false);

        AppCompatEditText mContents = mRootView.findViewById(R.id.contents);
        AppCompatImageButton mSave = mRootView.findViewById(R.id.save_button);
        LinearLayoutCompat mNoteEditor = mRootView.findViewById(R.id.main_layout);
        MaterialCardView mAddNewCard = mRootView.findViewById(R.id.add_new);
        MaterialCardView mColorBackground = mRootView.findViewById(R.id.color_background);
        MaterialCardView mColorText = mRootView.findViewById(R.id.color_text);
        NestedScrollView mNestedScrollView = mRootView.findViewById(R.id.scroll_view);
        ProgressBar mProgress = mRootView.findViewById(R.id.progress);
        RecyclerView mRecyclerView = mRootView.findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        WidgetNotesAdapter mRecycleViewAdapter = new WidgetNotesAdapter(getData(requireActivity()));
        mRecyclerView.setAdapter(mRecycleViewAdapter);

        mRecycleViewAdapter.setOnItemClickListener((position, v) -> {
            if (Common.isWorking()) return;
            create(getData(requireActivity()).get(position).getNoteID());
        });

        Intent intent = requireActivity().getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            requireActivity().finish();
        }

        mAddNewCard.setOnClickListener(v -> {
            if (Common.isWorking()) return;
            if (mNoteEditor.getVisibility() == View.VISIBLE) {
                mNoteEditor.setVisibility(View.GONE);
                mSave.setVisibility(View.GONE);
                mContents.clearFocus();
            } else {
                mNoteEditor.setVisibility(View.VISIBLE);
                mSave.setVisibility(mContents.getText() != null && !mContents.getText().toString().trim().isEmpty() &&
                        !mContents.getText().toString().trim().equals("") ? View.VISIBLE : View.GONE);
                mContents.requestFocus();
            }
        });

        mColorBackground.setCardBackgroundColor(sNotzColor.getAccentColor(requireActivity()));
        mNestedScrollView.setBackgroundColor(sNotzColor.getAccentColor(requireActivity()));
        mSelectedColorBg = sNotzColor.getAccentColor(requireActivity());
        mColorText.setCardBackgroundColor(sNotzColor.getTextColor(requireActivity()));
        mContents.setTextColor(sNotzColor.getTextColor(requireActivity()));
        mContents.setHintTextColor(sNotzColor.getTextColor(requireActivity()));
        mSelectedColorTxt = sNotzColor.getTextColor(requireActivity());

        mContents.setTextSize(TypedValue.COMPLEX_UNIT_SP, sUtils.getInt("font_size", 18, requireActivity()));
        mContents.setTypeface(null, AppSettings.getStyle(requireActivity()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mContents.setTextCursorDrawable(sNotzUtils.getColoredDrawable(mContents.getCurrentTextColor(), R.drawable.ic_cursor, requireActivity()));
        }

        mColorBackground.setOnClickListener(v -> {
            if (Common.isWorking()) return;
            ColorPickerDialogBuilder
                    .with(requireActivity())
                    .setTitle(R.string.choose_color)
                    .initialColor(sNotzColor.getAccentColor(requireActivity()))
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setOnColorSelectedListener(selectedColor -> {
                    })
                    .setPositiveButton(R.string.ok, (dialog, selectedColor, allColors) -> {
                        mNestedScrollView.setBackgroundColor(selectedColor);
                        mColorBackground.setCardBackgroundColor(selectedColor);
                        mSelectedColorBg = selectedColor;
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    }).build().show();
        });

        mColorText.setOnClickListener(v -> {
            if (Common.isWorking()) return;
            ColorPickerDialogBuilder
                    .with(requireActivity())
                    .setTitle(R.string.choose_color)
                    .initialColor(sNotzColor.getAccentColor(requireActivity()))
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setOnColorSelectedListener(selectedColor -> {
                    })
                    .setPositiveButton(R.string.ok, (dialog, selectedColor, allColors) -> {
                        mContents.setTextColor(selectedColor);
                        mContents.setHintTextColor(selectedColor);
                        mColorText.setCardBackgroundColor(selectedColor);
                        mSelectedColorTxt = selectedColor;
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    }).build().show();
        });

        mContents.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.toString().trim().isEmpty() && !s.toString().trim().equals("")) {
                    mSave.setVisibility(View.VISIBLE);
                } else {
                    mSave.setVisibility(View.GONE);
                }
            }
        });

        mSave.setOnClickListener(v ->
                new sExecutor() {
                    private int mNoteID;
                    @Override
                    public void onPreExecute() {
                        mNoteEditor.setVisibility(View.GONE);
                        mProgress.setVisibility(View.VISIBLE);
                        mSave.setVisibility(View.GONE);
                        Common.isWorking(true);
                    }

                    @Override
                    public void doInBackground() {
                        JsonObject mJSONObject;
                        JsonArray mJSONArray;
                        JsonObject note = new JsonObject();
                        if (sUtils.exist(new File(requireActivity().getFilesDir(),"snotz"))) {
                            mJSONObject = sNotzData.getJSONObject(sUtils.read(new File(requireActivity().getFilesDir(), "snotz")));
                            mJSONArray = Objects.requireNonNull(mJSONObject).getAsJsonArray("sNotz");
                            note.addProperty("noteID", sNotzUtils.generateNoteID(requireActivity()));
                            mNoteID = sNotzUtils.generateNoteID(requireActivity());
                        } else {
                            mJSONObject = new JsonObject();
                            mJSONArray = new JsonArray();
                            note.addProperty("noteID", 0);
                            mNoteID = 0;
                        }
                        note.addProperty("note", Objects.requireNonNull(mContents.getText()).toString());
                        note.addProperty("date", System.currentTimeMillis());
                        note.addProperty("image", "");
                        note.addProperty("hidden", false);
                        note.addProperty("colorBackground", mSelectedColorBg);
                        note.addProperty("colorText", mSelectedColorTxt);
                        mJSONArray.add(note);
                        mJSONObject.add("sNotz", mJSONArray);
                        Gson gson = new Gson();
                        String json = gson.toJson(mJSONObject);
                        sUtils.create(json, new File(requireActivity().getFilesDir(),"snotz"));
                    }

                    @Override
                    public void onPostExecute() {
                        Common.isWorking(false);
                        mProgress.setVisibility(View.GONE);
                        create(mNoteID);
                    }
                }.execute());

        return mRootView;
    }

    private void create(int noteID) {
        sUtils.saveString("appwidget" + mAppWidgetId, String.valueOf(noteID), requireActivity());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(requireActivity());
        WidgetProvider.update(appWidgetManager, mAppWidgetId, requireActivity());
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        requireActivity().setResult(Activity.RESULT_OK, resultValue);
        requireActivity().finish();
    }

    private static List<sNotzItems> getData(Activity activity) {
        List<sNotzItems> mData = new ArrayList<>();
        for (sNotzItems item : sNotzData.getData(activity)) {
            if (!item.isHidden()) {
                mData.add(item);
            }
        }
        return mData;
    }

}