package com.sunilpaulmathew.snotz.utils.dialogs;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.adapters.RestoreNotesAdapter;
import com.sunilpaulmathew.snotz.utils.AppSettings;
import com.sunilpaulmathew.snotz.utils.sNotzColor;
import com.sunilpaulmathew.snotz.utils.sNotzData;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;
import com.sunilpaulmathew.snotz.utils.serializableItems.RestoreNotesEntry;
import com.sunilpaulmathew.snotz.utils.serializableItems.sNotzEntry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import in.sunilpaulmathew.sCommon.CommonUtils.sExecutor;
import in.sunilpaulmathew.sCommon.FileUtils.sFileUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on March 18, 2023
 */
public abstract class RestoreNoteDialog extends BottomSheetDialog {

    private RestoreNotesAdapter adapter = null;

    public RestoreNoteDialog(String jsonString, boolean autoBackup, Context context) {
        super(context);

        View view = View.inflate(context, R.layout.layout_restore_notes, null);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        MaterialButton cancel = view.findViewById(R.id.cancel);
        MaterialButton restore = view.findViewById(R.id.restore);
        MaterialCardView descriptionCard = view.findViewById(R.id.description_card);
        MaterialTextView title = view.findViewById(R.id.title);

        if (autoBackup) {
            title.setText(R.string.restore_notes);
            descriptionCard.setVisibility(VISIBLE);
        }

        title.setTextColor(sNotzColor.getAppAccentColor(context));

        cancel.setOnClickListener(v -> {
            if (autoBackup) {
                sFileUtils.delete(new File(context.getExternalFilesDir("autoBackup"),"autoBackup"));
            }
            negativeButtonLister();
            dismiss();
        });

        restore.setOnClickListener(v -> {
            dismiss();
            new sExecutor() {
                private List<sNotzEntry> originalData;
                private int startingId;

                @Override
                public void onPreExecute() {
                    originalData = sNotzData.getRawData(context);
                    startingId = sNotzUtils.generateNoteID(context);
                }

                @Override
                public void doInBackground() {

                    List<sNotzEntry> updatedList = new ArrayList<>(originalData);

                    int currentId = startingId;
                    for (RestoreNotesEntry item : adapter.getData()) {
                        if (item.isSelected()) {
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
                    }

                    sNotzUtils.updateDataBase(updatedList, context);

                    AppSettings.backupData(new File(context.getFilesDir(),"snotz"), new File(context.getExternalFilesDir("autoBackup"),"autoBackup"), context);
                }

                @Override
                public void onPostExecute() {
                    positiveButtonLister();
                }
            }.execute();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        new sExecutor() {
            private List<RestoreNotesEntry> data;
            @Override
            public void onPreExecute() {
            }

            @Override
            public void doInBackground() {
                data = getData(context);
            }

            private List<RestoreNotesEntry> getData(Context context) {
                List<RestoreNotesEntry> mData = new CopyOnWriteArrayList<>();
                JsonArray sNotz = JsonParser.parseString(jsonString).getAsJsonObject().getAsJsonArray("sNotz");
                for (int i = 0; i < sNotz.size(); i++) {
                    mData.add(new RestoreNotesEntry(new sNotzEntry(
                            sNotzData.getNote(sNotz.get(i).getAsJsonObject()),
                            sNotzData.getDate(sNotz.get(i).getAsJsonObject()),
                            sNotzData.isHidden(sNotz.get(i).getAsJsonObject()),
                            sNotzData.getBackgroundColor(sNotz.get(i).getAsJsonObject(), context),
                            sNotzData.getTextColor(sNotz.get(i).getAsJsonObject(), context),
                            sNotzData.getNoteID(sNotz.get(i).getAsJsonObject())),
                            true));
                }
                return mData;
            }

            @Override
            public void onPostExecute() {
                adapter = new RestoreNotesAdapter(data);

                recyclerView.setAdapter(adapter);
            }
        }.execute();

        setContentView(view);
        setCancelable(false);
        show();
    }

    public abstract void negativeButtonLister();

    public abstract void positiveButtonLister();

}