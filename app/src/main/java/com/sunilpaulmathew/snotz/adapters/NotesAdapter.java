package com.sunilpaulmathew.snotz.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.activities.NoteActivity;
import com.sunilpaulmathew.snotz.activities.NotificationRequestActivity;
import com.sunilpaulmathew.snotz.utils.AppSettings;
import com.sunilpaulmathew.snotz.utils.QRCodeUtils;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.dialogs.DeleteNoteDialog;
import com.sunilpaulmathew.snotz.utils.dialogs.SaveAsTextDialog;
import com.sunilpaulmathew.snotz.utils.sNotzData;
import com.sunilpaulmathew.snotz.utils.sNotzReminders;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;
import com.sunilpaulmathew.snotz.utils.sNotzWidgets;
import com.sunilpaulmathew.snotz.utils.serializableItems.sNotzEntry;

import java.text.DateFormat;
import java.util.List;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.CommonUtils.sExecutor;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2020
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private final Activity activity;
    private final ActivityResultLauncher<Intent> addItem;
    private final int spanCount;
    private final List<sNotzEntry> data;
    private int mExpandPosition = RecyclerView.NO_POSITION, mPosition = RecyclerView.NO_POSITION;

    public NotesAdapter(List<sNotzEntry> data, int spanCount, ActivityResultLauncher<Intent> addItem, Activity activity) {
        this.data = data;
        this.spanCount = spanCount;
        this.addItem = addItem;
        this.activity = activity;
    }

    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_main, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        sNotzEntry notesEntry = this.data.get(position);
        if (notesEntry.isChecklist()) {
            holder.mContents.setText(sNotzWidgets.getWidgetText(notesEntry.getNote()));
        } else {
            holder.mContents.setText(notesEntry.getNote());
        }
        holder.mContents.setTextColor(notesEntry.getColorText());
        holder.mContents.setTextSize(TypedValue.COMPLEX_UNIT_SP, sCommonUtils.getInt("font_size", 18, holder.mContents.getContext()));
        holder.mContents.setTypeface(null, AppSettings.getStyle(holder.mContents.getContext()));
        holder.mContents.setMaxLines(mExpandPosition != position ? 1 : Integer.MAX_VALUE);
        holder.mActionLayout.setVisibility(position == mPosition ? View.VISIBLE : View.GONE);
        holder.mExpand.setVisibility(spanCount > 1 ? View.GONE : View.VISIBLE);
        holder.mExpand.setImageDrawable(sCommonUtils.getDrawable(mExpandPosition != position ? R.drawable.ic_expand :
                R.drawable.ic_collapse,holder.mExpand.getContext()));
        holder.mExpand.setOnClickListener(v -> setExpandStatus(holder.getBindingAdapterPosition()));
        holder.mExpand.setColorFilter(sCommonUtils.getInt("text_color", sCommonUtils.getColor(R.color.color_white,
                holder.mExpand.getContext()), holder.mExpand.getContext()));
        holder.mExpand.setColorFilter(notesEntry.getColorText());
        holder.mRVCard.setOnLongClickListener(item -> {
            int currentPosition = holder.getBindingAdapterPosition();
            sNotzEntry currentNotesEntry = this.data.get(currentPosition);
            if (Utils.isSmallScreenSize(spanCount, activity)) {
                PopupMenu popupMenu = new PopupMenu(holder.mRVCard.getContext(), holder.mExpand);
                Menu menu = popupMenu.getMenu();
                menu.add(Menu.NONE, 0, Menu.NONE, holder.mRVCard.getContext().getString(R.string.share)).setIcon(R.drawable.ic_share);
                menu.add(Menu.NONE, 1, Menu.NONE, holder.mRVCard.getContext().getString(R.string.duplicate)).setIcon(R.drawable.ic_duplicate);
                if (!currentNotesEntry.isChecklist()) {
                    menu.add(Menu.NONE, 2, Menu.NONE, holder.mRVCard.getContext().getString(R.string.hidden_note)).setIcon(R.drawable.ic_eye).setCheckable(true)
                            .setChecked(currentNotesEntry.isHidden());
                }
                if (sNotzReminders.isReminderSet(currentNotesEntry.getNoteID(), holder.mReminder.getContext())) {
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
                            sNotzUtils.shareNote(currentNotesEntry.getNote(), holder.mRVCard.getContext());
                            break;
                        case 1:
                            addNote(currentPosition, holder.mProgress, item.getContext()).execute();
                            break;
                        case 2:
                            hideNote(currentPosition, holder.mHidden, holder.mProgress, item.getContext()).execute();
                            sCommonUtils.toast(holder.mRVCard.getContext().getString(R.string.hidden_note_message), holder.mRVCard.getContext()).show();
                            break;
                        case 3:
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && Utils.isPermissionDenied(Manifest.permission.POST_NOTIFICATIONS, item.getContext())) {
                                Intent intent = new Intent(item.getContext(), NotificationRequestActivity.class);
                                item.getContext().startActivity(intent);
                            } else {
                                sNotzReminders.launchReminderMenu(holder.mReminder,currentNotesEntry.getNote(), currentNotesEntry.getNoteID(), item.getContext());
                            }
                            break;
                        case 4:
                            new QRCodeUtils(currentNotesEntry.getNote(), null, activity).generateQRCode().execute();
                            break;
                        case 5:
                            if (Build.VERSION.SDK_INT < 29 && Utils.isPermissionDenied(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, holder.mRVCard.getContext())) {
                                Utils.requestPermission(new String[] {
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                }, activity);
                            } else {
                                new SaveAsTextDialog(currentNotesEntry.getNote(), holder.mRVCard.getContext());
                            }
                            break;
                        case 6:
                            deleteNote(currentPosition, holder.mProgress, holder.mRVCard.getContext());
                            break;
                    }
                    return false;
                });
                popupMenu.show();
            } else {
                setActionLayout(currentPosition);
            }
            return true;
        });
        holder.mRVCard.setCardBackgroundColor(notesEntry.getColorBackground());
        holder.mRVCard.setStrokeColor(notesEntry.getColorBackground());
        holder.mRVCard.setOnClickListener(v -> {
            Intent editNote = getEditNoteIntent(holder.getBindingAdapterPosition(), v);
            addItem.launch(editNote);
        });
        holder.mShare.setColorFilter(notesEntry.getColorText());
        holder.mDownload.setColorFilter(notesEntry.getColorText());
        holder.mDuplicate.setColorFilter(notesEntry.getColorText());
        holder.mQrCode.setColorFilter(notesEntry.getColorText());
        holder.mReminder.setColorFilter(notesEntry.getColorText());
        if (sNotzReminders.isReminderSet(notesEntry.getNoteID(), holder.mReminder.getContext())) {
            holder.mReminder.setImageDrawable(sCommonUtils.getDrawable(R.drawable.ic_notification_on, holder.mReminder.getContext()));
        } else {
            holder.mReminder.setImageDrawable(sCommonUtils.getDrawable(R.drawable.ic_notification, holder.mReminder.getContext()));
        }
        holder.mDelete.setColorFilter(notesEntry.getColorText());
        holder.mHidden.setThumbTintList(ColorStateList.valueOf(notesEntry.getColorText()));
        holder.mHidden.setChecked(notesEntry.isHidden());
        holder.mQrCode.setOnClickListener(v -> new QRCodeUtils(notesEntry.getNote(), null, activity).generateQRCode().execute());
        holder.mShare.setOnClickListener(v -> sNotzUtils.shareNote(notesEntry.getNote(), v.getContext()));
        holder.mDuplicate.setOnClickListener(v -> addNote(holder.getBindingAdapterPosition(), holder.mProgress, v.getContext()).execute());
        holder.mDownload.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT < 29 && Utils.isPermissionDenied(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, v.getContext())) {
                Utils.requestPermission(new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, activity);
            } else {
                new SaveAsTextDialog(this.data.get(holder.getBindingAdapterPosition()).getNote(), v.getContext());
            }
        });
        holder.mHidden.setOnClickListener(v -> {
            hideNote(holder.getBindingAdapterPosition(), holder.mHidden, holder.mProgress, v.getContext()).execute();
            sCommonUtils.toast(v.getContext().getString(R.string.hidden_note_message), v.getContext()).show();

        });
        holder.mReminder.setOnClickListener(v -> {
            sNotzEntry currentNotesEntry = this.data.get(holder.getBindingAdapterPosition());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && Utils.isPermissionDenied(Manifest.permission.POST_NOTIFICATIONS, v.getContext())) {
                Intent intent = new Intent(v.getContext(), NotificationRequestActivity.class);
                v.getContext().startActivity(intent);
                return;
            }
            sNotzReminders.launchReminderMenu(holder.mReminder, currentNotesEntry.getNote(),
                    currentNotesEntry.getNoteID(), v.getContext());
        });
        holder.mDelete.setOnClickListener(v -> deleteNote(holder.getBindingAdapterPosition(), holder.mProgress, v.getContext()));
        holder.mDate.setText(DateFormat.getDateTimeInstance().format(notesEntry.getTimeStamp()));
        holder.mDate.setTextColor(notesEntry.getColorText());
        holder.mDate.setVisibility(holder.getBindingAdapterPosition() == mPosition ? View.GONE : View.VISIBLE);
        holder.mLock.setImageDrawable(sCommonUtils.getDrawable(notesEntry.isChecklist() ? R.drawable.ic_checklist : R.drawable.ic_lock_opened, holder.mLock.getContext()));
        holder.mLock.setColorFilter(notesEntry.getColorText());
        holder.mLock.setVisibility(holder.getBindingAdapterPosition() != mPosition && (notesEntry.isChecklist() || notesEntry.isHidden()) ? View.VISIBLE : View.GONE);
        // TODO: This should replaced.
        holder.mProgress.setIndeterminateTintList(ColorStateList.valueOf(notesEntry.getColorText()));
        AppSettings.setSlideInAnimation(holder.itemView, position);
    }

    @NonNull
    private Intent getEditNoteIntent(int position, View v) {
        Intent editNote = new Intent(v.getContext(), NoteActivity.class);
        editNote.putExtra(NoteActivity.NOTE_INTENT, this.data.get(position).getNote());
        editNote.putExtra(NoteActivity.NOTE_ID_INTENT, this.data.get(position).getNoteID());
        editNote.putExtra(NoteActivity.HIDDEN_INTENT, this.data.get(position).isHidden());
        editNote.putExtra(NoteActivity.COLOR_BG_INTENT, this.data.get(position).getColorBackground());
        editNote.putExtra(NoteActivity.COLOR_TXT_INTENT, this.data.get(position).getColorText());
        return editNote;
    }

    private sExecutor addNote(int position, ContentLoadingProgressBar progress, Context context) {
        return new sExecutor() {
            private List<sNotzEntry> mData;

            @Override
            public void onPreExecute() {
                progress.setVisibility(View.VISIBLE);
                mData = sNotzData.getRawData(context);
            }

            @Override
            public void doInBackground() {
                sNotzEntry item = new sNotzEntry(
                        data.get(position).getNote(),
                        data.get(position).getTimeStamp(),
                        data.get(position).isHidden(),
                        data.get(position).getColorBackground(),
                        data.get(position).getColorText(),
                        sNotzUtils.generateNoteID(context)
                );
                mData.add(item);
                data.add(position, item);
                sNotzUtils.updateDataBase(mData, context);
            }

            @Override
            public void onPostExecute() {
                progress.setVisibility(View.GONE);
                notifyItemInserted(position);
                notifyItemRangeChanged(position, getItemCount());
                setActionLayout(RecyclerView.NO_POSITION);
            }
        };
    }

    private void deleteNote(int position, ContentLoadingProgressBar progress, Context context) {
        new DeleteNoteDialog(this.data.get(position).getNote(), context) {

            @Override
            public void negativeButtonLister() {
            }

            @Override
            public void positiveButtonLister() {
                new sExecutor() {
                    private List<sNotzEntry> mData;
                    @Override
                    public void onPreExecute() {
                        progress.setVisibility(View.VISIBLE);
                        mData = sNotzData.getRawData(context);
                    }

                    @Override
                    public void doInBackground() {
                        for (sNotzEntry rawItems : mData) {
                            if (rawItems.getNoteID() == data.get(position).getNoteID()) {
                                mData.remove(rawItems);
                                data.remove(position);
                                break;
                            }
                        }
                        sNotzUtils.updateDataBase(mData, context);
                    }

                    @Override
                    public void onPostExecute() {
                        progress.setVisibility(View.GONE);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());
                        setActionLayout(RecyclerView.NO_POSITION);
                    }
                }.execute();
            }
        };
    }

    private sExecutor hideNote(int position, SwitchMaterial hiddenSwitch, ContentLoadingProgressBar progress, Context context) {
        return new sExecutor() {
            private List<sNotzEntry> mData;
            private sNotzEntry item;
            @Override
            public void onPreExecute() {
                progress.setVisibility(View.VISIBLE);
                mData = sNotzData.getRawData(context);
            }

            @Override
            public void doInBackground() {
                item = new sNotzEntry(
                        data.get(position).getNote(),
                        data.get(position).getTimeStamp(),
                        hiddenSwitch.isChecked(),
                        data.get(position).getColorBackground(),
                        data.get(position).getColorText(),
                        data.get(position).getNoteID()
                );
                for (int i=0; i<mData.size(); i++) {
                    if (mData.get(i).getNoteID() == data.get(position).getNoteID()) {
                        mData.set(i, item);
                        if (sCommonUtils.getBoolean("hidden_note", false, context)) {
                            data.set(position, item);
                        } else {
                            data.remove(position);
                        }
                        break;
                    }
                }
                sNotzUtils.updateDataBase(mData, context);
            }

            @Override
            public void onPostExecute() {
                progress.setVisibility(View.GONE);
                if (sCommonUtils.getBoolean("hidden_note", false, context)) {
                    notifyItemChanged(position, item);
                } else {
                    notifyItemRemoved(position);
                }
                notifyItemRangeChanged(position, getItemCount());
                setActionLayout(RecyclerView.NO_POSITION);
            }
        };
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

    public void reset() {
        setActionLayout(RecyclerView.NO_POSITION);
        setExpandStatus(RecyclerView.NO_POSITION);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatImageButton mDelete, mDownload, mDuplicate, mExpand, mLock, mQrCode, mReminder, mShare;
        private final ConstraintLayout mActionLayout;
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