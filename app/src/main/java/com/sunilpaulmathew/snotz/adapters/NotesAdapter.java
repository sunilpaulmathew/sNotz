package com.sunilpaulmathew.snotz.adapters;

import android.content.res.ColorStateList;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.AppSettings;
import com.sunilpaulmathew.snotz.utils.sNotzWidgets;
import com.sunilpaulmathew.snotz.utils.serializableItems.sNotzEntry;

import java.text.DateFormat;
import java.util.List;
import java.util.Objects;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2020
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private final int spanCount;
    private final List<sNotzEntry> data;
    private int mExpandPosition = RecyclerView.NO_POSITION;

    private final ClickListener clickListener;

    public NotesAdapter(List<sNotzEntry> data, int spanCount, ClickListener clickListener) {
        this.data = data;
        this.spanCount = spanCount;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_main, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bind(spanCount);

        holder.mExpand.setOnClickListener(v -> setExpandStatus(holder.getBindingAdapterPosition()));

        holder.mMenu.setOnClickListener(v -> clickListener.onMenuClick(this.data.get(holder.getBindingAdapterPosition()), holder.getBindingAdapterPosition(), holder.getBindingAdapterPosition() == mExpandPosition));

        AppSettings.setSlideInAnimation(holder.mContents, holder.getBindingAdapterPosition());
    }

    public void updateData(List<sNotzEntry> newData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return data.size();
            }

            @Override
            public int getNewListSize() {
                return newData.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return data.get(oldItemPosition).getNoteID() == newData.get(newItemPosition).getNoteID();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                sNotzEntry oldItem = data.get(oldItemPosition);
                sNotzEntry newItem = newData.get(newItemPosition);

                return Objects.equals(oldItem.toString(), newItem.toString());
            }
        });

        this.data.clear();
        this.data.addAll(newData);
        notifyItemRangeChanged(0, getItemCount());
        diffResult.dispatchUpdatesTo(this);
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
        setExpandStatus(RecyclerView.NO_POSITION);
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

            view.setOnClickListener(v -> clickListener.onItemClick(data.get(getBindingAdapterPosition()), getBindingAdapterPosition(), getBindingAdapterPosition() == mExpandPosition));
        }

        private void bind(int spanCount) {
            int layoutPosition = getBindingAdapterPosition();
            sNotzEntry notesEntry = data.get(layoutPosition);
            if (notesEntry.isChecklist()) {
                mContents.setText(sNotzWidgets.getWidgetText(notesEntry.getNote()));
            } else {
                mContents.setText(notesEntry.getNote());
            }
            mContents.setTextColor(notesEntry.getColorText());
            mContents.setTextSize(TypedValue.COMPLEX_UNIT_SP, sCommonUtils.getInt("font_size", 18, itemView.getContext()));
            mContents.setTypeface(null, AppSettings.getStyle(itemView.getContext()));
            mContents.setMaxLines(mExpandPosition != layoutPosition ? 1 : Integer.MAX_VALUE);
            mExpand.setVisibility(spanCount > 1 ? View.GONE : View.VISIBLE);
            mExpand.setImageDrawable(sCommonUtils.getDrawable(mExpandPosition != layoutPosition ? R.drawable.ic_expand :
                    R.drawable.ic_collapse,itemView.getContext()));
            mExpand.setColorFilter(sCommonUtils.getInt("text_color", sCommonUtils.getColor(R.color.color_white,
                    itemView.getContext()), itemView.getContext()));
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
    }

    public interface ClickListener {
        void onItemClick(sNotzEntry entry, int position, boolean isPositionExpanded);

        void onMenuClick(sNotzEntry entry, int position, boolean isPositionExpanded);
    }

}