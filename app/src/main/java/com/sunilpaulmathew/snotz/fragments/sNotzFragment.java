package com.sunilpaulmathew.snotz.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.activities.AboutActivity;
import com.sunilpaulmathew.snotz.activities.CreateNoteActivity;
import com.sunilpaulmathew.snotz.activities.RemindersActivity;
import com.sunilpaulmathew.snotz.activities.SettingsActivity;
import com.sunilpaulmathew.snotz.adapters.NotesAdapter;
import com.sunilpaulmathew.snotz.utils.AsyncTasks;
import com.sunilpaulmathew.snotz.utils.Common;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzColor;
import com.sunilpaulmathew.snotz.utils.sNotzData;
import com.sunilpaulmathew.snotz.utils.sNotzReminders;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 01, 2021
 */
public class sNotzFragment extends Fragment {

    private AppCompatImageButton mAddIcon, mMenu, mSearchButton, mSortButton;
    private MaterialCardView mAddNoteCard;
    private MaterialTextView mAppTitle;
    private AppCompatEditText mSearchWord;
    private boolean mExit;
    private final Handler mHandler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_main, container, false);

        mAppTitle = mRootView.findViewById(R.id.app_title);
        mSearchButton = mRootView.findViewById(R.id.search_button);
        mSortButton = mRootView.findViewById(R.id.sort_button);
        mMenu = mRootView.findViewById(R.id.settings_button);
        mAddIcon = mRootView.findViewById(R.id.add_note_icon);
        mAddNoteCard = mRootView.findViewById(R.id.add_note_card);
        mSearchWord = mRootView.findViewById(R.id.search_word);
        LinearLayout mProgressLayout = mRootView.findViewById(R.id.progress_layout);

        mSearchWord.setTextColor(Color.RED);

        Common.initializeRecyclerView(R.id.recycler_view, mRootView);

        Common.getRecyclerView().setLayoutManager(new GridLayoutManager(requireActivity(), Utils.getSpanCount(requireActivity())));
        Common.getRecyclerView().addItemDecoration(new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL));

        loadUI(mProgressLayout, requireActivity()).execute();

        mAddIcon.setColorFilter(sNotzColor.getAccentColor(requireActivity()));
        mAddNoteCard.setCardBackgroundColor(sNotzColor.getTextColor(requireActivity()));

        mAddNoteCard.setOnClickListener(v -> {
            Common.setNote(null);
            Common.setImageString(null);
            Common.isHiddenNote(false);
            Common.setBackgroundColor(123456789);
            Common.setTextColor(123456789);
            Intent createNote = new Intent(requireActivity(), CreateNoteActivity.class);
            startActivity(createNote);
        });
        mSearchButton.setOnClickListener(v -> {
            mSearchButton.setVisibility(View.GONE);
            mSortButton.setVisibility(View.GONE);
            mMenu.setVisibility(View.GONE);
            mSearchWord.setVisibility(View.VISIBLE);
            Utils.toggleKeyboard(mSearchWord, requireActivity());
        });

        mSearchWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                Common.setSearchText(s.toString().toLowerCase());
                loadUI(mProgressLayout, requireActivity()).execute();
            }
        });

        mSortButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireActivity(), mSortButton);
            if (Utils.exist(requireActivity().getFilesDir().getPath() + "/snotz")) {
                Menu menu = popupMenu.getMenu();
                SubMenu sort = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.sort_by));
                sort.add(0, 1, Menu.NONE, getString(R.string.created_order)).setCheckable(true)
                        .setChecked(Utils.getInt("sort_notes", 0, requireActivity()) == 2);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    sort.add(0, 2, Menu.NONE, getString(R.string.note_color_background)).setCheckable(true)
                            .setChecked(Utils.getInt("sort_notes", 0, requireActivity()) == 1);
                }
                sort.add(0, 3, Menu.NONE, getString(R.string.az_order)).setCheckable(true)
                        .setChecked(Utils.getInt("sort_notes", 0, requireActivity()) == 0);
                sort.setGroupCheckable(0, true, true);
                menu.add(Menu.NONE, 4, Menu.NONE, getString(R.string.reverse_order)).setCheckable(true)
                        .setChecked(Utils.getBoolean("reverse_order", false, requireActivity()));
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 0:
                        break;
                    case 1:
                        if (Utils.getInt("sort_notes", 0, requireActivity()) != 2) {
                            Utils.saveInt("sort_notes", 2, requireActivity());
                            loadUI(mProgressLayout, requireActivity()).execute();
                        }
                        break;
                    case 2:
                        if (Utils.getInt("sort_notes", 0, requireActivity()) != 1) {
                            Utils.saveInt("sort_notes", 1, requireActivity());
                            loadUI(mProgressLayout, requireActivity()).execute();
                        }
                        break;
                    case 3:
                        if (Utils.getInt("sort_notes", 0, requireActivity()) != 0) {
                            Utils.saveInt("sort_notes", 0, requireActivity());
                            loadUI(mProgressLayout, requireActivity()).execute();
                        }
                        break;
                    case 4:
                        Utils.saveBoolean("reverse_order", !Utils.getBoolean("reverse_order", false, requireActivity()), requireActivity());
                        loadUI(mProgressLayout, requireActivity()).execute();
                        break;
                }
                return false;
            });
            popupMenu.show();
        });

        mMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireActivity(), mMenu);
            Menu menu = popupMenu.getMenu();
            menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.settings));
            if (Utils.exist(requireActivity().getCacheDir().getPath() + "/reminders") && sNotzReminders
                    .getRawData(requireActivity()).size() > 0) {
                menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.reminders));
            }
            menu.add(Menu.NONE, 2, Menu.NONE, getString(R.string.about));
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 0:
                        Intent settings = new Intent(requireActivity(), SettingsActivity.class);
                        startActivity(settings);
                        break;
                    case 1:
                        Intent reminders = new Intent(requireActivity(), RemindersActivity.class);
                        startActivity(reminders);
                        break;
                    case 2:
                        Intent aboutsNotz = new Intent(requireActivity(), AboutActivity.class);
                        startActivity(aboutsNotz);
                        break;
                }
                return false;
            });
            popupMenu.show();
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mSearchWord.getVisibility() == View.VISIBLE) {
                    if (Common.getSearchText() != null) {
                        Common.setSearchText(null);
                        mSearchWord.setText(null);
                    }
                    mSearchWord.setVisibility(View.GONE);
                    mSearchButton.setVisibility(View.VISIBLE);
                    mSortButton.setVisibility(View.VISIBLE);
                    mMenu.setVisibility(View.VISIBLE);
                    return;
                }
                if (mExit) {
                    mExit = false;
                    requireActivity().finish();
                } else {
                    Utils.showSnackbar(mAppTitle, getString(R.string.press_back_exit));
                    mExit = true;
                    mHandler.postDelayed(() -> mExit = false, 2000);
                }
            }
        });

        return mRootView;
    }

    private static AsyncTasks loadUI(LinearLayout linearLayout, Activity activity) {
        return new AsyncTasks() {
            private NotesAdapter mNotesAdapter;
            @Override
            public void onPreExecute() {
                linearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void doInBackground() {
                mNotesAdapter = new NotesAdapter(sNotzData.getData(activity));
            }

            @Override
            public void onPostExecute() {
                try {
                    Common.getRecyclerView().setAdapter(mNotesAdapter);
                } catch (NullPointerException ignored) {}
                linearLayout.setVisibility(View.GONE);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Common.isReloading()) {
            Common.isReloading(false);
            mAddIcon.setColorFilter(sNotzColor.getAccentColor(requireActivity()));
            mAddNoteCard.setCardBackgroundColor(sNotzColor.getTextColor(requireActivity()));
        }
    }

}