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
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.activities.AboutActivity;
import com.sunilpaulmathew.snotz.activities.NoteActivity;
import com.sunilpaulmathew.snotz.activities.QRCodeScannerActivity;
import com.sunilpaulmathew.snotz.activities.SettingsActivity;
import com.sunilpaulmathew.snotz.adapters.NotesAdapter;
import com.sunilpaulmathew.snotz.utils.CheckLists;
import com.sunilpaulmathew.snotz.utils.QRCodeUtils;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.dialogs.DeleteNoteDialog;
import com.sunilpaulmathew.snotz.utils.sNotzColor;
import com.sunilpaulmathew.snotz.utils.sNotzData;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;
import com.sunilpaulmathew.snotz.utils.sNotzWidgets;
import com.sunilpaulmathew.snotz.utils.serializableItems.sNotzItems;

import java.util.List;
import java.util.Objects;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.CommonUtils.sExecutor;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 01, 2021
 */
public class sNotzFragment extends Fragment {

    private TextInputEditText mSearchWord;
    private ContentLoadingProgressBar mProgressBar;
    private MaterialCardView mAddNoteCard;
    private NotesAdapter mNotesAdapter;
    private RecyclerView mRecyclerView;
    private boolean mExit;
    private final Handler mHandler = new Handler();
    private List<sNotzItems> mData = null;
    private static int mExtraNoteId, mSpanCount = 1;
    private static String mExternalNote = null, mSearchText = null;

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
        AppCompatImageButton mAddIcon = mRootView.findViewById(R.id.add_note_icon);
        mAddNoteCard = mRootView.findViewById(R.id.add_note_card);
        mSearchWord = mRootView.findViewById(R.id.search_word);
        mProgressBar = mRootView.findViewById(R.id.progress);
        mRecyclerView = mRootView.findViewById(R.id.recycler_view);

        // This is temporary and will be removed in future
        if (!CheckLists.getOldChecklists(requireActivity()).isEmpty() && !sCommonUtils.getBoolean("restoreMessageShown", false, requireActivity())) {
            new MaterialAlertDialogBuilder(requireActivity())
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(R.string.app_name)
                    .setCancelable(false)
                    .setMessage("Checklists from the previous version of sNotz is found in \"" + requireActivity().getExternalFilesDir("checklists") + "\". If they are valuable, please restore them individually by\n" +
                            "\n1. Go to \"" + requireActivity().getExternalFilesDir("checklists") + "\"" +
                            "\n2. Add a \".txt\" extension to the checklist files to be restored" +
                            "\n3. Simply click on them and choose to open with sNotz")
                    .setPositiveButton(R.string.ok, (dialog, which) -> sCommonUtils.saveBoolean("restoreMessageShown", true, requireActivity())).show();
        }

        mAppTitle.setTextColor(sNotzColor.getAppAccentColor(requireActivity()));
        mSettingsButton.setIconTint(ColorStateList.valueOf(sNotzColor.getAppAccentColor(requireActivity())));
        mInfoButton.setIconTint(ColorStateList.valueOf(sNotzColor.getAppAccentColor(requireActivity())));
        mQRCodeButton.setIconTint(ColorStateList.valueOf(sNotzColor.getAppAccentColor(requireActivity())));
        mProgressBar.setBackgroundColor(sCommonUtils.getColor(R.color.color_black, requireActivity()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mProgressBar.setIndeterminateTintList(ColorStateList.valueOf(sNotzColor.getAppAccentColor(requireActivity())));
        }
        mSearchButton.setIconTint(ColorStateList.valueOf(sNotzColor.getAppAccentColor(requireActivity())));
        mSearchWord.setTextColor(sNotzColor.getAppAccentColor(requireActivity()));
        mSearchWord.setHintTextColor(sNotzColor.getAppAccentColor(requireActivity()));
        mSortButton.setIconTint(ColorStateList.valueOf(sNotzColor.getAppAccentColor(requireActivity())));

        mSearchWord.setTextColor(Color.RED);

        GridLayoutManager mLayoutManager = new GridLayoutManager(requireActivity(), Utils.getSpanCount(requireActivity()));

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
                int position = viewHolder.getAdapterPosition();
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

                mAddNoteCard.setVisibility(newState == RecyclerView.SCROLL_STATE_IDLE ? View.VISIBLE : View.GONE);
            }
        });

        mAddIcon.setColorFilter(sNotzColor.isRandomColorScheme(requireActivity()) ? sNotzColor.getMaterial3Colors(0, sCommonUtils.getColor(R.color.color_teal, requireActivity()), requireActivity()) : sNotzColor.getAccentColor(requireActivity()));
        mAddNoteCard.setCardBackgroundColor(sNotzColor.isRandomColorScheme(requireActivity()) ? sNotzColor.getMaterial3Colors(1, sCommonUtils.getColor(R.color.color_white, requireActivity()), requireActivity()) : sNotzColor.getTextColor(requireActivity()));
        mAddNoteCard.setStrokeColor(sNotzColor.isRandomColorScheme(requireActivity()) ? sNotzColor.getMaterial3Colors(1, sCommonUtils.getColor(R.color.color_white, requireActivity()), requireActivity()) : sNotzColor.getTextColor(requireActivity()));

        mAddNoteCard.setOnClickListener(v -> {
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
            PopupMenu popupMenu = new PopupMenu(requireActivity(), mQRCodeButton);
            Menu menu = popupMenu.getMenu();
            menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.qr_code_scan));
            menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.qr_code_read));
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 0:
                        if (Utils.isPermissionDenied(Manifest.permission.CAMERA, requireActivity())) {
                            Utils.requestPermission(new String[] {
                                    Manifest.permission.CAMERA
                            }, requireActivity());
                        } else {
                            Intent scanner = new Intent(requireActivity(), QRCodeScannerActivity.class);
                            addItem.launch(scanner);
                        }
                        break;
                    case 1:
                        if (Build.VERSION.SDK_INT < 29 && Utils.isPermissionDenied(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, requireActivity())) {
                            Utils.requestPermission(new String[] {
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            },requireActivity());
                        } else {
                            Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            try {
                                pickImageForQRReader.launch(pickImage);
                            } catch (ActivityNotFoundException ignored) {}
                        }
                        break;
                }
                return false;
            });
            popupMenu.show();
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
                            loadUI(mProgressBar, mSearchText).execute();
                        }
                        break;
                    case 2:
                        if (sCommonUtils.getInt("sort_notes", 2, requireActivity()) != 1) {
                            sCommonUtils.saveInt("sort_notes", 1, requireActivity());
                            loadUI(mProgressBar, mSearchText).execute();
                        }
                        break;
                    case 3:
                        if (sCommonUtils.getInt("sort_notes", 2, requireActivity()) != 0) {
                            sCommonUtils.saveInt("sort_notes", 0, requireActivity());
                            loadUI(mProgressBar, mSearchText).execute();
                        }
                        break;
                    case 4:
                        sCommonUtils.saveBoolean("reverse_order", !sCommonUtils.getBoolean("reverse_order", false, requireActivity()), requireActivity());
                        loadUI(mProgressBar, mSearchText).execute();
                        break;
                    case 5:
                        if (sCommonUtils.getInt("show_all", 0, requireActivity()) != 0) {
                            sCommonUtils.saveInt("show_all", 0, requireActivity());
                            loadUI(mProgressBar, mSearchText).execute();
                        }
                        break;
                    case 6:
                        if (sCommonUtils.getInt("show_all", 0, requireActivity()) != 1) {
                            sCommonUtils.saveInt("show_all", 1, requireActivity());
                            loadUI(mProgressBar, mSearchText).execute();
                        }
                        break;
                    case 7:
                        if (sCommonUtils.getInt("show_all", 0, requireActivity()) != 2) {
                            sCommonUtils.saveInt("show_all", 2, requireActivity());
                            loadUI(mProgressBar, mSearchText).execute();
                        }
                        break;
                }
                return false;
            });
            popupMenu.show();
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

    private sExecutor deleteNote(int position) {
        return new sExecutor() {
            private List<sNotzItems> data;
            @Override
            public void onPreExecute() {
                mProgressBar.setVisibility(View.VISIBLE);
                data = sNotzData.getRawData(requireActivity());
            }

            @Override
            public void doInBackground() {
                for (sNotzItems rawItems : data) {
                    if (rawItems.getNoteID() == mData.get(position).getNoteID()) {
                        data.remove(rawItems);
                        mData.remove(position);
                        break;
                    }
                }
                updateDataBase(data, requireActivity());
            }

            @Override
            public void onPostExecute() {
                mNotesAdapter.notifyItemRemoved(position);
                mNotesAdapter.notifyItemRangeChanged(position, mNotesAdapter.getItemCount());
                mNotesAdapter.reset();
                mProgressBar.setVisibility(View.GONE);
            }
        };
    }

    private sExecutor loadUI(ContentLoadingProgressBar progressBar, String searchText) {
        return new sExecutor() {
            @Override
            public void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void doInBackground() {
                mData = sNotzData.getData(requireActivity(), searchText);
                setNoteFromIntent();
            }

            @Override
            public void onPostExecute() {
                try {
                    mRecyclerView.setAdapter(mNotesAdapter);
                } catch (NullPointerException ignored) {}
                mSearchText = searchText;
                progressBar.setVisibility(View.GONE);
            }

            private void setNoteFromIntent() {
                mNotesAdapter = new NotesAdapter(mData, mSpanCount, addItem);
                if (mExternalNote == null && mExtraNoteId == Integer.MIN_VALUE) return;
                if (mExternalNote != null) {
                    Intent createNote = new Intent(requireActivity(), NoteActivity.class);
                    createNote.putExtra(NoteActivity.NOTE_INTENT, mExternalNote);
                    createNote.putExtra(NoteActivity.NOTE_ID_INTENT, -1);
                    addItem.launch(createNote);
                } else {
                    for (sNotzItems items : sNotzData.getRawData(requireActivity())) {
                        if (items.getNoteID() == mExtraNoteId) {
                            Intent intent = getIntent(items);
                            addItem.launch(intent);
                            break;
                        }
                    }
                }
                mExternalNote = null;
                mExtraNoteId = Integer.MIN_VALUE;
            }

            @NonNull
            private Intent getIntent(sNotzItems items) {
                Intent intent;
                intent = new Intent(requireActivity(), NoteActivity.class);
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
                        private boolean hidden, isUpdate, toDelete;
                        private int colorBackground, colorText, id, position = RecyclerView.NO_POSITION;
                        private List<sNotzItems> data;
                        private sNotzItems item;
                        private String note;
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
                            data = sNotzData.getRawData(requireActivity());
                        }

                        @Override
                        public void doInBackground() {
                            position = getPosition();
                            if (toDelete) {
                                for (sNotzItems rawItems : data) {
                                    if (rawItems.getNoteID() == id) {
                                        data.remove(rawItems);
                                        if (position != RecyclerView.NO_POSITION) {
                                            mData.remove(position);
                                        }
                                        break;
                                    }
                                }
                            } else if (isUpdate) {
                                for (int i=0; i<data.size(); i++) {
                                    if (data.get(i).getNoteID() == id) {
                                        item = new sNotzItems(note, System.currentTimeMillis(), hidden, colorBackground, colorText, data.get(i).getNoteID());
                                        data.set(i, item);
                                        if (position != RecyclerView.NO_POSITION) {
                                            if (item.isHidden() && !sCommonUtils.getBoolean("hidden_note", false, requireActivity())) {
                                                mData.remove(position);
                                            } else {
                                                mData.set(position, item);
                                            }
                                        }
                                        break;
                                    }
                                }
                            } else {
                                item = new sNotzItems(note, System.currentTimeMillis(), hidden, colorBackground, colorText, id);
                                data.add(item);
                                if (!item.isHidden() || sCommonUtils.getBoolean("hidden_note", false, requireActivity())) {
                                    mData.add(0, item);
                                }
                            }
                            updateDataBase(data, requireActivity());
                        }

                        private int getPosition() {
                            for (int i=0; i<mData.size(); i++) {
                                if (mData.get(i).getNoteID() == id) {
                                    return i;
                                }
                            }
                            return RecyclerView.NO_POSITION;
                        }

                        @Override
                        public void onPostExecute() {
                            if (toDelete) {
                                mNotesAdapter.notifyItemRemoved(position);
                            } else if (isUpdate) {
                                if (item.isHidden() && !sCommonUtils.getBoolean("hidden_note", false, requireActivity())) {
                                    mNotesAdapter.notifyItemRemoved(position);
                                } else {
                                    mNotesAdapter.notifyItemChanged(position);
                                }
                            } else {
                                mRecyclerView.scrollToPosition(0);
                            }
                            mNotesAdapter.notifyItemRangeChanged(position, mNotesAdapter.getItemCount());
                            mNotesAdapter.reset();
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
                            private List<sNotzItems> data;

                            @Override
                            public void onPreExecute() {
                                mProgressBar.setVisibility(View.VISIBLE);
                                data = sNotzData.getRawData(requireActivity());
                            }

                            @Override
                            public void doInBackground() {
                                int i = sNotzUtils.generateNoteID(requireActivity());
                                for (sNotzItems items : sNotzUtils.getNotesFromBackup(dataBaseString, requireActivity())) {
                                    data.add(new sNotzItems(items.getNote(), items.getTimeStamp(), items.isHidden(), items.getColorBackground(), items.getColorText(), i));
                                    i++;
                                }

                                updateDataBase(data, requireActivity());
                            }

                            @Override
                            public void onPostExecute() {
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