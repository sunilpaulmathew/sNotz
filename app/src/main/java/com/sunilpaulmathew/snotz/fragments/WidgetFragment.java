package com.sunilpaulmathew.snotz.fragments;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.adapters.CheckListAdapter;
import com.sunilpaulmathew.snotz.adapters.WidgetAdapter;
import com.sunilpaulmathew.snotz.interfaces.EditTextInterface;
import com.sunilpaulmathew.snotz.utils.AppSettings;
import com.sunilpaulmathew.snotz.utils.CheckListItems;
import com.sunilpaulmathew.snotz.utils.CheckLists;
import com.sunilpaulmathew.snotz.utils.Common;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzColor;
import com.sunilpaulmathew.snotz.utils.sNotzData;
import com.sunilpaulmathew.snotz.utils.sNotzItems;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;
import com.sunilpaulmathew.snotz.utils.sNotzWidgets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.CommonUtils.sExecutor;
import in.sunilpaulmathew.sCommon.Dialog.sSingleItemDialog;
import in.sunilpaulmathew.sCommon.FileUtils.sFileUtils;
import in.sunilpaulmathew.sCommon.ThemeUtils.sThemeUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 19, 2021
 * Based mainly on the following Stack Overflow discussions
 * Reference 1: https://stackoverflow.com/questions/34588501/how-to-create-an-android-widget-with-options
 * Reference 2: https://stackoverflow.com/questions/6264809/how-to-build-a-simple-android-widget
 */
public class WidgetFragment extends Fragment {

    private AppCompatImageButton mSave;
    private boolean mChecklist = false;
    private static int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID, mSelectedColorBg, mSelectedColorTxt;
    private final List<CheckListItems> mData = new ArrayList<>();
    private LinearLayoutCompat mAddNewLayout;
    private MaterialTextView mAddNewText;
    private RecyclerView mRecyclerViewCheckList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_widget, container, false);

        AppCompatEditText mContents = mRootView.findViewById(R.id.contents);
        AppCompatImageButton mAddIcon = mRootView.findViewById(R.id.add_note_icon);
        mSave = mRootView.findViewById(R.id.save);
        ContentLoadingProgressBar mProgress = mRootView.findViewById(R.id.progress);
        LinearLayoutCompat mColorLayout = mRootView.findViewById(R.id.color_layout);
        MaterialCardView mAddNewCard = mRootView.findViewById(R.id.add_new_card);
        mAddNewLayout = mRootView.findViewById(R.id.add_new_layout);
        MaterialCardView mColorBackground = mRootView.findViewById(R.id.color_background);
        MaterialCardView mColorText = mRootView.findViewById(R.id.color_text);
        mAddNewText = mRootView.findViewById(R.id.add_new_title);
        MaterialTextView mTitle = mRootView.findViewById(R.id.title);
        NestedScrollView mNestedScrollView = mRootView.findViewById(R.id.scroll_view);
        RecyclerView mRecyclerView = mRootView.findViewById(R.id.recycler_view);
        mRecyclerViewCheckList = mRootView.findViewById(R.id.recycler_view_checklist);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        WidgetAdapter mRecycleViewAdapter = new WidgetAdapter(getData(requireActivity()));
        mRecyclerView.setAdapter(mRecycleViewAdapter);

        mTitle.setTextColor(sNotzColor.getAppAccentColor(requireActivity()));
        mAddNewText.setTextColor(sNotzColor.getAppAccentColor(requireActivity()));
        mAddIcon.setColorFilter(sCommonUtils.getInt("accent_color", sCommonUtils.getColor(R.color.color_teal, requireActivity()), requireActivity()));
        mAddNewCard.setCardBackgroundColor(sCommonUtils.getInt("text_color", sCommonUtils.getColor(R.color.color_white, requireActivity()), requireActivity()));
        mProgress.setBackgroundColor(sCommonUtils.getColor(R.color.color_black, requireActivity()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mProgress.setIndeterminateTintList(ColorStateList.valueOf(sNotzColor.getAppAccentColor(requireActivity())));
        }
        mSave.setColorFilter(sNotzColor.getAppAccentColor(requireActivity()));
        mColorBackground.setCardBackgroundColor(sNotzColor.getAccentColor(requireActivity()));
        mNestedScrollView.setBackgroundColor(sNotzColor.getAccentColor(requireActivity()));
        mSelectedColorBg = sNotzColor.getAccentColor(requireActivity());
        mColorText.setCardBackgroundColor(sNotzColor.getTextColor(requireActivity()));
        mContents.setTextColor(sNotzColor.getTextColor(requireActivity()));
        mContents.setHintTextColor(sNotzColor.getTextColor(requireActivity()));
        mSelectedColorTxt = sNotzColor.getTextColor(requireActivity());
        mAddNewLayout.setBackgroundColor(sCommonUtils.getColor(sThemeUtils.isDarkTheme(requireActivity()) ? R.color.color_dark : R.color.color_white, requireActivity()));

        mContents.setTextSize(TypedValue.COMPLEX_UNIT_SP, sCommonUtils.getInt("font_size", 18, requireActivity()));
        mContents.setTypeface(null, AppSettings.getStyle(requireActivity()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mContents.setTextCursorDrawable(sNotzUtils.getColoredDrawable(mContents.getCurrentTextColor(), R.drawable.ic_cursor, requireActivity()));
        }

        mRecyclerViewCheckList.setLayoutManager(new GridLayoutManager(requireActivity(), Utils.getSpanCount(requireActivity())));
        mRecyclerViewCheckList.addItemDecoration(new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerViewCheckList.setAdapter(new CheckListAdapter(mData));

        mRecycleViewAdapter.setOnItemClickListener((position, v) -> {
            if (Common.isWorking()) return;
            if (getData(requireActivity()).get(position).isChecklist()) {
                sCommonUtils.saveString("appwidget" + mAppWidgetId, new File(getData(requireActivity()).get(position).getNote()).getAbsolutePath(), requireActivity());
                sNotzWidgets.create(mAppWidgetId, requireActivity());
            } else {
                sCommonUtils.saveString("appwidget" + mAppWidgetId, String.valueOf(getData(requireActivity()).get(position).getNoteID()), requireActivity());
                sNotzWidgets.create(mAppWidgetId, requireActivity());
            }
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
            if (Common.isWorking()) {
                return;
            }
            new sSingleItemDialog(-1, null,
                    new String[]{
                            getString(R.string.note),
                            getString(R.string.check_list)
                    }, requireActivity()) {

                @Override
                public void onItemSelected(int itemPosition) {
                    if (itemPosition == 0) {
                        mChecklist = false;
                        mContents.requestFocus();
                        mAddNewLayout.setVisibility(View.VISIBLE);
                        mNestedScrollView.setVisibility(View.VISIBLE);
                        mColorLayout.setVisibility(View.VISIBLE);
                    } else {
                        initializeCheckList();
                    }
                }
            }.show();
        });

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
                    @Override
                    public void onPreExecute() {
                        if (mChecklist) {
                            mRecyclerViewCheckList.setVisibility(View.GONE);
                        } else {
                            mNestedScrollView.setVisibility(View.GONE);
                            mColorLayout.setVisibility(View.GONE);
                        }
                        mProgress.setVisibility(View.VISIBLE);
                        mSave.setVisibility(View.GONE);
                        Common.isWorking(true);
                    }

                    @Override
                    public void doInBackground() {
                        if (mChecklist) {
                            if (CheckLists.getChecklists(mData).size() == 0) return;
                            JsonObject mJSONObject = new JsonObject();
                            mJSONObject.add("checklist", CheckLists.getChecklists(mData));
                            sFileUtils.create(mJSONObject.toString(), new File(requireActivity().getExternalFilesDir("checklists"), CheckLists.getCheckListName()));

                            sCommonUtils.saveString("appwidget" + mAppWidgetId, new File(requireActivity().getExternalFilesDir("checklists"), CheckLists.getCheckListName()).getAbsolutePath(), requireActivity());
                        } else {
                            JsonObject mJSONObject;
                            JsonArray mJSONArray;
                            JsonObject note = new JsonObject();
                            int mNoteID;
                            if (sFileUtils.exist(new File(requireActivity().getFilesDir(), "snotz"))) {
                                mJSONObject = sNotzData.getJSONObject(sFileUtils.read(new File(requireActivity().getFilesDir(), "snotz")));
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
                            sFileUtils.create(json, new File(requireActivity().getFilesDir(), "snotz"));
                            sNotzColor.updateRandomColorCode(requireActivity());

                            sCommonUtils.saveString("appwidget" + mAppWidgetId, String.valueOf(mNoteID), requireActivity());
                        }
                        sNotzWidgets.create(mAppWidgetId, requireActivity());
                    }

                    @Override
                    public void onPostExecute() {
                        Common.isWorking(false);
                        mProgress.setVisibility(View.GONE);
                    }
                }.execute()
        );

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mChecklist) {
                    new MaterialAlertDialogBuilder(requireActivity())
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(R.string.warning)
                            .setMessage(getString(R.string.discard_note))
                            .setCancelable(false)
                            .setNegativeButton(R.string.cancel, (dialog, which) -> {
                            })
                            .setPositiveButton(R.string.discard, (dialog, which) -> requireActivity().finish()).show();
                } else if (mContents.getText() != null && !mContents.getText().toString().trim().isEmpty()) {
                    new MaterialAlertDialogBuilder(requireActivity())
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(R.string.app_name)
                            .setMessage(getString(R.string.discard_note))
                            .setCancelable(false)
                            .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                            })
                            .setPositiveButton(R.string.discard, (dialogInterface, i) -> requireActivity().finish()).show();
                } else {
                    requireActivity().finish();
                }
            }
        });

        return mRootView;
    }

    private void initializeCheckList() {
        new EditTextInterface(null, getString(R.string.check_list_create_question), requireActivity()) {

            @Override
            public void positiveButtonLister(Editable s) {
                if (s != null && !s.toString().trim().isEmpty()) {
                    if (sFileUtils.exist(new File(requireActivity().getExternalFilesDir("checklists"), s.toString().trim()))) {
                        new MaterialAlertDialogBuilder(requireActivity())
                                .setMessage(getString(R.string.check_list_exist_warning))
                                .setNegativeButton(getString(R.string.change_name), (dialogInterface, i) -> initializeCheckList())
                                .setPositiveButton(getString(R.string.replace), (dialogInterface, i) -> {
                                    mAddNewLayout.setVisibility(View.VISIBLE);
                                    mAddNewText.setText(s.toString().trim());
                                    mSave.setVisibility(View.VISIBLE);
                                    CheckLists.setCheckListName(s.toString().trim());
                                    mRecyclerViewCheckList.setVisibility(View.VISIBLE);
                                    mChecklist = true;
                                }).show();
                    } else {
                        mAddNewLayout.setVisibility(View.VISIBLE);
                        mAddNewText.setText(s.toString().trim());
                        mSave.setVisibility(View.VISIBLE);
                        CheckLists.setCheckListName(s.toString().trim());
                        mRecyclerViewCheckList.setVisibility(View.VISIBLE);
                        mData.add(new CheckListItems("", false));
                        mChecklist = true;
                    }
                } else {
                    sCommonUtils.snackBar(requireActivity().findViewById(android.R.id.content), getString(R.string.check_list_name_empty_message)).show();
                }
            }
        }.show();
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