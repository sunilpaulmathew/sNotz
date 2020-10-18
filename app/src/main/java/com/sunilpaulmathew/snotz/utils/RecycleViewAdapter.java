/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Script Manager, an app to create, import, edit
 * and easily execute any properly formatted shell scripts.
 *
 */

package com.sunilpaulmathew.snotz.utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sunilpaulmathew.snotz.BuildConfig;
import com.sunilpaulmathew.snotz.R;

import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2020
 */

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    private List<String> data;
    public RecycleViewAdapter (List<String> data){
        this.data = data;
    }

    @NonNull
    @Override
    public RecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_recycle_view, parent, false);
        return new ViewHolder(rowItem);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull RecycleViewAdapter.ViewHolder holder, int position) {
        if (Utils.mSearchText != null && Objects.requireNonNull(sNotz.getNote(this.data.get(position))).toLowerCase().contains(Utils.mSearchText)) {
            holder.mContents.setText(Utils.fromHtml(Objects.requireNonNull(sNotz.getNote(this.data.get(position))).toLowerCase().replace(Utils.mSearchText,
                    "<b><i><font color=\"" + Color.RED + "\">" + Utils.mSearchText + "</font></i></b>")));
        } else {
            holder.mContents.setText(sNotz.getNote(this.data.get(position)));
        }
        holder.mContents.setTextColor(sNotzColor.setAccentColor("text_color", holder.mContents.getContext()));
        holder.mExpand.setOnClickListener(v -> {
            if (Utils.getBoolean(this.data.get(position) + "_expanded", false, holder.mExpand.getContext())) {
                Utils.saveBoolean(this.data.get(position) + "_expanded", false, holder.mExpand.getContext());
                holder.mContents.setSingleLine(true);
                holder.mExpand.setImageDrawable(holder.mExpand.getContext().getResources().getDrawable(R.drawable.ic_expand));
            } else {
                Utils.saveBoolean(this.data.get(position) + "_expanded", true, holder.mExpand.getContext());
                holder.mContents.setSingleLine(false);
                holder.mExpand.setImageDrawable(holder.mExpand.getContext().getResources().getDrawable(R.drawable.ic_collapse));
            }
        });
        holder.mExpand.setColorFilter(sNotzColor.setAccentColor("text_color", holder.mExpand.getContext()));
        holder.mRVCard.setOnLongClickListener(item -> {
            PopupMenu popupMenu = new PopupMenu(holder.mRVCard.getContext(), holder.mExpand);
            Menu menu = popupMenu.getMenu();
            menu.add(Menu.NONE, 0, Menu.NONE, holder.mRVCard.getContext().getString(R.string.share));
            menu.add(Menu.NONE, 1, Menu.NONE, holder.mRVCard.getContext().getString(R.string.hidden_note)).setCheckable(true)
                    .setChecked(Utils.getBoolean(this.data.get(position), false, holder.mRVCard.getContext()));
            menu.add(Menu.NONE, 2, Menu.NONE, holder.mRVCard.getContext().getString(R.string.delete));
            popupMenu.setOnMenuItemClickListener(popupMenuItem -> {
                switch (popupMenuItem.getItemId()) {
                    case 0:
                        Intent share_app = new Intent();
                        share_app.setAction(Intent.ACTION_SEND);
                        share_app.putExtra(Intent.EXTRA_SUBJECT, holder.mRVCard.getContext().getString(R.string.shared_by, BuildConfig.VERSION_NAME));
                        share_app.putExtra(Intent.EXTRA_TEXT, "\"" + sNotz.getNote(this.data.get(position)) + "\"\n\n" +
                                holder.mRVCard.getContext().getString(R.string.shared_by_message, BuildConfig.VERSION_NAME));
                        share_app.setType("text/plain");
                        Intent shareIntent = Intent.createChooser(share_app, holder.mRVCard.getContext().getString(R.string.share_with));
                        holder.mRVCard.getContext().startActivity(shareIntent);
                        break;
                    case 1:
                        if (Utils.getBoolean(this.data.get(position), false, holder.mRVCard.getContext())) {
                            Utils.saveBoolean(this.data.get(position), false, holder.mRVCard.getContext());
                        } else {
                            Utils.saveBoolean(this.data.get(position), true, holder.mRVCard.getContext());
                        }
                        Utils.reloadUI(holder.mRVCard.getContext());
                        break;
                    case 2:
                        String mJson = holder.mRVCard.getContext().getFilesDir().toString() + "/snotz";
                        String[] sNotzContents = Objects.requireNonNull(sNotz.getNote(this.data.get(position))).split("\\s+");
                        new AlertDialog.Builder(holder.mRVCard.getContext())
                                .setMessage(holder.mRVCard.getContext().getString(R.string.delete_sure_question, sNotzContents.length <= 2 ?
                                        sNotz.getNote(this.data.get(position)) : sNotzContents[0] + " " + sNotzContents[1] + " " + sNotzContents[2] + "..."))
                                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                                })
                                .setPositiveButton(R.string.delete, (dialog, which) -> {
                                    if (Objects.requireNonNull(Utils.readFile(mJson)).contains("," + this.data.get(position))) {
                                        Utils.create(Objects.requireNonNull(Utils.readFile(mJson)).replace("," + this.data.get(position),""), mJson);
                                    } else if (Objects.requireNonNull(Utils.readFile(mJson)).contains(this.data.get(position) + ",")) {
                                        Utils.create(Objects.requireNonNull(Utils.readFile(mJson)).replace(this.data.get(position) + ",",""), mJson);
                                    } else {
                                        Utils.deleteFile(mJson);
                                    }
                                    Utils.reloadUI(holder.mRVCard.getContext());
                                })
                                .show();
                        break;
                }
                return false;
            });
            popupMenu.show();
            return true;
        });
        holder.mRVCard.setCardBackgroundColor(sNotzColor.setAccentColor("note_background", holder.mRVCard.getContext()));
        holder.mRVCard.setOnClickListener(v -> {
            Utils.mName = this.data.get(position);
            Intent editNote = new Intent(holder.mRVCard.getContext(), CreateNoteActivity.class);
            holder.mRVCard.getContext().startActivity(editNote);
        });
        holder.mDate.setText(sNotz.getDate(this.data.get(position)));
        holder.mDate.setTextColor(sNotzColor.setAccentColor("text_color", holder.mDate.getContext()));
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private AppCompatImageButton mExpand;
        private AppCompatTextView mContents;
        private AppCompatTextView mDate;
        private CardView mRVCard;

        public ViewHolder(View view) {
            super(view);
            this.mExpand = view.findViewById(R.id.expand);
            this.mContents = view.findViewById(R.id.contents);
            this.mDate = view.findViewById(R.id.date);
            this.mRVCard = view.findViewById(R.id.rv_card);
        }
    }

}