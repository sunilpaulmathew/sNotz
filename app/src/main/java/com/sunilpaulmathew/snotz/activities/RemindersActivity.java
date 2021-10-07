package com.sunilpaulmathew.snotz.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.adapters.RemindersAdapter;
import com.sunilpaulmathew.snotz.receivers.ReminderReceiver;
import com.sunilpaulmathew.snotz.utils.Common;
import com.sunilpaulmathew.snotz.utils.ReminderItems;
import com.sunilpaulmathew.snotz.utils.sNotzReminders;

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

        mRecycleViewAdapter.setOnItemClickListener((position, v) ->
                new MaterialAlertDialogBuilder(this).setItems(getResources().getStringArray(
                R.array.reminder_options), (dialogInterface, i) -> {
            switch (i) {
                case 0:
                    Common.setNote(getData(this).get(position).getNote());
                    Common.setID(getData(this).get(position).getNotificationID());
                    Intent editReminder = new Intent(this, ReminderActivity.class);
                    startActivity(editReminder);
                    finish();
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