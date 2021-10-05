package com.sunilpaulmathew.snotz.adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.BuildConfig;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.activities.CreateNoteActivity;
import com.sunilpaulmathew.snotz.activities.ReminderActivity;
import com.sunilpaulmathew.snotz.utils.Common;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzColor;
import com.sunilpaulmathew.snotz.utils.sNotzItems;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2020
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private final List<sNotzItems> data;
    public NotesAdapter(List<sNotzItems> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_main, parent, false);
        return new ViewHolder(rowItem);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {
        if (Common.getSearchText() != null && Common.isTextMatched(this.data.get(position).getNote())) {
            holder.mContents.setText(Utils.fromHtml(this.data.get(position).getNote().replace(Common.getSearchText(),
                    "<b><i><font color=\"" + Color.RED + "\">" + Common.getSearchText() + "</font></i></b>")));
        } else {
            holder.mContents.setText(this.data.get(position).getNote());
        }
        holder.mContents.setTextColor(data.get(position).getColorText());
        holder.mExpand.setOnClickListener(v -> {
            if (Utils.getBoolean(position + "_expanded", false, holder.mExpand.getContext())) {
                Utils.saveBoolean(position + "_expanded", false, holder.mExpand.getContext());
                holder.mContents.setSingleLine(true);
                holder.mExpand.setImageDrawable(holder.mExpand.getContext().getResources().getDrawable(R.drawable.ic_expand));
            } else {
                Utils.saveBoolean(position + "_expanded", true, holder.mExpand.getContext());
                holder.mContents.setSingleLine(false);
                holder.mExpand.setImageDrawable(holder.mExpand.getContext().getResources().getDrawable(R.drawable.ic_collapse));
            }
        });
        holder.mExpand.setColorFilter(sNotzColor.getTextColor(holder.mExpand.getContext()));
        holder.mRVCard.setOnLongClickListener(item -> {
            PopupMenu popupMenu = new PopupMenu(holder.mRVCard.getContext(), holder.mExpand);
            Menu menu = popupMenu.getMenu();
            menu.add(Menu.NONE, 0, Menu.NONE, holder.mRVCard.getContext().getString(R.string.share));
            menu.add(Menu.NONE, 1, Menu.NONE, holder.mRVCard.getContext().getString(R.string.hidden_note)).setCheckable(true)
                    .setChecked(this.data.get(position).isHidden());
            menu.add(Menu.NONE, 2, Menu.NONE, holder.mRVCard.getContext().getString(R.string.set_reminder));
            menu.add(Menu.NONE, 3, Menu.NONE, holder.mRVCard.getContext().getString(R.string.save_text));
            menu.add(Menu.NONE, 4, Menu.NONE, holder.mRVCard.getContext().getString(R.string.delete));
            popupMenu.setOnMenuItemClickListener(popupMenuItem -> {
                switch (popupMenuItem.getItemId()) {
                    case 0:
                        Intent share_note = new Intent();
                        share_note.setAction(Intent.ACTION_SEND);
                        share_note.putExtra(Intent.EXTRA_SUBJECT, holder.mRVCard.getContext().getString(R.string.shared_by, BuildConfig.VERSION_NAME));
                        share_note.putExtra(Intent.EXTRA_TEXT, "\"" + this.data.get(position).getNote() + "\"\n\n" +
                                holder.mRVCard.getContext().getString(R.string.shared_by_message, BuildConfig.VERSION_NAME));
                        share_note.setType("text/plain");
                        Intent shareIntent = Intent.createChooser(share_note, holder.mRVCard.getContext().getString(R.string.share_with));
                        holder.mRVCard.getContext().startActivity(shareIntent);
                        break;
                    case 1:
                        if (this.data.get(position).isHidden()) {
                            sNotzUtils.hideNote(this.data.get(position).getNoteID(), false, item.getContext());
                        } else {
                            sNotzUtils.hideNote(this.data.get(position).getNoteID(), true, item.getContext());
                            Utils.showSnackbar(holder.mRVCard, holder.mRVCard.getContext().getString(R.string.hidden_note_message));
                        }
                        Utils.reloadUI(item.getContext()).execute();
                        break;
                    case 2:
                        Common.setID(-1);
                        Common.setNote(this.data.get(position).getNote());
                        Intent setAlarm = new Intent(holder.mRVCard.getContext(), ReminderActivity.class);
                        holder.mRVCard.getContext().startActivity(setAlarm);
                        break;
                    case 3:
                        if (Build.VERSION.SDK_INT < 30 && Utils.isPermissionDenied(holder.mRVCard.getContext())) {
                            ActivityCompat.requestPermissions((Activity) holder.mRVCard.getContext(), new String[] {
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        } else {
                            Utils.dialogEditText(null,
                                    (dialogInterface, i) -> {
                                    }, text -> {
                                        if (text.isEmpty()) {
                                            Utils.showSnackbar(holder.mRVCard, holder.mRVCard.getContext().getString(R.string.text_empty));
                                            return;
                                        }
                                        if (!text.endsWith(".txt")) {
                                            text += ".txt";
                                        }
                                        if (text.contains(" ")) {
                                            text = text.replace(" ", "_");
                                        }
                                        if (Build.VERSION.SDK_INT >= 30) {
                                            try {
                                                ContentValues values = new ContentValues();
                                                values.put(MediaStore.MediaColumns.DISPLAY_NAME, text);
                                                values.put(MediaStore.MediaColumns.MIME_TYPE, "*/*");
                                                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                                                Uri uri = holder.mRVCard.getContext().getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                                                OutputStream outputStream = holder.mRVCard.getContext().getContentResolver().openOutputStream(uri);
                                                outputStream.write(Objects.requireNonNull(this.data.get(position).getNote()).getBytes());
                                                outputStream.close();
                                            } catch (IOException ignored) {
                                            }
                                        } else {
                                            Utils.create(this.data.get(position).getNote(), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + text);
                                        }
                                        Utils.showSnackbar(holder.mRVCard, holder.mRVCard.getContext().getString(R.string.save_text_message,
                                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + text));
                                    }, holder.mRVCard.getContext()).setOnDismissListener(dialogInterface -> {
                            }).show();
                        }
                        break;
                    case 4:
                        String[] sNotzContents = this.data.get(position).getNote().split("\\s+");
                        new MaterialAlertDialogBuilder(holder.mRVCard.getContext())
                                .setMessage(holder.mRVCard.getContext().getString(R.string.delete_sure_question, sNotzContents.length <= 2 ?
                                        this.data.get(position).getNote() : sNotzContents[0] + " " + sNotzContents[1] + " " + sNotzContents[2] + "..."))
                                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                                })
                                .setPositiveButton(R.string.delete, (dialog, which) -> {
                                    sNotzUtils.deleteNote(this.data.get(position).getNoteID(), item.getContext());
                                    data.remove(position);
                                    notifyItemRemoved(position);
                                    Utils.reloadUI(item.getContext());
                                })
                                .show();
                        break;
                }
                return false;
            });
            popupMenu.show();
            return true;
        });
        holder.mRVCard.setCardBackgroundColor(data.get(position).getColorBackground());
        holder.mRVCard.setOnClickListener(v -> {
            Common.setNote(this.data.get(position).getNote());
            Common.setBackgroundColor(this.data.get(position).getColorBackground());
            Common.setTextColor(this.data.get(position).getColorText());
            Common.isHiddenNote(this.data.get(position).isHidden());
            Intent editNote = new Intent(holder.mRVCard.getContext(), CreateNoteActivity.class);
            holder.mRVCard.getContext().startActivity(editNote);
        });
        holder.mDate.setText(this.data.get(position).getTimeStamp());
        holder.mDate.setTextColor(data.get(position).getColorText());
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatImageButton mExpand;
        private final MaterialTextView mContents, mDate;
        private final MaterialCardView mRVCard;

        public ViewHolder(View view) {
            super(view);
            this.mExpand = view.findViewById(R.id.expand);
            this.mContents = view.findViewById(R.id.contents);
            this.mDate = view.findViewById(R.id.date);
            this.mRVCard = view.findViewById(R.id.rv_card);
        }
    }

}