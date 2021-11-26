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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.activities.CreateNoteActivity;
import com.sunilpaulmathew.snotz.interfaces.DialogEditTextListener;
import com.sunilpaulmathew.snotz.utils.AppSettings;
import com.sunilpaulmathew.snotz.utils.Common;
import com.sunilpaulmathew.snotz.utils.QRCodeUtils;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzColor;
import com.sunilpaulmathew.snotz.utils.sNotzItems;
import com.sunilpaulmathew.snotz.utils.sNotzReminders;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.List;
import java.util.Objects;

import in.sunilpaulmathew.sCommon.Utils.sPermissionUtils;
import in.sunilpaulmathew.sCommon.Utils.sUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2020
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private int mPosition = RecyclerView.NO_POSITION;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mContents.setText(this.data.get(position).getNote());
        if (position == mPosition) {
            holder.mActionLayout.setVisibility(View.VISIBLE);
            holder.mDate.setVisibility(View.GONE);
        } else {
            holder.mActionLayout.setVisibility(View.GONE);
            holder.mDate.setVisibility(View.VISIBLE);
        }
        holder.mContents.setTextColor(data.get(position).getColorText());
        holder.mContents.setTextSize(TypedValue.COMPLEX_UNIT_SP, sUtils.getInt("font_size", 18, holder.mContents.getContext()));
        holder.mContents.setTypeface(null, AppSettings.getStyle(holder.mContents.getContext()));
        holder.mExpand.setVisibility(Common.getSpanCount() > 1 ? View.GONE : View.VISIBLE);
        holder.mExpand.setOnClickListener(v -> {
            if (Common.isWorking() || Common.getSpanCount() > 1) {
                return;
            }
            if (holder.mContents.getLineCount() > 1) {
                holder.mContents.setSingleLine(true);
                holder.mExpand.setImageDrawable(sUtils.getDrawable(R.drawable.ic_expand, v.getContext()));
            } else {
                holder.mContents.setSingleLine(false);
                holder.mExpand.setImageDrawable(sUtils.getDrawable(R.drawable.ic_collapse, v.getContext()));
            }
        });
        holder.mExpand.setColorFilter(sNotzColor.getTextColor(holder.mExpand.getContext()));
        holder.mExpand.setImageDrawable(sUtils.getDrawable(holder.mContents.getLineCount() > 1 ? R.drawable.ic_collapse :
                R.drawable.ic_expand, holder.mExpand.getContext()));
        holder.mExpand.setColorFilter(data.get(position).getColorText());
        holder.mRVCard.setOnLongClickListener(item -> {
            if (Common.isWorking()) {
                return true;
            }
            if (Utils.isActionMenuSize((Activity) item.getContext())) {
                PopupMenu popupMenu = new PopupMenu(holder.mRVCard.getContext(), holder.mExpand);
                Menu menu = popupMenu.getMenu();
                menu.add(Menu.NONE, 0, Menu.NONE, holder.mRVCard.getContext().getString(R.string.share));
                menu.add(Menu.NONE, 1, Menu.NONE, holder.mRVCard.getContext().getString(R.string.hidden_note)).setCheckable(true)
                        .setChecked(this.data.get(position).isHidden());
                menu.add(Menu.NONE, 2, Menu.NONE, holder.mRVCard.getContext().getString(R.string.set_reminder));
                menu.add(Menu.NONE, 3, Menu.NONE, holder.mRVCard.getContext().getString(R.string.qr_code_generate));
                menu.add(Menu.NONE, 4, Menu.NONE, holder.mRVCard.getContext().getString(R.string.save_text));
                menu.add(Menu.NONE, 5, Menu.NONE, holder.mRVCard.getContext().getString(R.string.delete));
                popupMenu.setOnMenuItemClickListener(popupMenuItem -> {
                    switch (popupMenuItem.getItemId()) {
                        case 0:
                            sNotzUtils.shareNote(this.data.get(position).getNote(), this.data.get(position).getImageString(), holder.mRVCard.getContext());
                            break;
                        case 1:
                            if (this.data.get(position).isHidden()) {
                                sNotzUtils.hideNote(this.data.get(position).getNoteID(),false, holder.mProgress, holder.mRVCard.getContext()).execute();
                            } else {
                                sNotzUtils.hideNote(this.data.get(position).getNoteID(),true, holder.mProgress, holder.mRVCard.getContext()).execute();
                                sUtils.snackBar(holder.mRVCard, holder.mRVCard.getContext().getString(R.string.hidden_note_message)).show();
                            }
                            break;
                        case 2:
                            sNotzReminders.setYear(-1);
                            sNotzReminders.setMonth(-1);
                            sNotzReminders.setDay(-1);
                            sNotzReminders.launchReminderMenu(data.get(position).getNote(), data.get(position).getNoteID(), item.getContext());
                            break;
                        case 3:
                            Common.setNote(this.data.get(position).getNote());
                            new QRCodeUtils(this.data.get(position).getNote(), null, (Activity) holder.mRVCard.getContext()).generateQRCode().execute();
                            break;
                        case 4:
                            if (Build.VERSION.SDK_INT < 29 && sPermissionUtils.isPermissionDenied(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, holder.mRVCard.getContext())) {
                                sPermissionUtils.requestPermission(new String[] {
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                }, (Activity) item.getContext());
                            } else {
                                if (this.data.get(position).getImageString() != null) {
                                    sUtils.snackBar(holder.mRVCard, holder.mRVCard.getContext().getString(R.string.image_excluded_warning)).show();
                                }
                                DialogEditTextListener.dialogEditText(null, null,
                                        (dialogInterface, i) -> {
                                        }, text -> {
                                            if (text.isEmpty()) {
                                                sUtils.snackBar(holder.mRVCard, holder.mRVCard.getContext().getString(R.string.text_empty)).show();
                                                return;
                                            }
                                            if (!text.endsWith(".txt")) {
                                                text += ".txt";
                                            }
                                            if (text.contains(" ")) {
                                                text = text.replace(" ", "_");
                                            }
                                            if (Build.VERSION.SDK_INT >= 29) {
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
                                                sUtils.create(this.data.get(position).getNote(), new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), text));
                                            }
                                            sUtils.snackBar(holder.mRVCard, holder.mRVCard.getContext().getString(R.string.save_text_message,
                                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + text)).show();
                                        }, -1, (Activity) holder.mRVCard.getContext()).setOnDismissListener(dialogInterface -> {
                                }).show();
                            }
                            break;
                        case 5:
                            String[] sNotzContents = this.data.get(position).getNote().split("\\s+");
                            new MaterialAlertDialogBuilder(holder.mRVCard.getContext())
                                    .setMessage(holder.mRVCard.getContext().getString(R.string.delete_sure_question, sNotzContents.length <= 2 ?
                                            this.data.get(position).getNote() : sNotzContents[0] + " " + sNotzContents[1] + " " + sNotzContents[2] + "..."))
                                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                                    })
                                    .setPositiveButton(R.string.delete, (dialog, which) -> sNotzUtils.deleteNote(this.data.get(position).getNoteID(),
                                            holder.mProgress, holder.mRVCard.getContext()).execute()).show();
                            break;
                    }
                    return false;
                });
                popupMenu.show();
            } else {
                setActionLayout(position);
            }
            return true;
        });
        holder.mRVCard.setCardBackgroundColor(data.get(position).getColorBackground());
        holder.mRVCard.setOnClickListener(v -> {
            if (Common.isWorking()) {
                return;
            }
            Common.setExternalNote(null);
            Common.setNote(this.data.get(position).getNote());
            Common.setID(this.data.get(position).getNoteID());
            Common.setBackgroundColor(this.data.get(position).getColorBackground());
            Common.setTextColor(this.data.get(position).getColorText());
            if (this.data.get(position).getImageString() != null) {
                Common.setImageString(this.data.get(position).getImageString());
            }
            Common.isHiddenNote(this.data.get(position).isHidden());
            Intent editNote = new Intent(v.getContext(), CreateNoteActivity.class);
            v.getContext().startActivity(editNote);
        });
        holder.mShare.setColorFilter(data.get(position).getColorText());
        holder.mDownload.setColorFilter(data.get(position).getColorText());
        holder.mQrCode.setColorFilter(data.get(position).getColorText());
        holder.mReminder.setColorFilter(data.get(position).getColorText());
        holder.mDelete.setColorFilter(data.get(position).getColorText());
        holder.mHidden.setThumbTintList(ColorStateList.valueOf(data.get(position).getColorText()));
        holder.mHidden.setChecked(this.data.get(position).isHidden());
        holder.mQrCode.setOnClickListener(v -> {
            Common.setNote(this.data.get(position).getNote());
            new QRCodeUtils(this.data.get(position).getNote(), null, (Activity) v.getContext()).generateQRCode().execute();
        });
        holder.mShare.setOnClickListener(v -> sNotzUtils.shareNote(this.data.get(position).getNote(),
                this.data.get(position).getImageString(), v.getContext()));
        holder.mDownload.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT < 29 && sPermissionUtils.isPermissionDenied(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, v.getContext())) {
                sPermissionUtils.requestPermission(new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, (Activity) v.getContext());
            } else {
                if (this.data.get(position).getImageString() != null) {
                    sUtils.snackBar(holder.mRVCard, v.getContext().getString(R.string.image_excluded_warning)).show();
                }
                DialogEditTextListener.dialogEditText(null, null,
                        (dialogInterface, i) -> {
                        }, text -> {
                            if (text.isEmpty()) {
                                sUtils.snackBar(holder.mRVCard, v.getContext().getString(R.string.text_empty)).show();
                                return;
                            }
                            if (!text.endsWith(".txt")) {
                                text += ".txt";
                            }
                            if (text.contains(" ")) {
                                text = text.replace(" ", "_");
                            }
                            if (Build.VERSION.SDK_INT >= 29) {
                                try {
                                    ContentValues values = new ContentValues();
                                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, text);
                                    values.put(MediaStore.MediaColumns.MIME_TYPE, "*/*");
                                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                                    Uri uri = v.getContext().getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                                    OutputStream outputStream = v.getContext().getContentResolver().openOutputStream(uri);
                                    outputStream.write(Objects.requireNonNull(this.data.get(position).getNote()).getBytes());
                                    outputStream.close();
                                } catch (IOException ignored) {
                                }
                            } else {
                                sUtils.create(this.data.get(position).getNote(), new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), text));
                            }
                            sUtils.snackBar(holder.mRVCard, v.getContext().getString(R.string.save_text_message,
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + text)).show();
                        }, -1, (Activity) v.getContext()).setOnDismissListener(dialogInterface -> {
                }).show();
            }
        });
        holder.mHidden.setOnClickListener(v -> {
            if (this.data.get(position).isHidden()) {
                sNotzUtils.hideNote(this.data.get(position).getNoteID(),false, holder.mProgress, v.getContext()).execute();
            } else {
                sNotzUtils.hideNote(this.data.get(position).getNoteID(),true, holder.mProgress, v.getContext()).execute();
                sUtils.snackBar(holder.mRVCard, v.getContext().getString(R.string.hidden_note_message)).show();
            }
        });
        holder.mReminder.setOnClickListener(v -> {
            sNotzReminders.setYear(-1);
            sNotzReminders.setMonth(-1);
            sNotzReminders.setDay(-1);
            sNotzReminders.launchReminderMenu(data.get(position).getNote(), data.get(position).getNoteID(), v.getContext());
        });
        holder.mDelete.setOnClickListener(v -> {
            String[] sNotzContents = this.data.get(position).getNote().split("\\s+");
            new MaterialAlertDialogBuilder(v.getContext())
                    .setMessage(v.getContext().getString(R.string.delete_sure_question, sNotzContents.length <= 2 ?
                            this.data.get(position).getNote() : sNotzContents[0] + " " + sNotzContents[1] + " " + sNotzContents[2] + "..."))
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    })
                    .setPositiveButton(R.string.delete, (dialog, which) -> sNotzUtils.deleteNote(this.data.get(position).getNoteID(),
                            holder.mProgress, v.getContext()).execute()).show();
        });
        holder.mDate.setText(DateFormat.getDateTimeInstance().format(this.data.get(position).getTimeStamp()));
        holder.mDate.setTextColor(data.get(position).getColorText());
        // TODO: This should replaced.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.mProgress.setIndeterminateTintList(ColorStateList.valueOf(data.get(position).getColorBackground()));
        }
    }

    private void setActionLayout(int position) {
        notifyItemChanged(mPosition);
        mPosition = position;
        notifyItemChanged(mPosition);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayoutCompat mActionLayout;
        private final AppCompatImageButton mDelete, mDownload, mExpand, mQrCode, mReminder, mShare;
        private final MaterialTextView mContents, mDate;
        private final MaterialCardView mRVCard;
        private final ProgressBar mProgress;
        private final SwitchCompat mHidden;

        public ViewHolder(View view) {
            super(view);
            this.mDelete = view.findViewById(R.id.delete);
            this.mDownload = view.findViewById(R.id.download);
            this.mExpand = view.findViewById(R.id.expand);
            this.mQrCode = view.findViewById(R.id.qr_code);
            this.mReminder = view.findViewById(R.id.reminder);
            this.mShare = view.findViewById(R.id.share);
            this.mActionLayout = view.findViewById(R.id.layout_actions);
            this.mProgress = view.findViewById(R.id.progress);
            this.mContents = view.findViewById(R.id.contents);
            this.mDate = view.findViewById(R.id.date);
            this.mRVCard = view.findViewById(R.id.rv_card);
            this.mHidden = view.findViewById(R.id.hidden);
        }
    }

}