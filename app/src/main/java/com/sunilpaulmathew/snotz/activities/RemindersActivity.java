package com.sunilpaulmathew.snotz.activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
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
import com.sunilpaulmathew.snotz.utils.sNotzReminders;

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
        RemindersAdapter mRecycleViewAdapter = new RemindersAdapter(sNotzReminders.getRawData(this));
        mRecyclerView.setAdapter(mRecycleViewAdapter);

        mRecycleViewAdapter.setOnItemClickListener((position, v) -> {
            new MaterialAlertDialogBuilder(this).setItems(getResources().getStringArray(
                    R.array.reminder_options), (dialogInterface, i) -> {
                switch (i) {
                    case 0:
                        Common.setNote(sNotzReminders.getRawData(this).get(position).getNote());
                        Common.setID(sNotzReminders.getRawData(this).get(position).getNotificationID());
                        Intent editReminder = new Intent(this, ReminderActivity.class);
                        startActivity(editReminder);
                        finish();
                        break;
                    case 1:
                        deleteReminder(sNotzReminders.getRawData(this).get(position).getNotificationID());
                        mRecyclerView.setAdapter(new RemindersAdapter(sNotzReminders.getRawData(this)));
                        break;
                }
            }).setOnDismissListener(dialogInterface -> {
            }).show();
        });

        mBack.setOnClickListener(v -> finish());
    }

    private void deleteReminder(int id) {
        AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent mIntent = new Intent(this, ReminderReceiver.class);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(this, id, mIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(mPendingIntent);
        sNotzReminders.delete(id, this);
        Common.setID(-1);
    }

}