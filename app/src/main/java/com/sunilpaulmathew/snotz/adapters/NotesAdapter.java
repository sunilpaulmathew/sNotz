package com.sunilpaulmathew.snotz.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
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
import androidx.core.content.FileProvider;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.BuildConfig;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.activities.CheckListActivity;
import com.sunilpaulmathew.snotz.activities.CreateNoteActivity;
import com.sunilpaulmathew.snotz.utils.AppSettings;
import com.sunilpaulmathew.snotz.utils.CheckLists;
import com.sunilpaulmathew.snotz.utils.Common;
import com.sunilpaulmathew.snotz.utils.QRCodeUtils;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.dialogs.DeleteChecklistDialog;
import com.sunilpaulmathew.snotz.utils.dialogs.DeleteNoteDialog;
import com.sunilpaulmathew.snotz.utils.dialogs.PermissionDialog;
import com.sunilpaulmathew.snotz.utils.dialogs.SaveAsTextDialog;
import com.sunilpaulmathew.snotz.utils.sNotzItems;
import com.sunilpaulmathew.snotz.utils.sNotzReminders;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;

import java.io.File;
import java.text.DateFormat;
import java.util.List;

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
        if (this.data.get(position).isChecklist()) {
            holder.mContents.setText(CheckLists.getChecklistData(this.data.get(position).getNote()));
        } else {
            holder.mContents.setText(this.data.get(position).getNote());
        }
        holder.mContents.setTextColor(data.get(position).getColorText());
        holder.mContents.setTextSize(TypedValue.COMPLEX_UNIT_SP, sCommonUtils.getInt("font_size", 18, holder.mContents.getContext()));
        holder.mContents.setTypeface(null, AppSettings.getStyle(holder.mContents.getContext()));
        holder.mContents.setMaxLines(mExpandPosition != position ? 1 : Integer.MAX_VALUE);
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
                if (!this.data.get(position).isChecklist()) {
                    menu.add(Menu.NONE, 1, Menu.NONE, holder.mRVCard.getContext().getString(R.string.duplicate)).setIcon(R.drawable.ic_duplicate);
                    menu.add(Menu.NONE, 2, Menu.NONE, holder.mRVCard.getContext().getString(R.string.hidden_note)).setIcon(R.drawable.ic_eye).setCheckable(true)
                            .setChecked(this.data.get(position).isHidden());
                    if (sNotzReminders.isReminderSet(data.get(position).getNoteID(), holder.mReminder.getContext())) {
                        menu.add(Menu.NONE, 3, Menu.NONE, holder.mRVCard.getContext().getString(R.string.reminder_manage)).setIcon(R.drawable.ic_notification_on);
                    } else {
                        menu.add(Menu.NONE, 3, Menu.NONE, holder.mRVCard.getContext().getString(R.string.reminder_set)).setIcon(R.drawable.ic_notification);
                    }
                }
                menu.add(Menu.NONE, 4, Menu.NONE, holder.mRVCard.getContext().getString(R.string.qr_code_generate)).setIcon(R.drawable.ic_qr_code);
                menu.add(Menu.NONE, 5, Menu.NONE, holder.mRVCard.getContext().getString(R.string.save_text)).setIcon(R.drawable.ic_save);
                menu.add(Menu.NONE, 6, Menu.NONE, holder.mRVCard.getContext().getString(R.string.delete)).setIcon(R.drawable.ic_delete);
                popupMenu.setForceShowIcon(true);
                popupMenu.setOnMenuItemClickListener(popupMenuItem -> {
                    switch (popupMenuItem.getItemId()) {
                        case 0:
                            if (this.data.get(position).isChecklist()) {
                                Intent mIntent = new Intent();
                                mIntent.setAction(Intent.ACTION_SEND);
                                mIntent.putExtra(Intent.EXTRA_SUBJECT, new File(data.get(position).getNote()).getName() + "/" + holder.mRVCard.getContext().getString(R.string.shared_by, BuildConfig.VERSION_NAME));
                                mIntent.putExtra(Intent.EXTRA_TEXT, holder.mRVCard.getContext().getString(R.string.shared_by_message, BuildConfig.VERSION_NAME));
                                mIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(holder.mRVCard.getContext(),BuildConfig.APPLICATION_ID + ".provider", new File(data.get(position).getNote())));
                                mIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                mIntent.setType("*/*");
                                Intent shareIntent = Intent.createChooser(mIntent, holder.mRVCard.getContext().getString(R.string.share_with));
                                holder.mRVCard.getContext().startActivity(shareIntent);
                            } else {
                                sNotzUtils.shareNote(this.data.get(position).getNote(), this.data.get(position).getImageString(), holder.mRVCard.getContext());
                            }
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
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && Utils.isPermissionDenied(Manifest.permission.POST_NOTIFICATIONS, item.getContext())) {
                                new PermissionDialog(item.getContext());
                            } else {
                                sNotzReminders.launchReminderMenu(holder.mReminder, data.get(position).getNote(), data.get(position).getNoteID(), item.getContext());
                            }
                            break;
                        case 4:
                            if (this.data.get(position).isChecklist()) {
                                Common.setNote(new File(data.get(position).getNote()).getName());
                                new QRCodeUtils(sFileUtils.read(new File(data.get(position).getNote())), null, (Activity) holder.mRVCard.getContext()).generateQRCode().execute();
                            } else {
                                Common.setNote(this.data.get(position).getNote());
                                new QRCodeUtils(this.data.get(position).getNote(), null, (Activity) holder.mRVCard.getContext()).generateQRCode().execute();
                            }
                            break;
                        case 5:
                            if (Build.VERSION.SDK_INT < 29 && Utils.isPermissionDenied(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, holder.mRVCard.getContext())) {
                                Utils.requestPermission(new String[] {
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                }, (Activity) item.getContext());
                            } else {
                                if (this.data.get(position).isChecklist()) {
                                    CheckLists.setCheckListName(new File(data.get(position).getNote()).getName());
                                    CheckLists.backupCheckList((Activity) holder.mRVCard.getContext());
                                } else {
                                    if (this.data.get(position).getImageString() != null) {
                                        sCommonUtils.snackBar(holder.mRVCard, holder.mRVCard.getContext().getString(R.string.image_excluded_warning)).show();
                                    }
                                    new SaveAsTextDialog(this.data.get(position).getNote(), holder.mRVCard, holder.mRVCard.getContext());
                                }
                            }
                            break;
                        case 6:
                            if (this.data.get(position).isChecklist()) {
                                new DeleteChecklistDialog(new File(this.data.get(position).getNote()), holder.mRVCard.getContext()) {
                                    @Override
                                    public void negativeButtonLister() {
                                    }
                                };
                            } else {
                                new DeleteNoteDialog(this.data.get(position).getNote(), holder.mRVCard.getContext()) {

                                    @Override
                                    public void negativeButtonLister() {
                                    }

                                    @Override
                                    public void positiveButtonLister() {
                                        sNotzUtils.deleteNote(data.get(position).getNoteID(),
                                            holder.mProgress, holder.mRVCard.getContext()).execute();
                                    }
                                };
                            }
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
            if (this.data.get(position).isChecklist()) {
                CheckLists.setCheckListName(new File(this.data.get(position).getNote()).getName());
                Intent createCheckList = new Intent(v.getContext(), CheckListActivity.class);
                v.getContext().startActivity(createCheckList);
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
            if (this.data.get(position).isChecklist()) {
                Common.setNote(new File(data.get(position).getNote()).getName());
                new QRCodeUtils(sFileUtils.read(new File(data.get(position).getNote())), null, (Activity) v.getContext()).generateQRCode().execute();
            } else {
                Common.setNote(this.data.get(position).getNote());
                new QRCodeUtils(this.data.get(position).getNote(), null, (Activity) v.getContext()).generateQRCode().execute();
            }
        });
        holder.mShare.setOnClickListener(v -> {
            if (this.data.get(position).isChecklist()) {
                Intent mIntent = new Intent();
                mIntent.setAction(Intent.ACTION_SEND);
                mIntent.putExtra(Intent.EXTRA_SUBJECT, new File(data.get(position).getNote()).getName() + "/" + v.getContext().getString(R.string.shared_by, BuildConfig.VERSION_NAME));
                mIntent.putExtra(Intent.EXTRA_TEXT, v.getContext().getString(R.string.shared_by_message, BuildConfig.VERSION_NAME));
                mIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(v.getContext(),BuildConfig.APPLICATION_ID + ".provider", new File(data.get(position).getNote())));
                mIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                mIntent.setType("*/*");
                Intent shareIntent = Intent.createChooser(mIntent, v.getContext().getString(R.string.share_with));
                v.getContext().startActivity(shareIntent);
            } else {
                sNotzUtils.shareNote(this.data.get(position).getNote(), this.data.get(position).getImageString(), v.getContext());
            }
        });
        holder.mDuplicate.setOnClickListener(v -> sNotzUtils.addNote(new SpannableStringBuilder(this.data.get(position).getNote()),
                this.data.get(position).getImageString(), this.data.get(position).getColorBackground(), this.data.get(position).getColorText(),
                this.data.get(position).isHidden(), holder.mProgress, v.getContext()));
        holder.mDownload.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT < 29 && Utils.isPermissionDenied(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, v.getContext())) {
                Utils.requestPermission(new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, (Activity) v.getContext());
            } else {
                if (this.data.get(position).isChecklist()) {
                    CheckLists.setCheckListName(new File(data.get(position).getNote()).getName());
                    CheckLists.backupCheckList((Activity) v.getContext());
                } else {
                    if (this.data.get(position).getImageString() != null) {
                        sCommonUtils.snackBar(holder.mRVCard, v.getContext().getString(R.string.image_excluded_warning)).show();
                    }
                    new SaveAsTextDialog(this.data.get(position).getNote(), holder.mRVCard, v.getContext());
                }
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
        holder.mReminder.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && Utils.isPermissionDenied(Manifest.permission.POST_NOTIFICATIONS, v.getContext())) {
                new PermissionDialog(v.getContext());
                return;
            }
            sNotzReminders.launchReminderMenu(holder.mReminder, data.get(position).getNote(),
                    data.get(position).getNoteID(), v.getContext());
        });
        holder.mDelete.setOnClickListener(v -> {
            if (this.data.get(position).isChecklist()) {
                new DeleteChecklistDialog(new File(this.data.get(position).getNote()), v.getContext()) {
                    @Override
                    public void negativeButtonLister() {
                    }
                };
            } else {
                new DeleteNoteDialog(this.data.get(position).getNote(), v.getContext()) {

                    @Override
                    public void negativeButtonLister() {
                    }

                    @Override
                    public void positiveButtonLister() {
                        sNotzUtils.deleteNote(data.get(position).getNoteID(),
                                holder.mProgress, v.getContext()).execute();
                    }
                };
            }
        });
        holder.mDate.setText(DateFormat.getDateTimeInstance().format(this.data.get(position).getTimeStamp()));
        holder.mDate.setTextColor(data.get(position).getColorText());
        holder.mDate.setVisibility(position == mPosition ? View.GONE : View.VISIBLE);
        holder.mLock.setImageDrawable(sCommonUtils.getDrawable(data.get(position).isChecklist() ? R.drawable.ic_checklist : R.drawable.ic_lock_opened, holder.mLock.getContext()));
        holder.mLock.setColorFilter(data.get(position).getColorText());
        holder.mLock.setVisibility(position != mPosition && (this.data.get(position).isChecklist() || this.data.get(position).isHidden()) ? View.VISIBLE : View.GONE);
        holder.mDuplicate.setVisibility(this.data.get(position).isChecklist() ? View.GONE : View.VISIBLE);
        holder.mHidden.setVisibility(this.data.get(position).isChecklist() ? View.GONE : View.VISIBLE);
        holder.mReminder.setVisibility(this.data.get(position).isChecklist() ? View.GONE : View.VISIBLE);
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