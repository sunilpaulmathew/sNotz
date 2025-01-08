package com.sunilpaulmathew.snotz.adapters;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.AppSettings;
import com.sunilpaulmathew.snotz.utils.sNotzWidgets;
import com.sunilpaulmathew.snotz.utils.serializableItems.sNotzItems;

import java.util.List;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 19, 2021
 */
public class WidgetAdapter extends RecyclerView.Adapter<WidgetAdapter.ViewHolder> {

    private int mExpandPosition = RecyclerView.NO_POSITION;
    private final List<sNotzItems> data;
    private static ClickListener mClickListener;
    public WidgetAdapter(List<sNotzItems> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public WidgetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_widgets, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull WidgetAdapter.ViewHolder holder, int position) {
        holder.mCard.setStrokeColor(this.data.get(position).getColorBackground());
        holder.mCard.setCardBackgroundColor(this.data.get(position).getColorBackground());
        if (this.data.get(position).isChecklist()) {
            holder.mTitle.setText(sNotzWidgets.getWidgetText(this.data.get(position).getNote()));
        } else {
            holder.mTitle.setText(this.data.get(position).getNote());
        }
        holder.mTitle.setTextColor(this.data.get(position).getColorText());
        holder.mTitle.setMaxLines(mExpandPosition != position ? 1 : Integer.MAX_VALUE);
        holder.mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, sCommonUtils.getInt("font_size", 18, holder.mTitle.getContext()));
        holder.mTitle.setTypeface(null, AppSettings.getStyle(holder.mTitle.getContext()));
        holder.mExpand.setImageDrawable(sCommonUtils.getDrawable(mExpandPosition != position ? R.drawable.ic_expand :
                R.drawable.ic_collapse,holder.mExpand.getContext()));
        holder.mExpand.setOnClickListener(v -> setExpandStatus(position));
        holder.mExpand.setColorFilter(data.get(position).getColorText());
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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AppCompatImageButton mExpand;
        private final MaterialCardView mCard;
        private final MaterialTextView mTitle;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.mExpand = view.findViewById(R.id.expand);
            this.mCard = view.findViewById(R.id.card);
            this.mTitle = view.findViewById(R.id.title);
        }

        @Override
        public void onClick(View view) {
            mClickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        WidgetAdapter.mClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

}