package com.sunilpaulmathew.snotz.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.sNotzColor;

import java.io.File;
import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 21, 2021
 */
public class WidgetChecklistsAdapter extends RecyclerView.Adapter<WidgetChecklistsAdapter.ViewHolder> {

    private final List<File> data;
    private static ClickListener mClickListener;
    public WidgetChecklistsAdapter(List<File> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public WidgetChecklistsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_widgets, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull WidgetChecklistsAdapter.ViewHolder holder, int position) {
        holder.mCard.setCardBackgroundColor(sNotzColor.getAccentColor(holder.mCard.getContext()));
        holder.mTitle.setText(this.data.get(position).getName());
        holder.mTitle.setTextColor(sNotzColor.getTextColor(holder.mTitle.getContext()));
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
        WidgetChecklistsAdapter.mClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

}