package com.sunilpaulmathew.snotz.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.sNotzItems;

import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 19, 2021
 */
public class WidgetNotesAdapter extends RecyclerView.Adapter<WidgetNotesAdapter.ViewHolder> {

    private final List<sNotzItems> data;
    private static ClickListener mClickListener;
    public WidgetNotesAdapter(List<sNotzItems> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public WidgetNotesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_widgets, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull WidgetNotesAdapter.ViewHolder holder, int position) {
        holder.mCard.setCardBackgroundColor(this.data.get(position).getColorBackground());
        holder.mTitle.setText(this.data.get(position).getNote());
        holder.mTitle.setTextColor(this.data.get(position).getColorText());
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final MaterialCardView mCard;
        private final MaterialTextView mTitle;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.mCard = view.findViewById(R.id.card);
            this.mTitle = view.findViewById(R.id.title);
        }

        @Override
        public void onClick(View view) {
            mClickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        WidgetNotesAdapter.mClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

}