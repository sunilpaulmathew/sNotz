package com.sunilpaulmathew.snotz.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.View;

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
import com.sunilpaulmathew.snotz.interfaces.EditTextInterface;
import com.sunilpaulmathew.snotz.utils.CheckLists;
import com.sunilpaulmathew.snotz.utils.Common;
import com.sunilpaulmathew.snotz.utils.Utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import in.sunilpaulmathew.sCommon.Utils.sUtils;

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
        AppCompatImageButton mBack = findViewById(R.id.back_button);
        mRecyclerView = findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, Utils.getSpanCount(this)));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mRecyclerView.setAdapter(new CheckListsAdapter(CheckLists.getCheckLists(this)));

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
                        .setMessage(viewHolder.itemView.getContext().getString(R.string.delete_sure_question, CheckLists.getCheckLists(CheckListsActivity.this).get(position).getName()))
                        .setNegativeButton(R.string.cancel, (dialogInterface, i) -> mRecyclerView.setAdapter(new CheckListsAdapter(CheckLists.getCheckLists(CheckListsActivity.this))))
                        .setCancelable(false)
                        .setPositiveButton(R.string.delete, (dialogInterface, i) -> {
                            CheckLists.getCheckLists(CheckListsActivity.this).get(position).delete();
                            mRecyclerView.setAdapter(new CheckListsAdapter(CheckLists.getCheckLists(CheckListsActivity.this)));
                        }).show();
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

        mBack.setOnClickListener(v -> finish());
    }

    private void createCheckList() {
        new EditTextInterface(null, getString(R.string.check_list_create_question), this) {

            @Override
            public void positiveButtonLister(Editable s) {
                if (s != null && !s.toString().trim().isEmpty()) {
                    if (sUtils.exist(new File(getExternalFilesDir("checklists"), s.toString().trim()))) {
                        new MaterialAlertDialogBuilder(CheckListsActivity.this)
                                .setMessage(getString(R.string.check_list_exist_warning))
                                .setNegativeButton(getString(R.string.change_name), (dialogInterface, i) -> createCheckList())
                                .setPositiveButton(getString(R.string.replace), (dialogInterface, i) -> {
                                    CheckLists.setCheckListName(s.toString().trim());
                                    Intent createCheckList = new Intent(CheckListsActivity.this, CheckListActivity.class);
                                    startActivity(createCheckList);
                                }).show();
                    } else {
                        CheckLists.setCheckListName(s.toString().trim());
                        Intent createCheckList = new Intent(CheckListsActivity.this, CheckListActivity.class);
                        startActivity(createCheckList);
                    }
                } else {
                    sUtils.snackBar(findViewById(android.R.id.content), getString(R.string.check_list_name_empty_message)).show();
                }
            }
        }.show();
    }

    private void importCheckList() {
        new EditTextInterface(null, getString(R.string.check_list_import_question), this) {

            @Override
            public void positiveButtonLister(Editable s) {
                if (s != null && !s.toString().trim().isEmpty()) {
                    if (sUtils.exist(new File(getExternalFilesDir("checklists"), s.toString().trim()))) {
                        new MaterialAlertDialogBuilder(CheckListsActivity.this)
                                .setMessage(getString(R.string.check_list_exist_warning))
                                .setNegativeButton(getString(R.string.change_name), (dialogInterface, i) -> importCheckList())
                                .setPositiveButton(getString(R.string.replace), (dialogInterface, i) -> sUtils.create(mJSONString, new File(
                                        getExternalFilesDir("checklists"), s.toString().trim()))).show();
                    } else {
                        sUtils.create(mJSONString, new File(getExternalFilesDir("checklists"), s.toString().trim()));
                        mRecyclerView.setAdapter(new CheckListsAdapter(CheckLists.getCheckLists(CheckListsActivity.this)));
                    }
                } else {
                    sUtils.snackBar(findViewById(android.R.id.content), getString(R.string.check_list_name_empty_message)).show();
                }
            }
        }.show();
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
                sUtils.snackBar(findViewById(android.R.id.content), getString(R.string.restore_error)).show();
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
            mRecyclerView.setAdapter(new CheckListsAdapter(CheckLists.getCheckLists(this)));
        }
    }

}