package com.sunilpaulmathew.snotz.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.Security;
import com.sunilpaulmathew.snotz.utils.SettingsItems;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzColor;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;

import java.util.ArrayList;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 01, 2021
 */
public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

    private final ArrayList<SettingsItems> data;

    private static ClickListener mClickListener;

    public SettingsAdapter(ArrayList<SettingsItems> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public SettingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_settings, parent, false);
        return new SettingsAdapter.ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsAdapter.ViewHolder holder, int position) {
        holder.mTitle.setText(this.data.get(position).getTitle());
        if (this.data.get(position).getDescription() != null) {
            holder.mDescription.setText(this.data.get(position).getDescription());
            holder.mDescription.setVisibility(View.VISIBLE);
        } else {
            holder.mDescription.setVisibility(View.GONE);
        }
        if (this.data.get(position).getIcon() != null) {
            holder.mIcon.setImageDrawable(this.data.get(position).getIcon());
        } else {
            holder.mIcon.setImageDrawable(null);
        }
        if (!Utils.exist(holder.mTitle.getContext().getFilesDir().getPath() + "/snotz")) {
            if (position == 11 || position == 13) {
                holder.mTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.mDescription.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }
        if (position == 1 || position == 4 || position == 10) {
            holder.mDivider.setVisibility(View.VISIBLE);
        } else {
            holder.mDivider.setVisibility(View.GONE);
        }
        if (position == 2 || position == 3 || position == 7) {
            holder.mChecked.setVisibility(View.VISIBLE);
        } else {
            holder.mChecked.setVisibility(View.GONE);
        }
        if (position == 5 || position == 6) {
            holder.mCircle.setVisibility(View.VISIBLE);
        } else {
            holder.mCircle.setVisibility(View.GONE);
        }
        if (position == 2) {
            holder.mChecked.setImageDrawable(sNotzUtils.getDrawable(Security.isScreenLocked(holder.mChecked.getContext()) ?
                    R.drawable.ic_check_box_checked : R.drawable.ic_check_box_unchecked, holder.mChecked.getContext()));
        } else if (position == 3) {
            holder.mChecked.setImageDrawable(sNotzUtils.getDrawable(Security.isHiddenNotesUnlocked(holder.mChecked.getContext()) ?
                    R.drawable.ic_check_box_checked : R.drawable.ic_check_box_unchecked, holder.mChecked.getContext()));
        } else if (position == 5) {
            holder.mCircle.setCardBackgroundColor(sNotzColor.getAccentColor(holder.mCircle.getContext()));
        } else if (position == 6) {
            holder.mCircle.setCardBackgroundColor(sNotzColor.getTextColor(holder.mCircle.getContext()));
        } else if (position == 7) {
            holder.mChecked.setImageDrawable(sNotzUtils.getDrawable(Utils.getBoolean("allow_images", false,
                    holder.mChecked.getContext()) ? R.drawable.ic_check_box_checked : R.drawable.ic_check_box_unchecked,
                    holder.mChecked.getContext()));
        }
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AppCompatImageButton mChecked, mIcon;
        private final MaterialCardView mCircle;
        private final MaterialTextView mTitle, mDescription;
        private final View mDivider;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.mIcon = view.findViewById(R.id.icon);
            this.mTitle = view.findViewById(R.id.title);
            this.mDescription = view.findViewById(R.id.description);
            this.mChecked = view.findViewById(R.id.checked);
            this.mCircle = view.findViewById(R.id.circle);
            this.mDivider = view.findViewById(R.id.divider);
        }

        @Override
        public void onClick(View view) {
            mClickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        SettingsAdapter.mClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

}