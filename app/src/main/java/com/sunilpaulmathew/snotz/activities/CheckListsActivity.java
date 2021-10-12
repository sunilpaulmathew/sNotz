package com.sunilpaulmathew.snotz.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.adapters.CheckListsAdapter;
import com.sunilpaulmathew.snotz.utils.CheckLists;
import com.sunilpaulmathew.snotz.utils.Common;
import com.sunilpaulmathew.snotz.utils.Utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 10, 2021
 */
public class CheckListsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private String mJSONString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklists);

        AppCompatImageButton mAdd = findViewById(R.id.add_button);
        mRecyclerView = findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, Utils.getSpanCount(this)));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mRecyclerView.setAdapter(new CheckListsAdapter(getData()));

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
                new MaterialAlertDialogBuilder(viewHolder.itemView.getContext())
                        .setMessage(viewHolder.itemView.getContext().getString(R.string.delete_sure_question, getData().get(position).getName()))
                        .setNegativeButton(R.string.cancel, (dialogInterface, i) -> mRecyclerView.setAdapter(new CheckListsAdapter(getData())))
                        .setPositiveButton(R.string.delete, (dialogInterface, i) -> {
                            getData().get(position).delete();
                            mRecyclerView.setAdapter(new CheckListsAdapter(getData()));
                        }).show();
            }
        });

        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mAdd.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, mAdd);
            Menu menu = popupMenu.getMenu();
            menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.create));
            menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.import_item));
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 0:
                        createCheckList();
                        break;
                    case 1:
                        Intent restore = new Intent(Intent.ACTION_GET_CONTENT);
                        restore.setType("*/*");
                        restore.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(restore, 0);
                        break;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    public List<File> getData() {
        List<File> mCheckLists = new ArrayList<>();
        for (File checklists : Objects.requireNonNull(getExternalFilesDir("checklists").listFiles())) {
            if (CheckLists.isValidCheckList(Utils.read(checklists.getAbsolutePath()))) {
                mCheckLists.add(checklists);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(mCheckLists, Comparator.comparingLong(File::lastModified));
        }
        Collections.reverse(mCheckLists);
        return mCheckLists;
    }

    private void createCheckList() {
        Utils.dialogEditText(null, getString(R.string.check_list_create_question),
                (dialogInterface, i) -> {
                }, text -> {
                    if (text.isEmpty()) {
                        Utils.showSnackbar(findViewById(android.R.id.content), getString(R.string.check_list_name_empty_message));
                        return;
                    }
                    if (Utils.exist(new File(getExternalFilesDir("checklists"), text).getAbsolutePath())) {
                        new MaterialAlertDialogBuilder(this)
                                .setMessage(getString(R.string.check_list_exist_warning))
                                .setNegativeButton(getString(R.string.change_name), (dialogInterface, i) -> createCheckList())
                                .setPositiveButton(getString(R.string.replace), (dialogInterface, i) -> {
                                    CheckLists.setCheckListName(text);
                                    Intent createCheckList = new Intent(this, CheckListActivity.class);
                                    startActivity(createCheckList);
                                }).show();
                        return;
                    }
                    CheckLists.setCheckListName(text);
                    Intent createCheckList = new Intent(this, CheckListActivity.class);
                    startActivity(createCheckList);
                }, -1,this).setOnDismissListener(dialogInterface -> {
        }).show();
    }

    private void importCheckList() {
        Utils.dialogEditText(null, getString(R.string.check_list_import_question),
                (dialogInterface, i) -> {
                }, text -> {
                    if (text.isEmpty()) {
                        Utils.showSnackbar(findViewById(android.R.id.content), getString(R.string.check_list_name_empty_message));
                        return;
                    }
                    if (Utils.exist(new File(getExternalFilesDir("checklists"), text).getAbsolutePath())) {
                        new MaterialAlertDialogBuilder(this)
                                .setMessage(getString(R.string.check_list_exist_warning))
                                .setNegativeButton(getString(R.string.change_name), (dialogInterface, i) -> importCheckList())
                                .setPositiveButton(getString(R.string.replace), (dialogInterface, i) -> Utils.create(mJSONString, getExternalFilesDir("checklists") + "/" + text)).show();
                        return;
                    }
                    Utils.create(mJSONString, getExternalFilesDir("checklists") + "/" + text);
                    mRecyclerView.setAdapter(new CheckListsAdapter(getData()));
                }, -1,this).setOnDismissListener(dialogInterface -> {
        }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                for (int result = bis.read(); result != -1; result = bis.read()) {
                    buf.write((byte) result);
                }
                mJSONString = buf.toString("UTF-8");
            } catch (IOException ignored) {}

            if (mJSONString == null || !CheckLists.isValidCheckList(mJSONString)) {
                Utils.showSnackbar(findViewById(android.R.id.content), getString(R.string.restore_error));
                return;
            }
            importCheckList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Common.isReloading()) {
            Common.isReloading(false);
            mRecyclerView.setAdapter(new CheckListsAdapter(getData()));
        }
    }

}