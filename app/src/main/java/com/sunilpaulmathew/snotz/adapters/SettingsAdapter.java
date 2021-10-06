package com.sunilpaulmathew.snotz.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.Common;
import com.sunilpaulmathew.snotz.utils.SettingsItems;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzColor;

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
        holder.mDescription.setText(this.data.get(position).getDescription());
        holder.mIcon.setImageDrawable(this.data.get(position).getIcon());
        if (!Utils.isFingerprintAvailable(holder.mTitle.getContext()) && position == 1) {
            holder.mTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mDescription.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
        if (!Utils.exist(holder.mTitle.getContext().getFilesDir().getPath() + "/snotz")) {
            if (position == 5 || position == 7) {
                holder.mTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.mDescription.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }
        if (position == 1) {
            holder.mCheckBox.setVisibility(View.VISIBLE);
            holder.mCheckBox.setChecked(Utils.getBoolean("use_biometric", false, holder.mCheckBox.getContext()));
            holder.mCheckBox.setOnClickListener(v -> {
                if (Utils.isFingerprintAvailable(holder.mCheckBox.getContext())) {
                    Common.getBiometricPrompt().authenticate(Utils.showBiometricPrompt(v.getContext()));
                } else {
                    Utils.showSnackbar(holder.mCheckBox, holder.mCheckBox.getContext().getString(R.string.biometric_lock_unavailable));
                }
                notifyItemChanged(position);
            });
        } else if (position == 2) {
            holder.mCheckBox.setVisibility(View.VISIBLE);
            holder.mCheckBox.setChecked(Utils.getBoolean("hidden_note", false, holder.mCheckBox.getContext()));
            holder.mCheckBox.setOnClickListener(v -> {
                if (Utils.getBoolean("use_biometric", false, holder.mCheckBox.getContext()) && Utils.isFingerprintAvailable(holder.mCheckBox.getContext())) {
                    Common.isHiddenNote(true);
                    Common.getBiometricPrompt().authenticate(Utils.showBiometricPrompt(v.getContext()));
                } else {
                    Utils.saveBoolean("hidden_note", !Utils.getBoolean("hidden_note", false, v.getContext()), v.getContext());
                    Common.isHiddenNote(false);
                    Utils.reloadUI(v.getContext()).execute();
                }
                notifyItemChanged(position);
            });
        } else if (position == 3) {
            holder.mCircle.setVisibility(View.VISIBLE);
            holder.mCircle.setCardBackgroundColor(sNotzColor.getAccentColor(holder.mCircle.getContext()));
        } else if (position == 4) {
            holder.mCircle.setVisibility(View.VISIBLE);
            holder.mCircle.setCardBackgroundColor(sNotzColor.getTextColor(holder.mCircle.getContext()));
        }
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AppCompatImageButton mIcon;
        private final MaterialCardView mCircle;
        private final MaterialCheckBox mCheckBox;
        private final MaterialTextView mTitle, mDescription;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.mIcon = view.findViewById(R.id.icon);
            this.mTitle = view.findViewById(R.id.title);
            this.mDescription = view.findViewById(R.id.description);
            this.mCheckBox = view.findViewById(R.id.checkbox);
            this.mCircle = view.findViewById(R.id.circle);
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