package com.sunilpaulmathew.snotz.fragments;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.JsonObject;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.adapters.CheckListAdapter;
import com.sunilpaulmathew.snotz.adapters.WidgetChecklistsAdapter;
import com.sunilpaulmathew.snotz.interfaces.DialogEditTextListener;
import com.sunilpaulmathew.snotz.providers.WidgetProvider;
import com.sunilpaulmathew.snotz.utils.AsyncTasks;
import com.sunilpaulmathew.snotz.utils.CheckListItems;
import com.sunilpaulmathew.snotz.utils.CheckLists;
import com.sunilpaulmathew.snotz.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 19, 2021
 * Based mainly on the following Stack Overflow discussions
 * Reference 1: https://stackoverflow.com/questions/34588501/how-to-create-an-android-widget-with-options
 * Reference 2: https://stackoverflow.com/questions/6264809/how-to-build-a-simple-android-widget
 */
public class WidgetChecklistsFragment extends Fragment {

    AppCompatImageButton mSave;
    private MaterialTextView mTitle;
    private static int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private final List<CheckListItems> mData = new ArrayList<>();
    private RecyclerView mRecyclerViewCheckList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_widget_checklists, container, false);

        mSave = mRootView.findViewById(R.id.save_button);
        MaterialCardView mAddNewCard = mRootView.findViewById(R.id.add_new);
        mTitle = mRootView.findViewById(R.id.title);
        RecyclerView mRecyclerView = mRootView.findViewById(R.id.recycler_view);
        mRecyclerViewCheckList = mRootView.findViewById(R.id.recycler_view_checklist);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        WidgetChecklistsAdapter mRecycleViewAdapter = new WidgetChecklistsAdapter(CheckLists.getCheckLists(requireActivity()));
        mRecyclerView.setAdapter(mRecycleViewAdapter);

        mRecyclerViewCheckList.setLayoutManager(new GridLayoutManager(requireActivity(), Utils.getSpanCount(requireActivity())));
        mRecyclerViewCheckList.addItemDecoration(new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerViewCheckList.setAdapter(new CheckListAdapter(mData));

        if (Utils.exist(requireActivity().getExternalFilesDir("checklists") + "/" + CheckLists.getCheckListName()) && CheckLists.getData(requireActivity()).size() > 0) {
            mData.addAll(CheckLists.getData(requireActivity()));
        } else {
            mData.add(new CheckListItems("", false));
        }

        mRecycleViewAdapter.setOnItemClickListener((position, v) -> create(CheckLists.getCheckLists(requireActivity())
                .get(position).getAbsolutePath()));

        Intent intent = requireActivity().getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            requireActivity().finish();
        }

        mAddNewCard.setOnClickListener(v -> {
            if (mRecyclerViewCheckList.getVisibility() == View.VISIBLE) {
                mTitle.setText(getString(R.string.add_new));
                mSave.setVisibility(View.GONE);
                mRecyclerViewCheckList.setVisibility(View.GONE);
            } else {
                if (CheckLists.getCheckListName() != null) {
                    mRecyclerViewCheckList.setVisibility(View.VISIBLE);
                } else {
                    createCheckList();
                }
                mSave.setVisibility(CheckLists.getCheckListName() != null ? View.VISIBLE : View.GONE);
                mTitle.setText(CheckLists.getCheckListName() != null ? CheckLists.getCheckListName() : getString(R.string.add_new));
            }
        });

        mAddNewCard.setOnLongClickListener(v -> {
            createCheckList();
            return true;
        });

        mSave.setOnClickListener(v -> saveCheckList());

        return mRootView;
    }

    private void saveCheckList() {
        if (CheckLists.getChecklists(mData).size() == 0) return;
        new AsyncTasks() {

            @Override
            public void onPreExecute() {
                mSave.setVisibility(View.GONE);
            }

            @Override
            public void doInBackground() {
                String mCheckListName = CheckLists.getCheckListName();
                JsonObject mJSONObject = new JsonObject();
                mJSONObject.add("checklist", CheckLists.getChecklists(mData));
                Utils.create(mJSONObject.toString(), requireActivity().getExternalFilesDir("checklists") + "/" + mCheckListName);
            }

            @Override
            public void onPostExecute() {
                if (Utils.exist(requireActivity().getExternalFilesDir("checklists") + "/" + CheckLists.getCheckListName())) {
                    create(requireActivity().getExternalFilesDir("checklists") + "/" + CheckLists.getCheckListName());
                }
            }
        }.execute();
    }

    private void createCheckList() {
        DialogEditTextListener.dialogEditText(null, getString(R.string.check_list_create_question),
                (dialogInterface, i) -> {
                }, text -> {
                    if (text.isEmpty()) {
                        Utils.showSnackbar(requireActivity().findViewById(android.R.id.content), getString(R.string.check_list_name_empty_message));
                        return;
                    }
                    if (Utils.exist(new File(requireActivity().getExternalFilesDir("checklists"), text).getAbsolutePath())) {
                        new MaterialAlertDialogBuilder(requireActivity())
                                .setMessage(getString(R.string.check_list_exist_warning))
                                .setNegativeButton(getString(R.string.change_name), (dialogInterface, i) -> createCheckList())
                                .setPositiveButton(getString(R.string.replace), (dialogInterface, i) -> {
                                    mTitle.setText(text);
                                    mSave.setVisibility(View.VISIBLE);
                                    CheckLists.setCheckListName(text);
                                    mRecyclerViewCheckList.setVisibility(View.VISIBLE);
                                }).show();
                        return;
                    }
                    mTitle.setText(text);
                    mSave.setVisibility(View.VISIBLE);
                    CheckLists.setCheckListName(text);
                    mRecyclerViewCheckList.setVisibility(View.VISIBLE);
                }, -1,requireActivity()).setOnDismissListener(dialogInterface -> {
        }).show();
    }

    private void create(String path) {
        Utils.saveString("appwidget" + mAppWidgetId, path, requireActivity());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(requireActivity());
        WidgetProvider.update(appWidgetManager, mAppWidgetId, requireActivity());
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        requireActivity().setResult(Activity.RESULT_OK, resultValue);
        requireActivity().finish();
    }

}