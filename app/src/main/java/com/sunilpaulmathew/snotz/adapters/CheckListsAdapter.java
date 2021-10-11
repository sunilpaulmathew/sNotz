package com.sunilpaulmathew.snotz.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.BuildConfig;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.activities.CheckListActivity;
import com.sunilpaulmathew.snotz.utils.CheckLists;

import java.io.File;
import java.text.DateFormat;
import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 10, 2021
 */
public class CheckListsAdapter extends RecyclerView.Adapter<CheckListsAdapter.ViewHolder> {

    private static List<File> data;
    public CheckListsAdapter(List<File> data) {
        CheckListsAdapter.data = data;
    }

    @NonNull
    @Override
    public CheckListsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_checklists, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckListsAdapter.ViewHolder holder, int position) {
        holder.mTitle.setText(data.get(position).getName());
        holder.mStatus.setText(DateFormat.getDateTimeInstance().format(data.get(position).lastModified()));

        holder.mShare.setOnClickListener(v -> {
            Intent mIntent = new Intent();
            mIntent.setAction(Intent.ACTION_SEND);
            mIntent.putExtra(Intent.EXTRA_SUBJECT, data.get(position).getName() + "/" + v.getContext().getString(R.string.shared_by, BuildConfig.VERSION_NAME));
            mIntent.putExtra(Intent.EXTRA_TEXT, v.getContext().getString(R.string.shared_by_message, BuildConfig.VERSION_NAME));
            mIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(v.getContext(),BuildConfig.APPLICATION_ID + ".provider", data.get(position)));
            mIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mIntent.setType("*/*");
            Intent shareIntent = Intent.createChooser(mIntent, v.getContext().getString(R.string.share_with));
            v.getContext().startActivity(shareIntent);
        });

        holder.mDownload.setOnClickListener(v -> new MaterialAlertDialogBuilder(v.getContext())
                .setMessage(v.getContext().getString(R.string.check_lists_backup_question, data.get(position).getName()))
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                })
                .setPositiveButton(R.string.backup, (dialogInterface, i) -> {
                    CheckLists.setCheckListName(data.get(position).getName());
                    CheckLists.backupCheckList((Activity) v.getContext());
                }).show());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AppCompatImageButton mShare, mDownload;
        private final MaterialTextView mTitle, mStatus;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.mTitle = view.findViewById(R.id.title);
            this.mStatus = view.findViewById(R.id.status);
            this.mShare = view.findViewById(R.id.share);
            this.mDownload = view.findViewById(R.id.download);
        }

        @Override
        public void onClick(View view) {
            CheckLists.setCheckListName(data.get(getAdapterPosition()).getName());
            Intent createCheckList = new Intent(view.getContext(), CheckListActivity.class);
            view.getContext().startActivity(createCheckList);
        }

    }

}