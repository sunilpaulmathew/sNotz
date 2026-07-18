package com.sunilpaulmathew.snotz.fragments;

import static com.sunilpaulmathew.snotz.utils.sNotzUtils.updateDataBase;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
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
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.activities.AboutActivity;
import com.sunilpaulmathew.snotz.activities.NoteActivity;
import com.sunilpaulmathew.snotz.activities.NotificationRequestActivity;
import com.sunilpaulmathew.snotz.activities.QRCodeScannerActivity;
import com.sunilpaulmathew.snotz.activities.ReadNoteActivity;
import com.sunilpaulmathew.snotz.activities.SettingsActivity;
import com.sunilpaulmathew.snotz.adapters.NotesAdapter;
import com.sunilpaulmathew.snotz.utils.QRCodeUtils;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.dialogs.BottomMenuDialog;
import com.sunilpaulmathew.snotz.utils.dialogs.DeleteNoteDialog;
import com.sunilpaulmathew.snotz.utils.dialogs.SaveAsTextDialog;
import com.sunilpaulmathew.snotz.utils.dialogs.SortOptionsDialog;
import com.sunilpaulmathew.snotz.utils.sNotzColor;
import com.sunilpaulmathew.snotz.utils.sNotzData;
import com.sunilpaulmathew.snotz.utils.sNotzReminders;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;
import com.sunilpaulmathew.snotz.utils.sNotzWidgets;
import com.sunilpaulmathew.snotz.utils.serializableItems.MenuEntry;
import com.sunilpaulmathew.snotz.utils.serializableItems.sNotzEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.CommonUtils.sExecutor;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 01, 2021
 */
public class sNotzFragment extends Fragment {

    private TextInputEditText mSearchWord;
    private ContentLoadingProgressBar mProgressBar;
    private MaterialButton mAddIcon;
    private NotesAdapter mNotesAdapter;
    private RecyclerView mRecyclerView;
    private boolean mExit, mPositionExpanded = false;
    private final Handler mHandler = new Handler();
    private List<sNotzEntry> mData = null;
    private static int mExtraNoteId, mSpanCount = 1;
    private static String mExternalNote = null, mPermissionRequest = null, mSearchText = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments == null) return;

        mExtraNoteId = arguments.getInt("noteId", Integer.MIN_VALUE);
        mExternalNote = arguments.getString("externalNote");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_main, container, false);

        MaterialTextView mAppTitle = mRootView.findViewById(R.id.app_title);
        MaterialButton mSearchButton = mRootView.findViewById(R.id.search_button);
        MaterialButton mQRCodeButton = mRootView.findViewById(R.id.qrcode_button);
        MaterialButton mInfoButton = mRootView.findViewById(R.id.info_button);
        MaterialButton mSortButton = mRootView.findViewById(R.id.sort_button);
        MaterialButton mSettingsButton = mRootView.findViewById(R.id.settings_button);
        mAddIcon = mRootView.findViewById(R.id.add_note_icon);
        mSearchWord = mRootView.findViewById(R.id.search_word);
        mProgressBar = mRootView.findViewById(R.id.progress);
        mRecyclerView = mRootView.findViewById(R.id.recycler_view);

        mAppTitle.setTextColor(sNotzColor.getAppAccentColor(requireActivity()));
        mSettingsButton.setIconTint(ColorStateList.valueOf(sNotzColor.getAppAccentColor(requireActivity())));
        mInfoButton.setIconTint(ColorStateList.valueOf(sNotzColor.getAppAccentColor(requireActivity())));
        mQRCodeButton.setIconTint(ColorStateList.valueOf(sNotzColor.getAppAccentColor(requireActivity())));
        mProgressBar.setBackgroundColor(sCommonUtils.getColor(R.color.color_black, requireActivity()));
        mProgressBar.setIndeterminateTintList(ColorStateList.valueOf(sNotzColor.getAppAccentColor(requireActivity())));
        mSearchButton.setIconTint(ColorStateList.valueOf(sNotzColor.getAppAccentColor(requireActivity())));
        mSearchWord.setTextColor(sNotzColor.getAppAccentColor(requireActivity()));
        mSearchWord.setHintTextColor(sNotzColor.getAppAccentColor(requireActivity()));
        mSortButton.setIconTint(ColorStateList.valueOf(sNotzColor.getAppAccentColor(requireActivity())));

        mSearchWord.setTextColor(Color.RED);

        GridLayoutManager mLayoutManager = new GridLayoutManager(requireContext(), Utils.getSpanCount(requireActivity())) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        mRecyclerView.setLayoutManager(mLayoutManager);

        mSpanCount = mLayoutManager.getSpanCount();

        loadUI(mProgressBar, mSearchText).execute();

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
                int position = viewHolder.getBindingAdapterPosition();
                new DeleteNoteDialog(mData.get(position).isChecklist() ? Objects.requireNonNull(sNotzWidgets.getWidgetText(
                        mData.get(position).getNote())) : mData.get(position).getNote(), requireActivity()) {

                    @Override
                    public void negativeButtonLister() {
                        mNotesAdapter.notifyItemInserted(position);
                        mNotesAdapter.notifyItemRangeChanged(position, mNotesAdapter.getItemCount());
                    }

                    @Override
                    public void positiveButtonLister() {
                        deleteNote(position).execute();
                    }
                };
            }

            @Override
            public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;

                    Paint mPaint = new Paint();
                    mPaint.setColor(sCommonUtils.getColor(R.color.color_red, viewHolder.itemView.getContext()));
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

        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        /*
         * Based on the following Stack Overflow discussion
         * https://stackoverflow.com/questions/36127734/detect-when-recyclerview-reaches-the-bottom-most-position-while-scrolling
         */
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                mAddIcon.setVisibility(newState == RecyclerView.SCROLL_STATE_IDLE ? View.VISIBLE : View.GONE);
            }
        });

        mAddIcon.setTextColor(sNotzColor.isRandomColorScheme(requireActivity()) ? sNotzColor.getMaterial3Colors(0, sCommonUtils.getColor(R.color.color_teal, requireActivity()), requireActivity()) : sNotzColor.getAccentColor(requireActivity()));
        mAddIcon.setIconTint(ColorStateList.valueOf(sNotzColor.isRandomColorScheme(requireActivity()) ? sNotzColor.getMaterial3Colors(0, sCommonUtils.getColor(R.color.color_teal, requireActivity()), requireActivity()) : sNotzColor.getAccentColor(requireActivity())));
        mAddIcon.setBackgroundColor(sNotzColor.isRandomColorScheme(requireActivity()) ? sNotzColor.getMaterial3Colors(1, sCommonUtils.getColor(R.color.color_white, requireActivity()), requireActivity()) : sNotzColor.getTextColor(requireActivity()));
        mAddIcon.setStrokeColor(ColorStateList.valueOf(sNotzColor.isRandomColorScheme(requireActivity()) ? sNotzColor.getMaterial3Colors(1, sCommonUtils.getColor(R.color.color_white, requireActivity()), requireActivity()) : sNotzColor.getTextColor(requireActivity())));

        mAddIcon.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), NoteActivity.class);
            addItem.launch(intent);
        });

        mSearchButton.setOnClickListener(v -> {
            if (mSearchWord.getVisibility() == View.GONE) {
                mSearchWord.setVisibility(View.VISIBLE);
                Utils.toggleKeyboard(mSearchWord, requireActivity());
            } else {
                if (mSearchText != null) {
                    mSearchText = null;
                    mSearchWord.setText(null);
                }
                mSearchWord.setVisibility(View.GONE);
            }
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
                loadUI(mProgressBar, s.toString().toLowerCase()).execute();
            }
        });

        mInfoButton.setOnClickListener(v -> {
            Intent aboutsNotz = new Intent(requireActivity(), AboutActivity.class);
            startActivity(aboutsNotz);
        });

        mQRCodeButton.setOnClickListener(v -> {
            List<MenuEntry> menu = new CopyOnWriteArrayList<>();
            menu.add(new MenuEntry(getString(R.string.qr_code_scan), R.drawable.ic_qr_code, 0));
            menu.add(new MenuEntry(getString(R.string.qr_code_read), R.drawable.ic_image, 1));
            new BottomMenuDialog(menu, v.getContext()) {
                @Override
                public void onMenuItemClicked(int menuID) {
                    switch (menuID) {
                        case 0:
                            if (Utils.isPermissionDenied(Manifest.permission.CAMERA, requireActivity())) {
                                mPermissionRequest = Manifest.permission.CAMERA;
                                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                            } else {
                                Intent scanner = new Intent(requireActivity(), QRCodeScannerActivity.class);
                                addItem.launch(scanner);
                            }
                            break;
                        case 1:
                            if (Build.VERSION.SDK_INT < 29 && Utils.isPermissionDenied(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, requireActivity())) {
                                mPermissionRequest = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            } else {
                                Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                try {
                                    pickImageForQRReader.launch(pickImage);
                                } catch (ActivityNotFoundException ignored) {}
                            }
                            break;
                    }
                }
            };
        });

        mSortButton.setOnClickListener(v -> new SortOptionsDialog(requireActivity()) {
            @Override
            public void onItemClicked() {
                loadUI(mProgressBar, mSearchText).execute();
            }
        });

        mSettingsButton.setOnClickListener(v -> {
            Intent settings = new Intent(requireActivity(), SettingsActivity.class);
            appSettingTasks.launch(settings);
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mSearchWord.getVisibility() == View.VISIBLE) {
                    if (mSearchText != null) {
                        mSearchText = null;
                        mSearchWord.setText(null);
                    }
                    mSearchWord.setVisibility(View.GONE);
                    return;
                }
                if (mExit) {
                    mExit = false;
                    requireActivity().finish();
                } else {
                    sCommonUtils.toast(getString(R.string.press_back_exit), requireActivity()).show();
                    mExit = true;
                    mHandler.postDelayed(() -> mExit = false, 2000);
                }
            }
        });

        return mRootView;
    }

    private sExecutor addNote(sNotzEntry entry, int position) {
        return new sExecutor() {
            private List<sNotzEntry> data;

            @Override
            public void onPreExecute() {
                mProgressBar.setVisibility(View.VISIBLE);
                data = sNotzData.getRawData(requireActivity());
            }

            @Override
            public void doInBackground() {
                data.add(entry);
                updateDataBase(data, requireActivity());
            }

            @Override
            public void onPostExecute() {
                if (!isAdded()) return;

                List<sNotzEntry> updatedList = new CopyOnWriteArrayList<>(mData);
                updatedList.add(position, entry);

                mData = updatedList;

                mNotesAdapter.updateData(mData);

                mProgressBar.setVisibility(View.GONE);
            }
        };
    }

    private sExecutor deleteNote(int position) {
        return new sExecutor() {
            private List<sNotzEntry> data;
            @Override
            public void onPreExecute() {
                mProgressBar.setVisibility(View.VISIBLE);
                data = sNotzData.getRawData(requireActivity());
            }

            @Override
            public void doInBackground() {
                for (sNotzEntry rawItems : data) {
                    if (rawItems.getNoteID() == mData.get(position).getNoteID()) {
                        data.remove(rawItems);
                        break;
                    }
                }
                updateDataBase(data, requireActivity());
            }

            @Override
            public void onPostExecute() {
                if (!isAdded()) return;

                List<sNotzEntry> updatedList = new CopyOnWriteArrayList<>(mData);
                updatedList.remove(position);

                mData = updatedList;

                mNotesAdapter.updateData(mData);
                if (mPositionExpanded) {
                    mNotesAdapter.reset();
                    mPositionExpanded = false;
                }
                mProgressBar.setVisibility(View.GONE);
            }
        };
    }

    private sExecutor hideNote(sNotzEntry entry, int position) {
        return new sExecutor() {
            private boolean invalidPosition = false;
            private List<sNotzEntry> data;
            @Override
            public void onPreExecute() {
                mProgressBar.setVisibility(View.VISIBLE);
                data = sNotzData.getRawData(requireActivity());
            }

            @Override
            public void doInBackground() {
                entry.isHidden(!entry.isHidden());

                int targetPosition = -1;

                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).getNoteID() == mData.get(position).getNoteID()) {
                        targetPosition = i;
                        break;
                    }
                }

                if (targetPosition != -1) {
                    data.set(targetPosition, entry);
                    updateDataBase(data, requireActivity());
                } else {
                    invalidPosition = true;
                }
            }

            @Override
            public void onPostExecute() {
                if (!isAdded()) return;

                if (!invalidPosition) {
                    List<sNotzEntry> updatedList = new CopyOnWriteArrayList<>(mData);
                    if (sCommonUtils.getBoolean("hidden_note", false, requireActivity())) {
                        updatedList.set(position, entry);
                    } else {
                        updatedList.remove(position);
                    }

                    mData = updatedList;

                    mNotesAdapter.updateData(mData);
                }

                mProgressBar.setVisibility(View.GONE);
            }
        };
    }

    private sExecutor loadUI(ContentLoadingProgressBar progressBar, String searchText) {
        return new sExecutor() {
            private final Activity activity = requireActivity();

            @Override
            public void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void doInBackground() {
                mData = sNotzData.getData(activity, searchText);
            }

            private Intent getIntent(sNotzEntry notesEntry) {
                Intent editNote = new Intent(activity, NoteActivity.class);
                editNote.putExtra(NoteActivity.NOTE_INTENT, notesEntry.getNote());
                editNote.putExtra(NoteActivity.NOTE_ID_INTENT, notesEntry.getNoteID());
                editNote.putExtra(NoteActivity.HIDDEN_INTENT, notesEntry.isHidden());
                editNote.putExtra(NoteActivity.COLOR_BG_INTENT, notesEntry.getColorBackground());
                editNote.putExtra(NoteActivity.COLOR_TXT_INTENT, notesEntry.getColorText());
                return editNote;
            }

            @Override
            public void onPostExecute() {
                if (!isAdded()) return;

                mNotesAdapter = new NotesAdapter(mData, mSpanCount, new NotesAdapter.ClickListener() {
                    @Override
                    public void onItemClick(sNotzEntry notesEntry, int position, boolean positionExpanded) {
                        mPositionExpanded = positionExpanded;
                        Intent editNote = getIntent(notesEntry);
                        addItem.launch(editNote);
                    }

                    @Override
                    public void onMenuClick(sNotzEntry notesEntry, int position, boolean positionExpanded) {
                        mPositionExpanded = positionExpanded;
                        List<MenuEntry> menu = new CopyOnWriteArrayList<>();
                        menu.add(new MenuEntry(getString(R.string.edit), R.drawable.ic_note, 0));
                        menu.add(new MenuEntry(getString(R.string.share), R.drawable.ic_share, 1));
                        menu.add(new MenuEntry(getString(R.string.duplicate), R.drawable.ic_duplicate, 2));
                        if (!notesEntry.isChecklist()) {
                            menu.add(new MenuEntry(getString(R.string.hidden_note), R.drawable.ic_eye, true, notesEntry.isHidden(), 3));
                        }
                        if (sNotzReminders.isReminderSet(notesEntry.getNoteID(), activity)) {
                            menu.add(new MenuEntry(getString(R.string.reminder_manage), R.drawable.ic_notification_on, 4));
                        } else {
                            menu.add(new MenuEntry(getString(R.string.reminder_set), R.drawable.ic_notification, 4));
                        }
                        menu.add(new MenuEntry(getString(R.string.qr_code_generate), R.drawable.ic_qr_code, 5));
                        if (!notesEntry.isChecklist()) {
                            menu.add(new MenuEntry(getString(R.string.reading_mode), R.drawable.ic_reading_mode, 6));
                        }
                        menu.add(new MenuEntry(getString(R.string.save_text), R.drawable.ic_save, 7));
                        menu.add(new MenuEntry(getString(R.string.delete), R.drawable.ic_delete, 8));

                        new BottomMenuDialog(menu,
                                R.mipmap.ic_launcher,
                                notesEntry.isChecklist() ? Objects.requireNonNull(sNotzWidgets.getWidgetText(
                                        notesEntry.getNote())) : notesEntry.getNote(),
                                activity) {
                            @Override
                            public void onMenuItemClicked(int menuID) {
                                switch (menuID) {
                                    case 0:
                                        Intent editNote = getIntent(notesEntry);
                                        addItem.launch(editNote);
                                        break;
                                    case 1:
                                        sNotzUtils.shareNote(notesEntry.getNote(), activity);
                                        break;
                                    case 2:
                                        addNote(notesEntry, position).execute();
                                        break;
                                    case 3:
                                        hideNote(notesEntry, position).execute();
                                        sCommonUtils.toast(getString(R.string.hidden_note_message), activity).show();
                                        break;
                                    case 4:
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && Utils.isPermissionDenied(Manifest.permission.POST_NOTIFICATIONS, activity)) {
                                            Intent intent = new Intent(activity, NotificationRequestActivity.class);
                                            startActivity(intent);
                                        } else {
                                            sNotzReminders.launchReminderMenu(notesEntry.getNote(), notesEntry.getNoteID(), activity);
                                        }
                                        break;
                                    case 5:
                                        new QRCodeUtils(notesEntry.getNote(), null, activity).generateQRCode().execute();
                                        break;
                                    case 6:
                                        Intent readOnlyMode = new Intent(activity, ReadNoteActivity.class);
                                        readOnlyMode.putExtra(ReadNoteActivity.NOTE_INTENT, notesEntry.getNote());
                                        startActivity(readOnlyMode);
                                        break;
                                    case 7:
                                        if (Build.VERSION.SDK_INT < 29 && Utils.isPermissionDenied(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, activity)) {
                                            Utils.requestPermission(new String[] {
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                            }, activity);
                                        } else {
                                            new SaveAsTextDialog(notesEntry.getNote(), activity);
                                        }
                                        break;
                                    case 8:
                                        deleteNote(position).execute();
                                        break;
                                }
                            }
                        };
                    }
                    @Override
                    public void scrollToPosition(int position) {
                        if (position == RecyclerView.NO_POSITION) return;
                        mRecyclerView.smoothScrollToPosition(position);
                    }
                });

                if (mRecyclerView != null) {
                    mRecyclerView.setAdapter(mNotesAdapter);
                }

                mSearchText = searchText;
                progressBar.setVisibility(View.GONE);

                handleIntentRouting(activity);
            }

            private void handleIntentRouting(Activity activity) {
                if (mExternalNote == null && mExtraNoteId == Integer.MIN_VALUE) return;

                if (mExternalNote != null) {
                    Intent createNote = new Intent(activity, NoteActivity.class);
                    createNote.putExtra(NoteActivity.NOTE_INTENT, mExternalNote);
                    createNote.putExtra(NoteActivity.NOTE_ID_INTENT, -1);
                    addItem.launch(createNote);
                } else {
                    for (sNotzEntry items : sNotzData.getRawData(activity)) {
                        if (items.getNoteID() == mExtraNoteId) {
                            Intent intent = buildNoteIntent(activity, items);
                            addItem.launch(intent);
                            break;
                        }
                    }
                }

                mExternalNote = null;
                mExtraNoteId = Integer.MIN_VALUE;
            }

            @NonNull
            private Intent buildNoteIntent(Activity activity, sNotzEntry items) {
                Intent intent = new Intent(activity, NoteActivity.class);
                intent.putExtra(NoteActivity.NOTE_INTENT, items.getNote());
                intent.putExtra(NoteActivity.NOTE_ID_INTENT, items.getNoteID());
                intent.putExtra(NoteActivity.HIDDEN_INTENT, items.isHidden());
                intent.putExtra(NoteActivity.COLOR_BG_INTENT, items.getColorBackground());
                intent.putExtra(NoteActivity.COLOR_TXT_INTENT, items.getColorText());
                return intent;
            }
        };
    }

    private final ActivityResultLauncher<Intent> addItem = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    new sExecutor() {
                        private boolean hidden, invalidPosition = false, isUpdate, toDelete;
                        private final Activity activity = requireActivity();
                        private int colorBackground, colorText, id;
                        private List<sNotzEntry> data;
                        private sNotzEntry item;
                        private String note;
                        private int actionType = 0;

                        @Override
                        public void onPreExecute() {
                            mProgressBar.setVisibility(View.VISIBLE);
                            Intent intent = result.getData();

                            id = intent.getIntExtra("id", Integer.MIN_VALUE);
                            toDelete = intent.getBooleanExtra("toDelete", false);
                            isUpdate = intent.getBooleanExtra("isUpdate", false);
                            hidden = intent.getBooleanExtra("hidden", false);
                            colorBackground = intent.getIntExtra("colorBackground", Integer.MIN_VALUE);
                            colorText = intent.getIntExtra("colorText", Integer.MIN_VALUE);
                            note = intent.getStringExtra("note");

                            data = sNotzData.getRawData(activity);
                        }

                        @Override
                        public void doInBackground() {
                            int targetPositionInDb = RecyclerView.NO_POSITION;

                            if (!isUpdate && !toDelete) {
                                targetPositionInDb = 0;
                            } else {
                                for (int i = 0; i < data.size(); i++) {
                                    if (data.get(i).getNoteID() == id) {
                                        targetPositionInDb = i;
                                        break;
                                    }
                                }
                            }

                            if (targetPositionInDb == RecyclerView.NO_POSITION) {
                                invalidPosition = true;
                                return;
                            }

                            if (toDelete) {
                                actionType = 0;
                                data.remove(targetPositionInDb);
                            } else if (isUpdate) {
                                item = new sNotzEntry(note, System.currentTimeMillis(), hidden, colorBackground, colorText, id);
                                data.set(targetPositionInDb, item);

                                if (item.isHidden() && !sCommonUtils.getBoolean("hidden_note", false, activity)) {
                                    actionType = 3;
                                } else {
                                    actionType = 1;
                                }
                            } else {
                                actionType = 2;
                                item = new sNotzEntry(note, System.currentTimeMillis(), hidden, colorBackground, colorText, id);
                                data.add(item);
                            }

                            updateDataBase(data, activity);
                        }

                        @Override
                        public void onPostExecute() {
                            if (!isAdded()) return;

                            if (!invalidPosition) {
                                int targetUiPosition = RecyclerView.NO_POSITION;
                                for (int i = 0; i < mData.size(); i++) {
                                    if (mData.get(i).getNoteID() == id) {
                                        targetUiPosition = i;
                                        break;
                                    }
                                }

                                List<sNotzEntry> updatedList = new CopyOnWriteArrayList<>(mData);

                                switch (actionType) {
                                    case 2:
                                        if (!item.isHidden() || sCommonUtils.getBoolean("hidden_note", false, activity)) {
                                            updatedList.add(0, item);
                                            mData = updatedList;
                                            mNotesAdapter.updateData(mData);
                                            mRecyclerView.smoothScrollToPosition(0);
                                        }
                                        break;

                                    case 1:
                                        if (targetUiPosition != RecyclerView.NO_POSITION) {
                                            updatedList.set(targetUiPosition, item);
                                            mData = updatedList;
                                            mNotesAdapter.updateData(mData);
                                        }
                                        break;

                                    default:
                                        if (targetUiPosition != RecyclerView.NO_POSITION) {
                                            updatedList.remove(targetUiPosition);
                                            mData = updatedList;
                                            mNotesAdapter.updateData(mData);

                                            if (mPositionExpanded) {
                                                mNotesAdapter.reset();
                                                mPositionExpanded = false;
                                            }
                                        }
                                        break;
                                }
                            }

                            mProgressBar.setVisibility(View.GONE);
                        }
                    }.execute();
                }
            }
    );

    private final ActivityResultLauncher<Intent> pickImageForQRReader = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    if (new QRCodeUtils(null, data.getData(), requireActivity()).readQRCode() != null) {
                        Intent scanner = new Intent(requireActivity(), NoteActivity.class);
                        scanner.putExtra(NoteActivity.NOTE_INTENT, new QRCodeUtils(null, data.getData(), requireActivity()).readQRCode());
                        scanner.putExtra(NoteActivity.NOTE_ID_INTENT, -1);
                        addItem.launch(scanner);
                    } else {
                        sCommonUtils.toast(getString(R.string.qr_code_error_message), requireActivity()).show();
                    }
                }
            }
    );

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    if (Manifest.permission.CAMERA.equals(mPermissionRequest)) {
                        Intent scanner = new Intent(requireActivity(), QRCodeScannerActivity.class);
                        addItem.launch(scanner);
                    } else {
                        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        try {
                            pickImageForQRReader.launch(pickImage);
                        } catch (ActivityNotFoundException ignored) {}
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> appSettingTasks = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent intent = result.getData();
                    boolean reload = intent.getBooleanExtra("reload", false);
                    boolean recreate = intent.getBooleanExtra("recreate", false);
                    String dataBaseString = intent.getStringExtra("dataBase");
                    String checkListString = intent.getStringExtra("checkList");
                    if (checkListString != null) {
                        Intent createNote = new Intent(requireActivity(), NoteActivity.class);
                        createNote.putExtra(NoteActivity.NOTE_INTENT, checkListString);
                        createNote.putExtra(NoteActivity.NOTE_ID_INTENT, -1);
                        addItem.launch(createNote);
                    } else if (dataBaseString != null) {
                        new sExecutor() {
                            private final Activity activity = requireActivity();
                            private List<sNotzEntry> originalData;
                            private List<sNotzEntry> backupItems;
                            private int startingId;

                            @Override
                            public void onPreExecute() {
                                mProgressBar.setVisibility(View.VISIBLE);
                                originalData = sNotzData.getRawData(requireActivity());
                                backupItems = sNotzUtils.getNotesFromBackup(dataBaseString, activity);
                                startingId = sNotzUtils.generateNoteID(activity);
                            }

                            @Override
                            public void doInBackground() {

                                List<sNotzEntry> updatedList = new ArrayList<>(originalData);

                                int currentId = startingId;
                                for (sNotzEntry item : backupItems) {
                                    updatedList.add(new sNotzEntry(
                                            item.getNote(),
                                            item.getTimeStamp(),
                                            item.isHidden(),
                                            item.getColorBackground(),
                                            item.getColorText(),
                                            currentId
                                    ));
                                    currentId++;
                                }

                                updateDataBase(updatedList, activity);
                            }

                            @Override
                            public void onPostExecute() {
                                if (!isAdded()) return;

                                loadUI(mProgressBar, mSearchText).execute();
                            }
                        }.execute();
                    } else if (recreate) {
                        requireActivity().recreate();
                    } else if (reload) {
                        loadUI(mProgressBar, mSearchText).execute();
                    }
                }
            }
    );

}