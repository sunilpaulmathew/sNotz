package com.sunilpaulmathew.snotz.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.adapters.RemindersAdapter;
import com.sunilpaulmathew.snotz.receivers.ReminderReceiver;
import com.sunilpaulmathew.snotz.utils.Common;
import com.sunilpaulmathew.snotz.utils.ReminderItems;
import com.sunilpaulmathew.snotz.utils.sNotzReminders;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 04, 2021
 */
public class RemindersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        AppCompatImageButton mBack = findViewById(R.id.back_button);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        RemindersAdapter mRecycleViewAdapter = new RemindersAdapter(getData(this));
        mRecyclerView.setAdapter(mRecycleViewAdapter);

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
                String[] sNotzContents = getData(RemindersActivity.this).get(position).getNote().split("\\s+");
                new MaterialAlertDialogBuilder(viewHolder.itemView.getContext())
                        .setMessage(viewHolder.itemView.getContext().getString(R.string.delete_sure_question, sNotzContents.length <= 2 ?
                                getData(RemindersActivity.this).get(position).getNote() : sNotzContents[0] + " " + sNotzContents[1] + " " + sNotzContents[2] + "..."))
                        .setNegativeButton(R.string.cancel, (dialogInterface, i) -> mRecyclerView.setAdapter(new RemindersAdapter(getData(RemindersActivity.this))))
                        .setCancelable(false)
                        .setPositiveButton(R.string.delete, (dialogInterface, i) -> {
                            deleteReminder(getData(RemindersActivity.this).get(position).getNotificationID(), RemindersActivity.this);
                            mRecyclerView.setAdapter(new RemindersAdapter(getData(RemindersActivity.this)));
                        }).show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;

                    Paint mPaint = new Paint();
                    mPaint.setColor(sNotzUtils.getColor(R.color.color_red, viewHolder.itemView.getContext()));
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

        mRecycleViewAdapter.setOnItemClickListener((position, v) ->
                new MaterialAlertDialogBuilder(this).setItems(getResources().getStringArray(
                R.array.reminder_options), (dialogInterface, i) -> {
            switch (i) {
                case 0:
                    sNotzReminders.setYear(-1);
                    sNotzReminders.setMonth(-1);
                    sNotzReminders.setDay(-1);
                    sNotzReminders.launchDatePicker(getData(this).get(position).getNotificationID(), getData(this).get(position).getNote(), this).show();
                    break;
                case 1:
                    deleteReminder(getData(this).get(position).getNotificationID(), this);
                    mRecyclerView.setAdapter(new RemindersAdapter(getData(this)));
                    break;
            }
        }).setOnDismissListener(dialogInterface -> {
        }).show());

        mBack.setOnClickListener(v -> finish());
    }

    private static void deleteReminder(int id, Activity activity) {
        AlarmManager mAlarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        Intent mIntent = new Intent(activity, ReminderReceiver.class);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(activity, id, mIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(mPendingIntent);
        sNotzReminders.delete(id, activity);
        Common.setID(-1);
    }

    private static List<ReminderItems> getData(Activity activity) {
        List<ReminderItems> mData = new ArrayList<>(sNotzReminders.getRawData(activity));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(mData, Comparator.comparingInt(lhs -> (lhs.getHour() * 60) + lhs.getMin()));
        } else {
            Collections.sort(mData, (lhs, rhs) -> String.CASE_INSENSITIVE_ORDER.compare(String.valueOf((lhs.getHour() * 60) + lhs.getMin()), String.valueOf((rhs.getHour() * 60) + rhs.getMin())));
        }
        return mData;
    }

}