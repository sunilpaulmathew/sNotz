package com.sunilpaulmathew.snotz.fragments;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.activities.CreateWidgetActivity;
import com.sunilpaulmathew.snotz.adapters.WidgetAdapter;
import com.sunilpaulmathew.snotz.providers.WidgetProvider;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzColor;
import com.sunilpaulmathew.snotz.utils.sNotzData;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;
import com.sunilpaulmathew.snotz.utils.serializableItems.sNotzItems;

import java.util.ArrayList;
import java.util.List;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.CommonUtils.sExecutor;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 19, 2021
 * Based mainly on the following Stack Overflow discussions
 * Reference 1: https://stackoverflow.com/questions/34588501/how-to-create-an-android-widget-with-options
 * Reference 2: https://stackoverflow.com/questions/6264809/how-to-build-a-simple-android-widget
 */
public class WidgetFragment extends Fragment {

    private static int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private RecyclerView mRecyclerView;
    private WidgetAdapter mRecycleViewAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_widget, container, false);

        AppCompatImageButton mAddIcon = mRootView.findViewById(R.id.add_note_icon);
        ContentLoadingProgressBar mProgress = mRootView.findViewById(R.id.progress);
        MaterialButton mSortButton = mRootView.findViewById(R.id.sort_button);
        MaterialCardView mAddNewCard = mRootView.findViewById(R.id.add_new_card);
        mRecyclerView = mRootView.findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        mRecycleViewAdapter = new WidgetAdapter(getData(requireActivity()));
        mRecyclerView.setAdapter(mRecycleViewAdapter);

        mAddIcon.setColorFilter(sNotzColor.isRandomColorScheme(requireActivity()) ? sNotzColor.getMaterial3Colors(0, sCommonUtils.getColor(R.color.color_teal, requireActivity()), requireActivity()) : sNotzColor.getAccentColor(requireActivity()));
        mSortButton.setIconTint(ColorStateList.valueOf(sNotzColor.getAppAccentColor(requireActivity())));
        mAddNewCard.setCardBackgroundColor(sNotzColor.isRandomColorScheme(requireActivity()) ? sNotzColor.getMaterial3Colors(1, sCommonUtils.getColor(R.color.color_white, requireActivity()), requireActivity()) : sNotzColor.getTextColor(requireActivity()));
        mAddNewCard.setStrokeColor(sNotzColor.isRandomColorScheme(requireActivity()) ? sNotzColor.getMaterial3Colors(1, sCommonUtils.getColor(R.color.color_white, requireActivity()), requireActivity()) : sNotzColor.getTextColor(requireActivity()));
        mProgress.setBackgroundColor(sCommonUtils.getColor(R.color.color_black, requireActivity()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mProgress.setIndeterminateTintList(ColorStateList.valueOf(sNotzColor.getAppAccentColor(requireActivity())));
        }

        mRecycleViewAdapter.setOnItemClickListener((position, v) -> {
            sCommonUtils.saveString("appwidget" + mAppWidgetId, String.valueOf(getData(requireActivity()).get(position).getNoteID()), requireActivity());
            createWidget(false, mAppWidgetId);
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
            Intent createNewIntent = new Intent(requireActivity(), CreateWidgetActivity.class);
            createWidget.launch(createNewIntent);
        });

        mSortButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireActivity(), mSortButton);
            Menu menu = popupMenu.getMenu();
            SubMenu show = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.show));
            show.add(1, 5, Menu.NONE, getString(R.string.show_all)).setCheckable(true)
                    .setChecked(sCommonUtils.getInt("show_all", 0, requireActivity()) == 0);
            show.add(1, 6, Menu.NONE, getString(R.string.show_checklists)).setCheckable(true)
                    .setChecked(sCommonUtils.getInt("show_all", 0, requireActivity()) == 1);
            show.add(1, 7, Menu.NONE, getString(R.string.show_notes)).setCheckable(true)
                    .setChecked(sCommonUtils.getInt("show_all", 0, requireActivity()) == 2);
            show.setGroupCheckable(1, true, true);
            SubMenu sort = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.sort_by));
            sort.add(0, 1, Menu.NONE, getString(R.string.sort_by_date)).setCheckable(true)
                    .setChecked(sCommonUtils.getInt("sort_notes", 2, requireActivity()) == 2);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                sort.add(0, 2, Menu.NONE, getString(R.string.note_color_background)).setCheckable(true)
                        .setChecked(sCommonUtils.getInt("sort_notes", 2, requireActivity()) == 1);
            }
            sort.add(0, 3, Menu.NONE, getString(R.string.az_order)).setCheckable(true)
                    .setChecked(sCommonUtils.getInt("sort_notes", 2, requireActivity()) == 0);
            sort.setGroupCheckable(0, true, true);
            menu.add(Menu.NONE, 4, Menu.NONE, getString(R.string.reverse_order)).setCheckable(true)
                    .setChecked(sCommonUtils.getBoolean("reverse_order", false, requireActivity()));
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 0:
                        break;
                    case 1:
                        if (sCommonUtils.getInt("sort_notes", 2, requireActivity()) != 2) {
                            sCommonUtils.saveInt("sort_notes", 2, requireActivity());
                            loadUI(mProgress).execute();
                        }
                        break;
                    case 2:
                        if (sCommonUtils.getInt("sort_notes", 2, requireActivity()) != 1) {
                            sCommonUtils.saveInt("sort_notes", 1, requireActivity());
                            loadUI(mProgress).execute();
                        }
                        break;
                    case 3:
                        if (sCommonUtils.getInt("sort_notes", 2, requireActivity()) != 0) {
                            sCommonUtils.saveInt("sort_notes", 0, requireActivity());
                            loadUI(mProgress).execute();
                        }
                        break;
                    case 4:
                        sCommonUtils.saveBoolean("reverse_order", !sCommonUtils.getBoolean("reverse_order", false, requireActivity()), requireActivity());
                        loadUI(mProgress).execute();
                        break;
                    case 5:
                        if (sCommonUtils.getInt("show_all", 0, requireActivity()) != 0) {
                            sCommonUtils.saveInt("show_all", 0, requireActivity());
                            loadUI(mProgress).execute();
                        }
                        break;
                    case 6:
                        if (sCommonUtils.getInt("show_all", 0, requireActivity()) != 1) {
                            sCommonUtils.saveInt("show_all", 1, requireActivity());
                            loadUI(mProgress).execute();
                        }
                        break;
                    case 7:
                        if (sCommonUtils.getInt("show_all", 0, requireActivity()) != 2) {
                            sCommonUtils.saveInt("show_all", 2, requireActivity());
                            loadUI(mProgress).execute();
                        }
                        break;
                }
                return false;
            });
            popupMenu.show();
        });

        return mRootView;
    }

    private sExecutor loadUI(ContentLoadingProgressBar progressBar) {
        return new sExecutor() {
            @Override
            public void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void doInBackground() {
                mRecycleViewAdapter = new WidgetAdapter(getData(requireActivity()));
            }

            @Override
            public void onPostExecute() {
                try {
                    mRecyclerView.setAdapter(mRecycleViewAdapter);
                } catch (NullPointerException ignored) {}
                progressBar.setVisibility(View.GONE);
            }
        };
    }

    private static List<sNotzItems> getData(Activity activity) {
        List<sNotzItems> mData = new ArrayList<>();
        for (sNotzItems item : sNotzData.getData(activity, null)) {
            if (!item.isHidden()) {
                mData.add(item);
            }
        }
        return mData;
    }

    private void createWidget(boolean createNew, int mAppWidgetId) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(requireActivity());
        WidgetProvider.update(appWidgetManager, mAppWidgetId, requireActivity());
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        requireActivity().setResult(Activity.RESULT_OK, resultValue);
        if (!createNew) {
            requireActivity().finish();
        }
    }

    ActivityResultLauncher<Intent> createWidget = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    new sExecutor() {
                        private int colorBackground, colorText, id;
                        private List<sNotzItems> data;
                        private String note;
                        @Override
                        public void onPreExecute() {
                            Intent intent = result.getData();
                            id = intent.getIntExtra("id", Integer.MIN_VALUE);
                            colorBackground = intent.getIntExtra("colorBackground", Integer.MIN_VALUE);
                            colorText = intent.getIntExtra("colorText", Integer.MIN_VALUE);
                            note = intent.getStringExtra("note");
                            data = sNotzData.getRawData(requireActivity());
                        }

                        @Override
                        public void doInBackground() {
                            data.add(new sNotzItems(note, System.currentTimeMillis(), false, colorBackground, colorText, id));
                            sNotzUtils.updateDataBase(data, requireActivity());
                            if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                                sCommonUtils.saveString("appwidget" + mAppWidgetId, String.valueOf(id), requireActivity());
                                createWidget(true, mAppWidgetId);
                                Utils.updateWidgets(requireActivity());
                            }
                        }

                        @Override
                        public void onPostExecute() {
                            requireActivity().finish();
                        }
                    }.execute();
                }
            }
    );

}