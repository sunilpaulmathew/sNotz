package com.sunilpaulmathew.snotz.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.activities.AboutActivity;
import com.sunilpaulmathew.snotz.activities.CheckListActivity;
import com.sunilpaulmathew.snotz.activities.CheckListsActivity;
import com.sunilpaulmathew.snotz.activities.CreateNoteActivity;
import com.sunilpaulmathew.snotz.activities.QRCodeScannerActivity;
import com.sunilpaulmathew.snotz.activities.SettingsActivity;
import com.sunilpaulmathew.snotz.adapters.NotesAdapter;
import com.sunilpaulmathew.snotz.utils.CheckLists;
import com.sunilpaulmathew.snotz.utils.Common;
import com.sunilpaulmathew.snotz.utils.QRCodeUtils;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzColor;
import com.sunilpaulmathew.snotz.utils.sNotzData;
import com.sunilpaulmathew.snotz.utils.sNotzItems;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;
import com.sunilpaulmathew.snotz.utils.sNotzWidgets;

import java.io.File;

import in.sunilpaulmathew.sCommon.Utils.sExecutor;
import in.sunilpaulmathew.sCommon.Utils.sPermissionUtils;
import in.sunilpaulmathew.sCommon.Utils.sUtils;

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
    private static int mExtraNoteId = sNotzWidgets.getInvalidNoteId();
    private static String mExternalNote = null, mExtraCheckListPath = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments == null) return;

        mExtraNoteId = arguments.getInt(sNotzWidgets.getNoteID(), sNotzWidgets.getInvalidNoteId());
        mExtraCheckListPath = arguments.getString(sNotzWidgets.getChecklistPath());
        mExternalNote = arguments.getString(sNotzUtils.getExternalNote());
    }

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
        ProgressBar mProgressBar = mRootView.findViewById(R.id.progress);

        mSearchWord.setTextColor(Color.RED);

        Common.initializeRecyclerView(R.id.recycler_view, mRootView);

        GridLayoutManager mLayoutManager = new GridLayoutManager(requireActivity(), Utils.getSpanCount(requireActivity()));

        Common.getRecyclerView().setLayoutManager(mLayoutManager);

        Common.setSpanCount(mLayoutManager.getSpanCount());

        loadUI(mProgressBar, requireActivity()).execute();

        /*
         * Based on the following Stack Overflow discussion
         * https://stackoverflow.com/questions/55949538/update-onmove-changes-in-recycler-view-data-to-room-database
         */
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                String[] sNotzContents = sNotzData.getData(requireActivity()).get(position).getNote().split("\\s+");
                new MaterialAlertDialogBuilder(requireActivity())
                        .setMessage(getString(R.string.delete_sure_question, sNotzContents.length <= 2 ?
                                sNotzData.getData(requireActivity()).get(position).getNote() : sNotzContents[0] + " " + sNotzContents[1] + " " + sNotzContents[2] + "..."))
                        .setCancelable(false)
                        .setNegativeButton(R.string.cancel, (dialog, which) -> loadUI(mProgressBar, requireActivity()).execute())
                        .setPositiveButton(R.string.delete, (dialog, which) -> sNotzUtils.deleteNote(sNotzData.getData(requireActivity()).get(position).getNoteID(),
                                mProgressBar, requireActivity()).execute()).show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;

                    Paint mPaint = new Paint();
                    mPaint.setColor(sUtils.getColor(R.color.color_red, viewHolder.itemView.getContext()));
                    if (dX > 0) {
                        canvas.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                                (float) itemView.getBottom(), mPaint);
                    } else {
                        canvas.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom(), mPaint);
                    }
                    super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        });

        itemTouchHelper.attachToRecyclerView(Common.getRecyclerView());

        /*
         * Based on the following Stack Overflow discussion
         * https://stackoverflow.com/questions/36127734/detect-when-recyclerview-reaches-the-bottom-most-position-while-scrolling
         */
        Common.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                mAddNoteCard.setVisibility(newState == RecyclerView.SCROLL_STATE_IDLE ? View.VISIBLE : View.GONE);
            }
        });

        mAddIcon.setColorFilter(sNotzColor.getAccentColor(requireActivity()));
        mAddNoteCard.setCardBackgroundColor(sNotzColor.getTextColor(requireActivity()));

        mAddNoteCard.setOnClickListener(v -> {
            if (Common.isWorking()) {
                return;
            }
            Common.setExternalNote(null);
            Common.setNote(null);
            Common.setImageString(null);
            Common.isHiddenNote(false);
            Common.setID(-1);
            Common.setBackgroundColor(123456789);
            Common.setTextColor(123456789);
            Intent createNote = new Intent(requireActivity(), CreateNoteActivity.class);
            startActivity(createNote);
        });
        mSearchButton.setOnClickListener(v -> {
            if (Common.isWorking()) {
                return;
            }
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
                loadUI(mProgressBar, requireActivity()).execute();
            }
        });

        mSortButton.setOnClickListener(v -> {
            if (Common.isWorking()) {
                return;
            }
            PopupMenu popupMenu = new PopupMenu(requireActivity(), mSortButton);
            Menu menu = popupMenu.getMenu();
            SubMenu sort = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.sort_by));
            sort.add(0, 1, Menu.NONE, getString(R.string.sort_by_date)).setCheckable(true)
                    .setChecked(sUtils.getInt("sort_notes", 2, requireActivity()) == 2);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                sort.add(0, 2, Menu.NONE, getString(R.string.note_color_background)).setCheckable(true)
                        .setChecked(sUtils.getInt("sort_notes", 2, requireActivity()) == 1);
            }
            sort.add(0, 3, Menu.NONE, getString(R.string.az_order)).setCheckable(true)
                    .setChecked(sUtils.getInt("sort_notes", 2, requireActivity()) == 0);
            sort.setGroupCheckable(0, true, true);
            menu.add(Menu.NONE, 4, Menu.NONE, getString(R.string.reverse_order)).setCheckable(true)
                    .setChecked(sUtils.getBoolean("reverse_order", false, requireActivity()));
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 0:
                        break;
                    case 1:
                        if (sUtils.getInt("sort_notes", 2, requireActivity()) != 2) {
                            sUtils.saveInt("sort_notes", 2, requireActivity());
                            loadUI(mProgressBar, requireActivity()).execute();
                        }
                        break;
                    case 2:
                        if (sUtils.getInt("sort_notes", 2, requireActivity()) != 1) {
                            sUtils.saveInt("sort_notes", 1, requireActivity());
                            loadUI(mProgressBar, requireActivity()).execute();
                        }
                        break;
                    case 3:
                        if (sUtils.getInt("sort_notes", 2, requireActivity()) != 0) {
                            sUtils.saveInt("sort_notes", 0, requireActivity());
                            loadUI(mProgressBar, requireActivity()).execute();
                        }
                        break;
                    case 4:
                        sUtils.saveBoolean("reverse_order", !sUtils.getBoolean("reverse_order", false, requireActivity()), requireActivity());
                        loadUI(mProgressBar, requireActivity()).execute();
                        break;
                }
                return false;
            });
            popupMenu.show();
        });

        mMenu.setOnClickListener(v -> {
            if (Common.isWorking()) {
                return;
            }
            PopupMenu popupMenu = new PopupMenu(requireActivity(), mMenu);
            Menu menu = popupMenu.getMenu();
            menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.settings));
            menu.add(Menu.NONE, 2, Menu.NONE, getString(R.string.check_lists));
            SubMenu qrCode = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.qr_code));
            qrCode.add(Menu.NONE, 3, Menu.NONE, getString(R.string.qr_code_scan));
            qrCode.add(Menu.NONE, 4, Menu.NONE, getString(R.string.qr_code_read));
            menu.add(Menu.NONE, 5, Menu.NONE, getString(R.string.about));
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 0:
                        break;
                    case 1:
                        Intent settings = new Intent(requireActivity(), SettingsActivity.class);
                        startActivity(settings);
                        break;
                    case 2:
                        Intent checkLists = new Intent(requireActivity(), CheckListsActivity.class);
                        startActivity(checkLists);
                        break;
                    case 3:
                        if (sPermissionUtils.isPermissionDenied(Manifest.permission.CAMERA, requireActivity())) {
                            sPermissionUtils.requestPermission(new String[] {
                                    Manifest.permission.CAMERA
                            }, requireActivity());
                        } else {
                            Intent scanner = new Intent(requireActivity(), QRCodeScannerActivity.class);
                            startActivity(scanner);
                        }
                        break;
                    case 4:
                        if (Build.VERSION.SDK_INT < 29 && sPermissionUtils.isPermissionDenied(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, requireActivity())) {
                            sPermissionUtils.requestPermission(new String[] {
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            },requireActivity());
                        } else {
                            Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            try {
                                startActivityForResult(pickImage, 0);
                            } catch (ActivityNotFoundException ignored) {}
                        }
                        break;
                    case 5:
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
                    sUtils.snackBar(mAppTitle, getString(R.string.press_back_exit)).show();
                    mExit = true;
                    mHandler.postDelayed(() -> mExit = false, 2000);
                }
            }
        });

        return mRootView;
    }

    private static sExecutor loadUI(ProgressBar progressBar, Activity activity) {
        return new sExecutor() {
            private NotesAdapter mNotesAdapter;
            @Override
            public void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void doInBackground() {
                setNoteFromIntent();
            }

            @Override
            public void onPostExecute() {
                try {
                    Common.getRecyclerView().setAdapter(mNotesAdapter);
                } catch (NullPointerException ignored) {}
                progressBar.setVisibility(View.GONE);
            }

            private void setNoteFromIntent() {
                mNotesAdapter = new NotesAdapter(sNotzData.getData(activity));
                if (Common.isWorking() || mExternalNote == null && mExtraCheckListPath == null && mExtraNoteId == sNotzWidgets.getInvalidNoteId()) return;
                if (mExternalNote != null) {
                    Common.setExternalNote(mExternalNote);
                    mExternalNote = null;
                    Intent createNote = new Intent(activity, CreateNoteActivity.class);
                    activity.startActivity(createNote);
                } else if (mExtraCheckListPath != null && sUtils.exist(new File(mExtraCheckListPath))) {
                    CheckLists.setCheckListName(new File(mExtraCheckListPath).getName());
                    // It should be set null right after finishing the job as we are calling this method for other tasks as well
                    mExtraCheckListPath = null;
                    Intent checkList = new Intent(activity, CheckListActivity.class);
                    activity.startActivity(checkList);
                } else if (mExtraNoteId != sNotzWidgets.getInvalidNoteId()) {
                    sNotzItems extraItems = null;
                    for (sNotzItems items : sNotzData.getRawData(activity)) {
                        if (items.getNoteID() == mExtraNoteId) {
                            extraItems = items;
                            break;
                        }
                    }

                    if (extraItems == null) return;

                    Common.setNote(extraItems.getNote());
                    Common.setID(extraItems.getNoteID());
                    Common.setBackgroundColor(extraItems.getColorBackground());
                    Common.setTextColor(extraItems.getColorText());
                    if (extraItems.getImageString() != null) {
                        Common.setImageString(extraItems.getImageString());
                    }
                    Common.isHiddenNote(extraItems.isHidden());
                    // This one should also handled right after finishing the job
                    mExtraNoteId = sNotzWidgets.getInvalidNoteId();
                    Intent editNote = new Intent(activity, CreateNoteActivity.class);
                    activity.startActivity(editNote);
                }
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            if (data.getData() != null) {
                if (new QRCodeUtils(null, data.getData(), requireActivity()).readQRCode() != null) {
                    if (CheckLists.isValidCheckList(new QRCodeUtils(null, data.getData(), requireActivity()).readQRCode())) {
                        CheckLists.importCheckList(new QRCodeUtils(null, data.getData(), requireActivity()).readQRCode(), false, requireActivity());
                    } else {
                        Common.setExternalNote(new QRCodeUtils(null, data.getData(), requireActivity()).readQRCode());
                        Intent scanner = new Intent(requireActivity(), CreateNoteActivity.class);
                        startActivity(scanner);
                    }
                } else {
                    sUtils.snackBar(mAppTitle, getString(R.string.qr_code_error_message)).show();
                }
            }
        }
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