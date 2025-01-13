package com.sunilpaulmathew.snotz.activities;

import static com.sunilpaulmathew.snotz.R.string.discard_note;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.JsonObject;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.adapters.CheckListAdapter;
import com.sunilpaulmathew.snotz.utils.AppSettings;
import com.sunilpaulmathew.snotz.utils.CheckLists;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzColor;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;
import com.sunilpaulmathew.snotz.utils.sNotzWidgets;
import com.sunilpaulmathew.snotz.utils.serializableItems.CheckListItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import in.sunilpaulmathew.colorpicker.ColorPickerDialog;
import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.CommonUtils.sExecutor;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2020
 */
public class NoteActivity extends AppCompatActivity {

    private AppCompatEditText mContents;
    private boolean isChecklist = false, isNoteModified = false;
    private int mSelectedColorBg, mSelectedColorTxt;
    private final List<CheckListItems> mData = new ArrayList<>();
    public static final String COLOR_BG_INTENT = "bg_color";
    public static final String COLOR_TXT_INTENT = "txt_color";
    public static final String HIDDEN_INTENT = "hidden";
    public static final String NOTE_INTENT = "note";
    public static final String NOTE_ID_INTENT = "note_id";
    private static int mNoteID;
    private static String mNote = null;
    private SwitchMaterial mHidden;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        AppCompatImageButton mChecklist = findViewById(R.id.checklist);
        AppCompatImageButton mAZ = findViewById(R.id.az_button);
        AppCompatImageButton mSave = findViewById(R.id.save_button);
        AppCompatImageButton mReadingMode = findViewById(R.id.reading_mode);
        mContents = findViewById(R.id.contents);
        LinearLayoutCompat mColorLayout = findViewById(R.id.color_layout);
        MaterialCardView mColorBackground = findViewById(R.id.color_background);
        MaterialCardView mColorText = findViewById(R.id.color_text);
        NestedScrollView mScrollView = findViewById(R.id.scroll_view);
        MaterialTextView mTitle = findViewById(R.id.title);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mHidden = findViewById(R.id.hidden);

        int mColorBG = getIntent().getIntExtra(COLOR_BG_INTENT, Integer.MIN_VALUE);
        int mColorTxt = getIntent().getIntExtra(COLOR_TXT_INTENT, Integer.MIN_VALUE);
        boolean mHiddenNote = getIntent().getBooleanExtra(HIDDEN_INTENT, false);
        mNoteID = getIntent().getIntExtra(NOTE_ID_INTENT, -1);
        mNote = getIntent().getStringExtra(NOTE_INTENT);

        isChecklist = CheckLists.isValidCheckList(mNote);

        mHidden.setThumbTintList(ColorStateList.valueOf(sNotzColor.getAppAccentColor(this)));
        mAZ.setColorFilter(sNotzColor.getAppAccentColor(this));
        mChecklist.setColorFilter(sNotzColor.getAppAccentColor(this));
        mSave.setColorFilter(sNotzColor.getAppAccentColor(this));
        mTitle.setTextColor(sNotzColor.getAppAccentColor(this));

        mHidden.setChecked(mHiddenNote);

        if (mNoteID == -1 && mNote == null) {
            mChecklist.setVisibility(View.VISIBLE);
        }

        if (mColorBG != Integer.MIN_VALUE) {
            mColorBackground.setCardBackgroundColor(mColorBG);
            mScrollView.setBackgroundColor(mColorBG);
            mSelectedColorBg = mColorBG;
        } else {
            mColorBackground.setCardBackgroundColor(sNotzColor.getAccentColor(this));
            mScrollView.setBackgroundColor(sNotzColor.getAccentColor(this));
            mSelectedColorBg = sNotzColor.getAccentColor(this);
        }

        if (mColorTxt != Integer.MIN_VALUE) {
            mColorText.setCardBackgroundColor(mColorTxt);
            mContents.setTextColor(mColorTxt);
            mContents.setHintTextColor(mColorTxt);
            mSelectedColorTxt = mColorTxt;
        } else {
            mColorText.setCardBackgroundColor(sNotzColor.getTextColor(this));
            mContents.setTextColor(sNotzColor.getTextColor(this));
            mContents.setHintTextColor(sNotzColor.getTextColor(this));
            mSelectedColorTxt = sNotzColor.getTextColor(this);
        }

        mContents.setTextSize(TypedValue.COMPLEX_UNIT_SP, sCommonUtils.getInt("font_size", 18, this));
        mContents.setTypeface(null, AppSettings.getStyle(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mContents.setTextCursorDrawable(sNotzUtils.getColoredDrawable(mContents.getCurrentTextColor(), R.drawable.ic_cursor, this));
        }

        if (!isChecklist) {
            Utils.toggleKeyboard(mContents, this);
        }

        if (isChecklist && !sCommonUtils.getBoolean("auto_save", false, this)) {
            mSave.setVisibility(View.VISIBLE);
        }

        if (mNote != null) {
            if (mNoteID == -1) {
                isNoteModified = true;
            }
            mContents.setText(mNote);
        }

        if (mNote == null || isChecklist) {
            mReadingMode.setVisibility(View.GONE);
        }

        mColorLayout.setVisibility(isChecklist ? View.GONE : View.VISIBLE);
        mScrollView.setVisibility(isChecklist ? View.GONE : View.VISIBLE);
        mContents.setVisibility(isChecklist ? View.GONE : View.VISIBLE);
        mRecyclerView.setVisibility(!isChecklist ? View.GONE : View.VISIBLE);
        mAZ.setVisibility(!isChecklist ? View.GONE : View.VISIBLE);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, Utils.getSpanCount(this)));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        if (CheckLists.isValidCheckList(mNote)) {
            try {
                for (int i = 0; i < Objects.requireNonNull(CheckLists.getChecklists(mNote)).size(); i++) {
                    JsonObject object = Objects.requireNonNull(CheckLists.getChecklists(mNote)).get(i).getAsJsonObject();
                    mData.add(new CheckListItems(object.get("title").getAsString(), object.get("done").getAsBoolean(), false));
                }
            } catch (NullPointerException ignored) {
            }
        }

        mRecyclerView.setAdapter(new CheckListAdapter(mData));

        /*
         * Based on the following Stack Overflow discussion
         * https://stackoverflow.com/questions/55949538/update-onmove-changes-in-recycler-view-data-to-room-database
         */
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                if (viewHolder.getAdapterPosition() < target.getAdapterPosition()) {
                    for (int i = viewHolder.getAdapterPosition(); i < target.getAdapterPosition(); i++) {
                        Collections.swap(mData, i, i + 1);
                    }
                } else {
                    for (int i = viewHolder.getAdapterPosition(); i > target.getAdapterPosition(); i--) {
                        Collections.swap(mData, i, i - 1);
                    }
                }
                Objects.requireNonNull(recyclerView.getAdapter()).notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }

        });

        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mChecklist.setOnClickListener(v -> {
            if (isChecklist) {
                mContents.setVisibility(View.VISIBLE);
                mScrollView.setVisibility(View.VISIBLE);
                mColorLayout.setVisibility(View.VISIBLE);
                mChecklist.setImageDrawable(sCommonUtils.getDrawable(R.drawable.ic_checklist, this));
                Utils.toggleKeyboard(mContents, this);
                mRecyclerView.setVisibility(View.GONE);
                mSave.setVisibility(View.GONE);
                mAZ.setVisibility(View.GONE);
                isChecklist = false;
                mData.clear();
            } else {
                mContents.setText(null);
                mContents.setVisibility(View.GONE);
                mColorLayout.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mData.add(new CheckListItems("", false, false));
                mChecklist.setImageDrawable(sCommonUtils.getDrawable(R.drawable.ic_note, this));
                if (!sCommonUtils.getBoolean("auto_save", false, this)) {
                    mSave.setVisibility(View.VISIBLE);
                }
                mScrollView.setVisibility(View.GONE);
                mAZ.setVisibility(View.VISIBLE);
                hideKeyBoard();
                isNoteModified = false;
                isChecklist = true;
            }
        });

        mColorBackground.setOnClickListener(v -> ColorPickerDialog
                .with(this)
                .setTitle(R.string.choose_color)
                .initialColor(mSelectedColorBg)
                .density(12)
                .setOnColorSelectedListener(selectedColor -> {
                })
                .setPositiveButton(R.string.ok, (dialog, selectedColor, allColors) -> {
                    mScrollView.setBackgroundColor(selectedColor);
                    mColorBackground.setCardBackgroundColor(selectedColor);
                    mSelectedColorBg = selectedColor;
                    isNoteModified = true;
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                }).build().show()
        );

        mColorText.setOnClickListener(v -> ColorPickerDialog
                .with(this)
                .setTitle(R.string.choose_color)
                .initialColor(mSelectedColorTxt)
                .density(12)
                .setOnColorSelectedListener(selectedColor -> {
                })
                .setPositiveButton(R.string.ok, (dialog, selectedColor, allColors) -> {
                    mContents.setTextColor(selectedColor);
                    mContents.setHintTextColor(selectedColor);
                    mColorText.setCardBackgroundColor(selectedColor);
                    mSelectedColorTxt = selectedColor;
                    isNoteModified = true;
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                }).build().show()
        );

        mReadingMode.setOnClickListener(v -> {
            if (sCommonUtils.getBoolean("readmode_warning_hide", false, this)) {
                Intent readOnlyMode = new Intent(this, ReadNoteActivity.class);
                readOnlyMode.putExtra(ReadNoteActivity.NOTE_INTENT, mNote);
                startActivity(readOnlyMode);
                this.finish();
            } else {
                View checkBoxView = View.inflate(this, R.layout.layout_checkbox, null);
                MaterialCheckBox checkBox = checkBoxView.findViewById(R.id.checkbox);
                checkBox.setChecked(false);
                checkBox.setText(getString(R.string.hide));

                new MaterialAlertDialogBuilder(this)
                        .setCancelable(false)
                        .setView(checkBoxView)
                        .setTitle(R.string.app_name)
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage(getString(R.string.reading_mode_message) + (isNoteModified ? "\n\n" + getString(
                                R.string.reading_mode_warning) : ""))
                        .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                        })
                        .setPositiveButton(R.string.go_ahead, (dialogInterface, i) -> {
                            sCommonUtils.saveBoolean("readmode_warning_hide", checkBox.isChecked(), this);
                            Intent readOnlyMode = new Intent(this, ReadNoteActivity.class);
                            readOnlyMode.putExtra(ReadNoteActivity.NOTE_INTENT, mNote);
                            startActivity(readOnlyMode);
                            this.finish();
                        }).show();
            }
        });

        mHidden.setOnClickListener(v -> isNoteModified = true);

        mContents.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.toString().trim().isEmpty()) {
                    isNoteModified = true;
                    if (!sCommonUtils.getBoolean("auto_save", false, NoteActivity.this)) {
                        mSave.setVisibility(View.VISIBLE);
                    }
                } else {
                    mSave.setVisibility(View.GONE);
                }
            }
        });

        mAZ.setOnClickListener(v -> {
            if (mData.size() > 1) {
                Collections.sort(mData, (lhs, rhs) -> String.CASE_INSENSITIVE_ORDER.compare(lhs.getTitle(), rhs.getTitle()));
                mRecyclerView.setAdapter(new CheckListAdapter(mData));
            }
        });


        mSave.setOnClickListener(v -> {
            if (mContents.getText() != null && !mContents.getText().toString().trim().isEmpty() || !CheckLists.getChecklists(mData).isEmpty()) {
                saveNote(getIntent());
            } else {
                sCommonUtils.toast(getString(R.string.text_empty), this).show();
            }
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                exit().execute();
            }
        });
    }

    private void hideKeyBoard() {
        if (mContents != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mContents.getWindowToken(), 0);
        }
    }

    private void deleteNote(Intent intent) {
        intent.putExtra("id", mNoteID);
        intent.putExtra("toDelete", true);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void saveNote(Intent intent) {
        intent.putExtra("note", isChecklist ? CheckLists.getChecklistString(mData) : Objects.requireNonNull(mContents.getText()).toString().trim());
        intent.putExtra("hidden", mHidden.isChecked());
        intent.putExtra("colorBackground", isChecklist ? android.R.color.transparent : mSelectedColorBg);
        intent.putExtra("colorText", isChecklist ? sNotzColor.getAppAccentColor(this) : mSelectedColorTxt);
        intent.putExtra("id", mNoteID != -1 ? mNoteID : sNotzUtils.generateNoteID(this));
        intent.putExtra("isUpdate", mNoteID != -1);
        if (!isChecklist) {
            sNotzColor.updateRandomColorCode(this);
        }
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private boolean isNoteCleared() {
        return mNote != null && (mContents.getText() == null || mContents.getText().toString().trim().isEmpty());
    }

    private sExecutor exit() {
        return new sExecutor() {
            boolean delete = false, mModified = false;
            @Override
            public void onPreExecute() {
            }

            @Override
            public void doInBackground() {
                if (isChecklist) {
                    if (mNote != null && mNoteID == -1) {
                        mModified = !CheckLists.getChecklistString(mData).isEmpty();
                        delete = false;
                    } else {
                        for (CheckListItems items : mData) {
                            if (items.isModified()) {
                                mModified = true;
                            }
                            delete = items.getTitle().trim().isEmpty();
                        }
                    }
                } else {
                    if (mNoteID == -1) {
                        delete = false;
                        mModified = isNoteModified && !Objects.requireNonNull(mContents.getText()).toString().trim().isEmpty();
                    } else {
                        delete = isNoteCleared();
                        mModified = isNoteModified;
                    }
                }
            }

            @Override
            public void onPostExecute() {
                if (delete) {
                    if (mNoteID == -1) {
                        finish();
                    } else {
                        String[] mContents = Objects.requireNonNull(isChecklist ? sNotzWidgets.getWidgetText(mNote) : mNote).split("\\s+");
                        new MaterialAlertDialogBuilder(NoteActivity.this)
                                .setIcon(R.mipmap.ic_launcher)
                                .setTitle(R.string.app_name)
                                .setMessage(getString(R.string.delete_sure_question, mContents.length <= 2 ? isChecklist ? sNotzWidgets.getWidgetText(mNote) : mNote :
                                        mContents[0] + " " + mContents[1] + " " + mContents[2] + "..."))
                                .setCancelable(false)
                                .setNegativeButton(R.string.exit, (dialogInterface, i) -> finish())
                                .setPositiveButton(R.string.delete, (dialogInterface, i) -> {
                                    deleteNote(getIntent());
                                    finish();
                                }).show();
                    }
                } else if (mModified) {
                    if (sCommonUtils.getBoolean("auto_save", false, NoteActivity.this)) {
                        saveNote(getIntent());
                    } else {
                        new MaterialAlertDialogBuilder(NoteActivity.this)
                                .setIcon(R.mipmap.ic_launcher)
                                .setTitle(R.string.warning)
                                .setMessage(getString(discard_note))
                                .setCancelable(false)
                                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                                })
                                .setPositiveButton(R.string.discard, (dialog, which) -> finish()
                                ).show();
                    }
                } else {
                    finish();
                }
            }
        };
    }

}