package com.sunilpaulmathew.snotz.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.Common;
import com.sunilpaulmathew.snotz.utils.ReminderItems;

import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 04, 2021
 */
public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.ViewHolder> {

    private final List<ReminderItems> data;

    private static ClickListener mClickListener;

    public RemindersAdapter(List<ReminderItems> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public RemindersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_settings, parent, false);
        return new ViewHolder(rowItem);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull RemindersAdapter.ViewHolder holder, int position) {
        holder.mTitle.setText(this.data.get(position).getNote());
        holder.mDescription.setText(Common.getAdjustedTime(this.data.get(position).getYear(), this.data.get(position).getMonth(),
                this.data.get(position).getDay(), this.data.get(position).getHour(), this.data.get(position).getMin()));
        holder.mIcon.setImageDrawable(holder.mIcon.getContext().getDrawable(R.drawable.ic_notifications));
        holder.mTitle.setMaxLines(1);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AppCompatImageButton mIcon;
        private final MaterialTextView mTitle, mDescription;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.mIcon = view.findViewById(R.id.icon);
            this.mTitle = view.findViewById(R.id.title);
            this.mDescription = view.findViewById(R.id.description);
        }
        @Override
        public void onClick(View view) {
            mClickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

}