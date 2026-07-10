package com.sunilpaulmathew.snotz.adapters;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.AppSettings;
import com.sunilpaulmathew.snotz.utils.Security;
import com.sunilpaulmathew.snotz.utils.serializableItems.SettingsEntry;
import com.sunilpaulmathew.snotz.utils.sNotzColor;

import java.util.ArrayList;
import java.util.Objects;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 01, 2021
 */
public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

    private final ArrayList<SettingsEntry> data;

    private static ClickListener mClickListener;

    public SettingsAdapter(ArrayList<SettingsEntry> data) {
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
        SettingsEntry settingsEntry = this.data.get(position);
        int itemID = settingsEntry.getId();

        holder.mTitle.setText(settingsEntry.getTitle());
        holder.mTitle.setTextColor(sNotzColor.getAppAccentColor(holder.mTitle.getContext()));
        if (settingsEntry.isTitle()) {
            holder.mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        } else {
            holder.mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        }
        if (settingsEntry.getDescription() != null) {
            holder.mDescription.setText(settingsEntry.getDescription());
            holder.mDescription.setVisibility(VISIBLE);
        } else {
            holder.mDescription.setVisibility(GONE);
        }
        if (settingsEntry.getIcon() != null) {
            holder.mIcon.setImageDrawable(settingsEntry.getIcon());
            holder.mIcon.setVisibility(VISIBLE);
        } else {
            holder.mIcon.setVisibility(GONE);
        }
        if (itemID == 4 || itemID == 5 || itemID == 6 || itemID == 9 || itemID == 10 || itemID == 15) {
            holder.mChecked.setVisibility(VISIBLE);
        } else {
            holder.mChecked.setVisibility(GONE);
        }
        if (itemID == 3 || itemID == 7 || itemID == 8 || itemID == 11) {
            holder.mCircle.setVisibility(VISIBLE);
        } else {
            holder.mCircle.setVisibility(GONE);
        }
        if (itemID == 3) {
            holder.mCircle.setCardBackgroundColor(sCommonUtils.getInt("app_accent_color", sNotzColor.getAppAccentColor(holder.mCircle.getContext()), holder.mCircle.getContext()));
            holder.mCircle.setStrokeColor(sCommonUtils.getInt("app_accent_color", sNotzColor.getAppAccentColor(holder.mCircle.getContext()), holder.mCircle.getContext()));
        } else if (itemID == 4 || itemID == 5) {
            holder.mChecked.setImageDrawable(sCommonUtils.getDrawable(Security.isScreenLocked(holder.mChecked.getContext()) ?
                    R.drawable.ic_check_box_checked : R.drawable.ic_check_box_unchecked, holder.mChecked.getContext()));
        } else if (itemID == 6) {
            holder.mChecked.setImageDrawable(sCommonUtils.getDrawable(Security.isHiddenNotesUnlocked(holder.mChecked.getContext()) ?
                    R.drawable.ic_check_box_checked : R.drawable.ic_check_box_unchecked, holder.mChecked.getContext()));
        } else if (itemID == 7) {
            holder.mCircle.setCardBackgroundColor(sNotzColor.isRandomColorScheme(holder.mCircle.getContext()) ? sNotzColor.getMaterial3Colors(0, sCommonUtils.getColor(R.color.color_teal, holder.mCircle.getContext()), holder.mCircle.getContext()) : sNotzColor.getAccentColor(holder.mCircle.getContext()));
            holder.mCircle.setStrokeColor(sNotzColor.isRandomColorScheme(holder.mCircle.getContext()) ? sNotzColor.getMaterial3Colors(0, sCommonUtils.getColor(R.color.color_teal, holder.mCircle.getContext()), holder.mCircle.getContext()) : sNotzColor.getAccentColor(holder.mCircle.getContext()));
        } else if (itemID == 8) {
            holder.mCircle.setCardBackgroundColor(sNotzColor.isRandomColorScheme(holder.mCircle.getContext()) ? sNotzColor.getMaterial3Colors(1, sCommonUtils.getColor(R.color.color_white, holder.mCircle.getContext()), holder.mCircle.getContext()) : sNotzColor.getTextColor(holder.mCircle.getContext()));
            holder.mCircle.setStrokeColor(sNotzColor.isRandomColorScheme(holder.mCircle.getContext()) ? sNotzColor.getMaterial3Colors(1, sCommonUtils.getColor(R.color.color_white, holder.mCircle.getContext()), holder.mCircle.getContext()) : sNotzColor.getTextColor(holder.mCircle.getContext()));
        } else if (itemID == 9) {
            holder.mChecked.setImageDrawable(sCommonUtils.getDrawable(sNotzColor.isRandomColorScheme(holder.mChecked.getContext()) ?
                    R.drawable.ic_check_box_checked : R.drawable.ic_check_box_unchecked, holder.mChecked.getContext()));
        } else if (itemID == 10) {
            holder.mChecked.setImageDrawable(sCommonUtils.getDrawable(sCommonUtils.getBoolean("auto_save", false,
                    holder.mChecked.getContext()) ? R.drawable.ic_check_box_checked : R.drawable.ic_check_box_unchecked,
                    holder.mChecked.getContext()));
        } else if (itemID == 11) {
            holder.mCircle.setCardBackgroundColor(sCommonUtils.getInt("checklist_color", sNotzColor.getMaterial3Colors(0, sCommonUtils.getColor(R.color.color_teal, holder.mCircle.getContext()), holder.mCircle.getContext()), holder.mCircle.getContext()));
            holder.mCircle.setStrokeColor(sCommonUtils.getInt("checklist_color", sNotzColor.getMaterial3Colors(0, sCommonUtils.getColor(R.color.color_teal, holder.mCircle.getContext()), holder.mCircle.getContext()), holder.mCircle.getContext()));
        } else if (itemID == 15) {
            holder.mChecked.setImageDrawable(sCommonUtils.getDrawable(sCommonUtils.getBoolean("auto_backup", true,
                            holder.mChecked.getContext()) ? R.drawable.ic_check_box_checked : R.drawable.ic_check_box_unchecked,
                    holder.mChecked.getContext()));
        }
        holder.mChecked.setColorFilter(sNotzColor.getAppAccentColor(holder.mChecked.getContext()));
        AppSettings.setSlideInAnimation(holder.mTitle, position);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public void updateData(ArrayList<SettingsEntry> newData) {
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
                return data.get(oldItemPosition).getId() == newData.get(newItemPosition).getId();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                SettingsEntry oldItem = data.get(oldItemPosition);
                SettingsEntry newItem = newData.get(newItemPosition);

                return oldItem.getTitle().equals(newItem.getTitle()) &&
                        Objects.equals(oldItem.getDescription(), newItem.getDescription());
            }
        });

        this.data.clear();
        this.data.addAll(newData);
        notifyItemRangeChanged(0, getItemCount());
        diffResult.dispatchUpdatesTo(this);
    }

    public void updateItem(int position, String description) {
        if (position >= 0 && position < data.size()) {
            data.get(position).setDescription(description);
            notifyItemChanged(position);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AppCompatImageButton mChecked, mIcon;
        private final MaterialCardView mCircle;
        private final MaterialTextView mTitle, mDescription;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.mIcon = view.findViewById(R.id.icon);
            this.mTitle = view.findViewById(R.id.title);
            this.mDescription = view.findViewById(R.id.description);
            this.mChecked = view.findViewById(R.id.checked);
            this.mCircle = view.findViewById(R.id.circle);
        }

        @Override
        public void onClick(View view) {
            mClickListener.onItemClick(data.get(getBindingAdapterPosition()), getBindingAdapterPosition());
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        SettingsAdapter.mClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(SettingsEntry settingsEntry, int position);
    }

}