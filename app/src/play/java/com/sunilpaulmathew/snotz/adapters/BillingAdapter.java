package com.sunilpaulmathew.snotz.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.BillingItems;
import com.sunilpaulmathew.snotz.utils.sNotzColor;

import java.util.ArrayList;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 01, 2021
 */
public class BillingAdapter extends RecyclerView.Adapter<BillingAdapter.ViewHolder> {

    private final ArrayList<BillingItems> data;

    private static ClickListener clickListener;

    public BillingAdapter(ArrayList<BillingItems> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public BillingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_donate, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull BillingAdapter.ViewHolder holder, int position) {
        try {
            holder.mTitle.setText(this.data.get(position).getTitle());
            holder.mTitle.setTextColor(sNotzColor.getAppAccentColor(holder.mTitle.getContext()));
            holder.mIcon.setImageDrawable(this.data.get(position).getIcon());
        } catch (NullPointerException ignored) {}
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AppCompatImageView mIcon;
        private final MaterialTextView mTitle;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.mIcon = view.findViewById(R.id.icon);
            this.mTitle = view.findViewById(R.id.title);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        BillingAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

}