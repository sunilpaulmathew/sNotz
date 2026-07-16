package com.sunilpaulmathew.snotz.adapters;

import android.content.res.ColorStateList;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.AppSettings;
import com.sunilpaulmathew.snotz.utils.sNotzWidgets;
import com.sunilpaulmathew.snotz.utils.serializableItems.RestoreNotesEntry;

import java.text.DateFormat;
import java.util.List;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on July 16, 2026
 */
public class RestoreNotesAdapter extends RecyclerView.Adapter<RestoreNotesAdapter.ViewHolder> {

    private final List<RestoreNotesEntry> data;
    private int mExpandPosition = RecyclerView.NO_POSITION;

    public RestoreNotesAdapter(List<RestoreNotesEntry> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public RestoreNotesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_main, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bind();

        holder.mExpand.setOnClickListener(v -> setExpandStatus(holder.getBindingAdapterPosition()));

        holder.mMenu.setOnClickListener(v -> {
            int currentPosition =  holder.getBindingAdapterPosition();
            RestoreNotesEntry notesEntry = this.data.get(currentPosition);
            notesEntry.isSelected(!notesEntry.isSelected());
            notifyItemChanged(currentPosition);
        });

        AppSettings.setSlideInAnimation(holder.mContents, holder.getBindingAdapterPosition());
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

    public List<RestoreNotesEntry> getData() {
        return this.data;
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatImageButton mExpand, mMenu, mLock;
        private final ContentLoadingProgressBar mProgress;
        private final MaterialTextView mContents, mDate;
        private final MaterialCardView mRVCard;

        public ViewHolder(View view) {
            super(view);
            this.mExpand = view.findViewById(R.id.expand);
            this.mMenu = view.findViewById(R.id.menu);
            this.mLock = view.findViewById(R.id.lock);
            this.mProgress = view.findViewById(R.id.progress);
            this.mContents = view.findViewById(R.id.contents);
            this.mDate = view.findViewById(R.id.date);
            this.mRVCard = view.findViewById(R.id.rv_card);
        }

        private void bind() {
            int layoutPosition = getBindingAdapterPosition();
            RestoreNotesEntry notesEntry = data.get(layoutPosition);
            if (notesEntry.isChecklist()) {
                mContents.setText(sNotzWidgets.getWidgetText(notesEntry.getNote()));
            } else {
                mContents.setText(notesEntry.getNote());
            }

            mContents.setTextColor(notesEntry.getColorText());
            mContents.setTextSize(TypedValue.COMPLEX_UNIT_SP, sCommonUtils.getInt("font_size", 18, itemView.getContext()));
            mContents.setTypeface(null, AppSettings.getStyle(itemView.getContext()));
            mContents.setMaxLines(mExpandPosition != layoutPosition ? 1 : Integer.MAX_VALUE);

            mMenu.setImageDrawable(sCommonUtils.getDrawable(notesEntry.isSelected() ? R.drawable.ic_check_box_checked : R.drawable.ic_check_box_unchecked, mMenu.getContext()));

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mContents.getLayoutParams();
            if (mExpandPosition != layoutPosition) {
                mExpand.setImageDrawable(sCommonUtils.getDrawable(R.drawable.ic_expand, itemView.getContext()));
                params.setMargins(getMarginInPx(0), getMarginInPx(0), getMarginInPx(80), getMarginInPx(0));
            } else {
                mExpand.setImageDrawable(sCommonUtils.getDrawable(R.drawable.ic_collapse, itemView.getContext()));
                params.setMargins(getMarginInPx(0), getMarginInPx(30), getMarginInPx(0), getMarginInPx(20));
            }
            mContents.setLayoutParams(params);

            mExpand.setColorFilter(notesEntry.getColorText());
            mMenu.setColorFilter(notesEntry.getColorText());
            mRVCard.setCardBackgroundColor(notesEntry.getColorBackground());
            mRVCard.setStrokeColor(notesEntry.getColorBackground());
            mDate.setText(DateFormat.getDateTimeInstance().format(notesEntry.getTimeStamp()));
            mDate.setTextColor(notesEntry.getColorText());
            mDate.setVisibility(View.VISIBLE);
            mLock.setImageDrawable(sCommonUtils.getDrawable(notesEntry.isChecklist() ? R.drawable.ic_checklist : R.drawable.ic_lock_opened, itemView.getContext()));
            mLock.setColorFilter(notesEntry.getColorText());
            mLock.setVisibility(notesEntry.isChecklist() || notesEntry.isHidden() ? View.VISIBLE : View.GONE);
            mProgress.setIndeterminateTintList(ColorStateList.valueOf(notesEntry.getColorText()));
        }

        private int getMarginInPx(int marginInDp) {
            return  (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    marginInDp,
                    mContents.getContext().getResources().getDisplayMetrics()
            );
        }
    }

}