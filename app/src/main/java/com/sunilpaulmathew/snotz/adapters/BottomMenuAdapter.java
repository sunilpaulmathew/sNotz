package com.sunilpaulmathew.snotz.adapters;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.serializableItems.MenuEntry;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on July 13, 2026
 */
public class BottomMenuAdapter extends RecyclerView.Adapter<BottomMenuAdapter.ViewHolder> {

    private final List<MenuEntry> data;
    private final OnItemClickListener listener;

    public BottomMenuAdapter(List<MenuEntry> items, OnItemClickListener listener) {
        this.data = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_bottom_menu, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuEntry item = this.data.get(position);
        holder.title.setText(item.getTile());
        if (item.isSwitch()) {
            holder.enable.setVisibility(VISIBLE);
            holder.enable.setChecked(item.isEnabled());
        } else {
            holder.enable.setVisibility(GONE);
        }

        if (this.data.get(position).getDrawable() != Integer.MIN_VALUE) {
            holder.icon.setImageDrawable(sCommonUtils.getDrawable(this.data.get(position).getDrawable(), holder.icon.getContext()));
            holder.icon.setVisibility(VISIBLE);
        } else {
            holder.icon.setVisibility(GONE);
        }

        if (this.data.get(position).getDescription() != null) {
            holder.description.setText(item.getDescription());
            holder.description.setVisibility(VISIBLE);
        } else {
            holder.description.setVisibility(GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatImageButton icon;
        private final MaterialSwitch enable;
        private final MaterialTextView title, description;
        ViewHolder(@NonNull View view) {
            super(view);
            icon = view.findViewById(R.id.icon);
            enable = view.findViewById(R.id.enable_switch);
            title = view.findViewById(R.id.title);
            description = view.findViewById(R.id.description);

            view.setOnClickListener(v -> listener.onItemClick(data.get(getBindingAdapterPosition()).getID()));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int id);
    }

}