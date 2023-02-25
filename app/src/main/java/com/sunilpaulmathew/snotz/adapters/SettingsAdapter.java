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
import com.sunilpaulmathew.snotz.utils.sNotzColor;
import com.sunilpaulmathew.snotz.utils.sNotzData;

import java.util.ArrayList;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;

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
        holder.mTitle.setTextColor(sNotzColor.getAppAccentColor(holder.mTitle.getContext()));
        if (this.data.get(position).getDescription() != null) {
            holder.mDescription.setText(this.data.get(position).getDescription());
            holder.mDescription.setVisibility(View.VISIBLE);
        } else {
            holder.mDescription.setVisibility(View.GONE);
        }
        if (this.data.get(position).getIcon() != null) {
            holder.mIcon.setImageDrawable(this.data.get(position).getIcon());
            holder.mIcon.setColorFilter(sNotzColor.getAppAccentColor(holder.mIcon.getContext()));
        } else {
            holder.mIcon.setImageDrawable(null);
        }
        if ((position == 7 || position == 8) && sNotzColor.isRandomColorScheme(holder.mTitle.getContext())
                || (position == 17 || position == 19) && sNotzData.isNotesEmpty(holder.mTitle.getContext())) {
            holder.mTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mDescription.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.mTitle.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
            holder.mDescription.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
        }
        if (position == 3 || position == 6 || position == 16) {
            holder.mDivider.setVisibility(View.VISIBLE);
        } else {
            holder.mDivider.setVisibility(View.GONE);
        }
        if (position == 4 || position == 5 || position == 9 || position == 10 || position == 11) {
            holder.mChecked.setVisibility(View.VISIBLE);
        } else {
            holder.mChecked.setVisibility(View.GONE);
        }
        if (position == 2 || position == 7 || position == 8 || position == 12) {
            holder.mCircle.setVisibility(View.VISIBLE);
        } else {
            holder.mCircle.setVisibility(View.GONE);
        }
        if (position == 2) {
            holder.mCircle.setCardBackgroundColor(sCommonUtils.getInt("app_accent_color", sCommonUtils.getColor(R.color.color_teal,
                    holder.mCircle.getContext()), holder.mCircle.getContext()));
        } else if (position == 4) {
            holder.mChecked.setImageDrawable(sCommonUtils.getDrawable(Security.isScreenLocked(holder.mChecked.getContext()) ?
                    R.drawable.ic_check_box_checked : R.drawable.ic_check_box_unchecked, holder.mChecked.getContext()));
        } else if (position == 5) {
            holder.mChecked.setImageDrawable(sCommonUtils.getDrawable(Security.isHiddenNotesUnlocked(holder.mChecked.getContext()) ?
                    R.drawable.ic_check_box_checked : R.drawable.ic_check_box_unchecked, holder.mChecked.getContext()));
        } else if (position == 7) {
            holder.mCircle.setCardBackgroundColor(sCommonUtils.getInt("accent_color", sCommonUtils.getColor(R.color.color_teal,
                    holder.mCircle.getContext()), holder.mCircle.getContext()));
        } else if (position == 8) {
            holder.mCircle.setCardBackgroundColor(sCommonUtils.getInt("text_color", sCommonUtils.getColor(R.color.color_white,
                    holder.mCircle.getContext()), holder.mCircle.getContext()));
        } else if (position == 9) {
            holder.mChecked.setImageDrawable(sCommonUtils.getDrawable(sNotzColor.isRandomColorScheme(holder.mChecked.getContext()) ?
                    R.drawable.ic_check_box_checked : R.drawable.ic_check_box_unchecked, holder.mChecked.getContext()));
        } else if (position == 10) {
            holder.mChecked.setImageDrawable(sCommonUtils.getDrawable(sCommonUtils.getBoolean("allow_images", false,
                    holder.mChecked.getContext()) ? R.drawable.ic_check_box_checked : R.drawable.ic_check_box_unchecked,
                    holder.mChecked.getContext()));
        } else if (position == 11) {
            holder.mChecked.setImageDrawable(sCommonUtils.getDrawable(sCommonUtils.getBoolean("auto_save", false,
                    holder.mChecked.getContext()) ? R.drawable.ic_check_box_checked : R.drawable.ic_check_box_unchecked,
                    holder.mChecked.getContext()));
        } else if (position == 12) {
            holder.mCircle.setCardBackgroundColor(sCommonUtils.getInt("checklist_color", sCommonUtils.getColor(R.color.color_white,
                    holder.mCircle.getContext()), holder.mCircle.getContext()));
        }
        holder.mChecked.setColorFilter(sNotzColor.getAppAccentColor(holder.mChecked.getContext()));
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