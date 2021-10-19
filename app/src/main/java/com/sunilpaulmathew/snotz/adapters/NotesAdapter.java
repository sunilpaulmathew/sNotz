package com.sunilpaulmathew.snotz.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.activities.CreateNoteActivity;
import com.sunilpaulmathew.snotz.interfaces.DialogEditTextListener;
import com.sunilpaulmathew.snotz.utils.Common;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzColor;
import com.sunilpaulmathew.snotz.utils.sNotzItems;
import com.sunilpaulmathew.snotz.utils.sNotzReminders;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
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

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {
        holder.mContents.setText(this.data.get(position).getNote());
        holder.mContents.setTextColor(data.get(position).getColorText());
        holder.mExpand.setVisibility(Common.getSpanCount() > 1 ? View.GONE : View.VISIBLE);
        holder.mExpand.setOnClickListener(v -> {
            if (Common.isWorking() || Common.getSpanCount() > 1) {
                return;
            }
            if (holder.mContents.getLineCount() > 1) {
                holder.mContents.setSingleLine(true);
                holder.mExpand.setImageDrawable(sNotzUtils.getDrawable(R.drawable.ic_expand, v.getContext()));
            } else {
                holder.mContents.setSingleLine(false);
                holder.mExpand.setImageDrawable(sNotzUtils.getDrawable(R.drawable.ic_collapse, v.getContext()));
            }
        });
        holder.mExpand.setColorFilter(sNotzColor.getTextColor(holder.mExpand.getContext()));
        holder.mExpand.setImageDrawable(sNotzUtils.getDrawable(holder.mContents.getLineCount() > 1 ? R.drawable.ic_collapse :
                R.drawable.ic_expand, holder.mExpand.getContext()));
        holder.mExpand.setColorFilter(data.get(position).getColorText());
        holder.mRVCard.setOnLongClickListener(item -> {
            if (Common.isWorking()) {
                return true;
            }
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
                        sNotzUtils.shareNote(this.data.get(position).getNote(), this.data.get(position).getImageString(), holder.mRVCard.getContext());
                        break;
                    case 1:
                        if (this.data.get(position).isHidden()) {
                            sNotzUtils.hideNote(this.data.get(position).getNoteID(), false, holder.mProgress, holder.mRVCard.getContext()).execute();
                        } else {
                            sNotzUtils.hideNote(this.data.get(position).getNoteID(), true, holder.mProgress, holder.mRVCard.getContext()).execute();
                            Utils.showSnackbar(holder.mRVCard, holder.mRVCard.getContext().getString(R.string.hidden_note_message));
                        }
                        break;
                    case 2:
                        sNotzReminders.setYear(-1);
                        sNotzReminders.setMonth(-1);
                        sNotzReminders.setDay(-1);
                        sNotzReminders.launchReminderMenu(data.get(position).getNote(), item.getContext());
                        break;
                    case 3:
                        if (Build.VERSION.SDK_INT < 30 && Utils.isPermissionDenied(holder.mRVCard.getContext())) {
                            ActivityCompat.requestPermissions((Activity) holder.mRVCard.getContext(), new String[] {
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        } else {
                            if (this.data.get(position).getImageString() != null) {
                                Utils.showSnackbar(holder.mRVCard, holder.mRVCard.getContext().getString(R.string.image_excluded_warning));
                            }
                            DialogEditTextListener.dialogEditText(null, null,
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
                                    }, -1, (Activity) holder.mRVCard.getContext()).setOnDismissListener(dialogInterface -> {
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
                                .setPositiveButton(R.string.delete, (dialog, which) -> sNotzUtils.deleteNote(data.get(position).getNoteID(), holder.mProgress, holder.mRVCard.getContext()).execute()).show();
                        break;
                }
                return false;
            });
            popupMenu.show();
            return true;
        });
        holder.mRVCard.setCardBackgroundColor(data.get(position).getColorBackground());
        holder.mRVCard.setOnClickListener(v -> {
            if (Common.isWorking()) {
                return;
            }
            Common.setNote(this.data.get(position).getNote());
            Common.setID(this.data.get(position).getNoteID());
            Common.setBackgroundColor(this.data.get(position).getColorBackground());
            Common.setTextColor(this.data.get(position).getColorText());
            if (this.data.get(position).getImageString() != null) {
                Common.setImageString(this.data.get(position).getImageString());
            }
            Common.isHiddenNote(this.data.get(position).isHidden());
            Intent editNote = new Intent(holder.mRVCard.getContext(), CreateNoteActivity.class);
            holder.mRVCard.getContext().startActivity(editNote);
        });
        holder.mDate.setText(DateFormat.getDateTimeInstance().format(this.data.get(position).getTimeStamp()));
        holder.mDate.setTextColor(data.get(position).getColorText());
        holder.mProgress.setIndeterminateTintList(ColorStateList.valueOf(data.get(position).getColorText()));
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatImageButton mExpand;
        private final MaterialTextView mContents, mDate;
        private final MaterialCardView mRVCard;
        private final ProgressBar mProgress;

        public ViewHolder(View view) {
            super(view);
            this.mExpand = view.findViewById(R.id.expand);
            this.mProgress = view.findViewById(R.id.progress);
            this.mContents = view.findViewById(R.id.contents);
            this.mDate = view.findViewById(R.id.date);
            this.mRVCard = view.findViewById(R.id.rv_card);
        }
    }

}