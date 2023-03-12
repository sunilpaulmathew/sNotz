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
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.activities.CreateNoteActivity;
import com.sunilpaulmathew.snotz.interfaces.EditTextInterface;
import com.sunilpaulmathew.snotz.utils.AppSettings;
import com.sunilpaulmathew.snotz.utils.Common;
import com.sunilpaulmathew.snotz.utils.QRCodeUtils;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzItems;
import com.sunilpaulmathew.snotz.utils.sNotzReminders;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.List;
import java.util.Objects;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.FileUtils.sFileUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2020
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private int mExpandPosition = RecyclerView.NO_POSITION, mPosition = RecyclerView.NO_POSITION;
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
        holder.mContents.setTextColor(data.get(position).getColorText());
        holder.mContents.setTextSize(TypedValue.COMPLEX_UNIT_SP, sCommonUtils.getInt("font_size", 18, holder.mContents.getContext()));
        holder.mContents.setTypeface(null, AppSettings.getStyle(holder.mContents.getContext()));
        holder.mContents.setSingleLine(mExpandPosition != position);
        holder.mActionLayout.setVisibility(position == mPosition ? View.VISIBLE : View.GONE);
        holder.mExpand.setVisibility(Common.getSpanCount() > 1 ? View.GONE : View.VISIBLE);
        holder.mExpand.setImageDrawable(sCommonUtils.getDrawable(mExpandPosition != position ? R.drawable.ic_expand :
                R.drawable.ic_collapse,holder.mExpand.getContext()));
        holder.mExpand.setOnClickListener(v -> setExpandStatus(position));
        holder.mExpand.setColorFilter(sCommonUtils.getInt("text_color", sCommonUtils.getColor(R.color.color_white,
                holder.mExpand.getContext()), holder.mExpand.getContext()));
        holder.mExpand.setColorFilter(data.get(position).getColorText());
        holder.mRVCard.setOnLongClickListener(item -> {
            if (Common.isWorking()) {
                return true;
            }
            if (Utils.isSmallScreenSize((Activity) item.getContext())) {
                PopupMenu popupMenu = new PopupMenu(holder.mRVCard.getContext(), holder.mExpand);
                Menu menu = popupMenu.getMenu();
                menu.add(Menu.NONE, 0, Menu.NONE, holder.mRVCard.getContext().getString(R.string.share)).setIcon(R.drawable.ic_share);
                menu.add(Menu.NONE, 1, Menu.NONE, holder.mRVCard.getContext().getString(R.string.duplicate)).setIcon(R.drawable.ic_duplicate);
                menu.add(Menu.NONE, 2, Menu.NONE, holder.mRVCard.getContext().getString(R.string.hidden_note)).setIcon(R.drawable.ic_eye).setCheckable(true)
                        .setChecked(this.data.get(position).isHidden());
                if (sNotzReminders.isReminderSet(data.get(position).getNoteID(), holder.mReminder.getContext())) {
                    menu.add(Menu.NONE, 3, Menu.NONE, holder.mRVCard.getContext().getString(R.string.reminder_manage)).setIcon(R.drawable.ic_notification_on);
                } else {
                    menu.add(Menu.NONE, 3, Menu.NONE, holder.mRVCard.getContext().getString(R.string.reminder_set)).setIcon(R.drawable.ic_notification);
                }
                menu.add(Menu.NONE, 4, Menu.NONE, holder.mRVCard.getContext().getString(R.string.qr_code_generate)).setIcon(R.drawable.ic_qr_code);
                menu.add(Menu.NONE, 5, Menu.NONE, holder.mRVCard.getContext().getString(R.string.save_text)).setIcon(R.drawable.ic_save);
                menu.add(Menu.NONE, 6, Menu.NONE, holder.mRVCard.getContext().getString(R.string.delete)).setIcon(R.drawable.ic_delete);
                popupMenu.setForceShowIcon(true);
                popupMenu.setOnMenuItemClickListener(popupMenuItem -> {
                    switch (popupMenuItem.getItemId()) {
                        case 0:
                            sNotzUtils.shareNote(this.data.get(position).getNote(), this.data.get(position).getImageString(), holder.mRVCard.getContext());
                            break;
                        case 1:
                            sNotzUtils.addNote(new SpannableStringBuilder(this.data.get(position).getNote()),
                                    this.data.get(position).getImageString(), this.data.get(position).getColorBackground(), this.data.get(position).getColorText(),
                                    this.data.get(position).isHidden(), holder.mProgress, item.getContext());
                            break;
                        case 2:
                            if (this.data.get(position).isHidden()) {
                                sNotzUtils.hideNote(this.data.get(position).getNoteID(),false, holder.mProgress, holder.mRVCard.getContext()).execute();
                            } else {
                                sNotzUtils.hideNote(this.data.get(position).getNoteID(),true, holder.mProgress, holder.mRVCard.getContext()).execute();
                                sCommonUtils.snackBar(holder.mRVCard, holder.mRVCard.getContext().getString(R.string.hidden_note_message)).show();
                            }
                            break;
                        case 3:
                            sNotzReminders.launchReminderMenu(holder.mReminder, data.get(position).getNote(), data.get(position).getNoteID(), item.getContext());
                            break;
                        case 4:
                            Common.setNote(this.data.get(position).getNote());
                            new QRCodeUtils(this.data.get(position).getNote(), null, (Activity) holder.mRVCard.getContext()).generateQRCode().execute();
                            break;
                        case 5:
                            if (Build.VERSION.SDK_INT < 29 && Utils.isPermissionDenied(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, holder.mRVCard.getContext())) {
                                Utils.requestPermission(new String[] {
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                }, (Activity) item.getContext());
                            } else {
                                if (this.data.get(position).getImageString() != null) {
                                    sCommonUtils.snackBar(holder.mRVCard, holder.mRVCard.getContext().getString(R.string.image_excluded_warning)).show();
                                }
                                new EditTextInterface(null, null, (Activity) holder.mRVCard.getContext()) {

                                    @Override
                                    public void positiveButtonLister(Editable s) {
                                        if (s != null && !s.toString().trim().isEmpty()) {
                                            String fileName = s.toString().trim();
                                            if (!fileName.endsWith(".txt")) {
                                                fileName += ".txt";
                                            }
                                            if (fileName.contains(" ")) {
                                                fileName = fileName.replace(" ", "_");
                                            }
                                            if (Build.VERSION.SDK_INT >= 29) {
                                                try {
                                                    ContentValues values = new ContentValues();
                                                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                                                    values.put(MediaStore.MediaColumns.MIME_TYPE, "*/*");
                                                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                                                    Uri uri = holder.mRVCard.getContext().getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                                                    OutputStream outputStream = holder.mRVCard.getContext().getContentResolver().openOutputStream(uri);
                                                    outputStream.write(Objects.requireNonNull(data.get(position).getNote()).getBytes());
                                                    outputStream.close();
                                                } catch (IOException ignored) {
                                                }
                                            } else {
                                                sFileUtils.create(data.get(position).getNote(), new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName));
                                            }
                                            sCommonUtils.snackBar(holder.mRVCard, holder.mRVCard.getContext().getString(R.string.save_text_message,
                                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + fileName)).show();
                                        } else {
                                            sCommonUtils.snackBar(holder.mRVCard, holder.mRVCard.getContext().getString(R.string.text_empty)).show();
                                        }
                                    }
                                }.show();
                            }
                            break;
                        case 6:
                            String[] sNotzContents = this.data.get(position).getNote().split("\\s+");
                            new MaterialAlertDialogBuilder(holder.mRVCard.getContext())
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setTitle(R.string.warning)
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
        holder.mRVCard.setStrokeColor(data.get(position).getColorBackground());
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
        holder.mDuplicate.setColorFilter(data.get(position).getColorText());
        holder.mQrCode.setColorFilter(data.get(position).getColorText());
        holder.mReminder.setColorFilter(data.get(position).getColorText());
        if (sNotzReminders.isReminderSet(data.get(position).getNoteID(), holder.mReminder.getContext())) {
            holder.mReminder.setImageDrawable(sCommonUtils.getDrawable(R.drawable.ic_notification_on, holder.mReminder.getContext()));
        } else {
            holder.mReminder.setImageDrawable(sCommonUtils.getDrawable(R.drawable.ic_notification, holder.mReminder.getContext()));
        }
        holder.mDelete.setColorFilter(data.get(position).getColorText());
        holder.mHidden.setThumbTintList(ColorStateList.valueOf(data.get(position).getColorText()));
        holder.mHidden.setChecked(this.data.get(position).isHidden());
        holder.mQrCode.setOnClickListener(v -> {
            Common.setNote(this.data.get(position).getNote());
            new QRCodeUtils(this.data.get(position).getNote(), null, (Activity) v.getContext()).generateQRCode().execute();
        });
        holder.mShare.setOnClickListener(v -> sNotzUtils.shareNote(this.data.get(position).getNote(),
                this.data.get(position).getImageString(), v.getContext()));
        holder.mDuplicate.setOnClickListener(v -> sNotzUtils.addNote(new SpannableStringBuilder(this.data.get(position).getNote()),
                this.data.get(position).getImageString(), this.data.get(position).getColorBackground(), this.data.get(position).getColorText(),
                this.data.get(position).isHidden(), holder.mProgress, v.getContext()));
        holder.mDownload.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT < 29 && Utils.isPermissionDenied(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, v.getContext())) {
                Utils.requestPermission(new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, (Activity) v.getContext());
            } else {
                if (this.data.get(position).getImageString() != null) {
                    sCommonUtils.snackBar(holder.mRVCard, v.getContext().getString(R.string.image_excluded_warning)).show();
                }
                new EditTextInterface(null, null, (Activity) holder.mRVCard.getContext()) {

                    @Override
                    public void positiveButtonLister(Editable s) {
                        if (s != null && !s.toString().trim().isEmpty()) {
                            String fileName = s.toString().trim();
                            if (!fileName.endsWith(".txt")) {
                                fileName += ".txt";
                            }
                            if (fileName.contains(" ")) {
                                fileName = fileName.replace(" ", "_");
                            }
                            if (Build.VERSION.SDK_INT >= 29) {
                                try {
                                    ContentValues values = new ContentValues();
                                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                                    values.put(MediaStore.MediaColumns.MIME_TYPE, "*/*");
                                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                                    Uri uri = v.getContext().getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                                    OutputStream outputStream = v.getContext().getContentResolver().openOutputStream(uri);
                                    outputStream.write(Objects.requireNonNull(data.get(position).getNote()).getBytes());
                                    outputStream.close();
                                } catch (IOException ignored) {
                                }
                            } else {
                                sFileUtils.create(data.get(position).getNote(), new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName));
                            }
                            sCommonUtils.snackBar(holder.mRVCard, v.getContext().getString(R.string.save_text_message,
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + fileName)).show();
                        } else {
                            sCommonUtils.snackBar(holder.mRVCard, holder.mRVCard.getContext().getString(R.string.text_empty)).show();
                        }
                    }
                }.show();
            }
        });
        holder.mHidden.setOnClickListener(v -> {
            if (this.data.get(position).isHidden()) {
                sNotzUtils.hideNote(this.data.get(position).getNoteID(),false, holder.mProgress, v.getContext()).execute();
            } else {
                sNotzUtils.hideNote(this.data.get(position).getNoteID(),true, holder.mProgress, v.getContext()).execute();
                sCommonUtils.snackBar(holder.mRVCard, v.getContext().getString(R.string.hidden_note_message)).show();
            }
        });
        holder.mReminder.setOnClickListener(v -> sNotzReminders.launchReminderMenu(holder.mReminder, data.get(position).getNote(),
                data.get(position).getNoteID(), v.getContext()));
        holder.mDelete.setOnClickListener(v -> {
            String[] sNotzContents = this.data.get(position).getNote().split("\\s+");
            new MaterialAlertDialogBuilder(v.getContext())
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(R.string.warning)
                    .setMessage(v.getContext().getString(R.string.delete_sure_question, sNotzContents.length <= 2 ?
                            this.data.get(position).getNote() : sNotzContents[0] + " " + sNotzContents[1] + " " + sNotzContents[2] + "..."))
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    })
                    .setPositiveButton(R.string.delete, (dialog, which) -> sNotzUtils.deleteNote(this.data.get(position).getNoteID(),
                            holder.mProgress, v.getContext()).execute()).show();
        });
        holder.mDate.setText(DateFormat.getDateTimeInstance().format(this.data.get(position).getTimeStamp()));
        holder.mDate.setTextColor(data.get(position).getColorText());
        holder.mDate.setVisibility(position == mPosition ? View.GONE : View.VISIBLE);
        holder.mLock.setColorFilter(data.get(position).getColorText());
        holder.mLock.setVisibility(position != mPosition && this.data.get(position).isHidden() ? View.VISIBLE : View.GONE);
        // TODO: This should replaced.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.mProgress.setIndeterminateTintList(ColorStateList.valueOf(data.get(position).getColorText()));
        }
    }

    private void setActionLayout(int position) {
        if (mPosition != position) {
            notifyItemChanged(mPosition);
            mPosition = position;
        } else {
            mPosition = RecyclerView.NO_POSITION;
        }
        notifyItemChanged(position);
    }

    private void setExpandStatus(int position) {
        if (Common.isWorking() || Common.getSpanCount() > 1) {
            return;
        }
        if (mExpandPosition != position) {
            notifyItemChanged(mExpandPosition);
            mExpandPosition = position;
        } else {
            mExpandPosition = RecyclerView.NO_POSITION;
        }
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayoutCompat mActionLayout;
        private final AppCompatImageButton mDelete, mDownload, mDuplicate, mExpand, mLock, mQrCode, mReminder, mShare;
        private final MaterialTextView mContents, mDate;
        private final MaterialCardView mRVCard;
        private final ContentLoadingProgressBar mProgress;
        private final SwitchMaterial mHidden;

        public ViewHolder(View view) {
            super(view);
            this.mDelete = view.findViewById(R.id.delete);
            this.mDownload = view.findViewById(R.id.download);
            this.mDuplicate = view.findViewById(R.id.duplicate);
            this.mExpand = view.findViewById(R.id.expand);
            this.mLock = view.findViewById(R.id.lock);
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