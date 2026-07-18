package com.sunilpaulmathew.snotz.adapters;

import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.AppSettings;
import com.sunilpaulmathew.snotz.utils.serializableItems.CheckListsEntry;
import com.sunilpaulmathew.snotz.utils.sNotzColor;

import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 10, 2021
 */
public class CheckListAdapter extends RecyclerView.Adapter<CheckListAdapter.ViewHolder> {

    private final List<CheckListsEntry> data;

    public CheckListAdapter(List<CheckListsEntry> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public CheckListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_checklist, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckListAdapter.ViewHolder holder, int position) {
        CheckListsEntry checkListsEntry = this.data.get(position);
        holder.setIsRecyclable(data.size() < 25);
        holder.mCheckBox.setChecked(checkListsEntry.isChecked());
        holder.mTitle.setText(checkListsEntry.getTitle());
        holder.mTitle.setTextColor(sNotzColor.getAppAccentColor(holder.mTitle.getContext()));
        holder.mTitle.setPaintFlags(checkListsEntry.isChecked() ? Paint.STRIKE_THRU_TEXT_FLAG : Paint.LINEAR_TEXT_FLAG);

        holder.mCheckBox.setOnClickListener(v -> {
            CheckListsEntry currentCheckListsEntry = this.data.get(holder.getBindingAdapterPosition());
            currentCheckListsEntry.isChecked(!currentCheckListsEntry.isChecked());
            currentCheckListsEntry.isModified(true);
            holder.mTitle.setPaintFlags(currentCheckListsEntry.isChecked() ? Paint.STRIKE_THRU_TEXT_FLAG : Paint.LINEAR_TEXT_FLAG);
        });

        CompoundButtonCompat.setButtonTintList(holder.mCheckBox, ColorStateList.valueOf(sNotzColor.getAppAccentColor(holder.mCheckBox.getContext())));

        manageData(holder.mTitle, position);

        AppSettings.setSlideInAnimation(holder.mTitle, position);
    }

    private void manageData(AppCompatEditText editText, int position) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s == null) return;
                String original = s.toString();

                if (original.endsWith("\n") && !original.trim().isEmpty()) {
                    String cleanedCurrentText = original.replace("\n", "");
                    editText.setText(null);
                    int insertPosition = position + 1;
                    data.add(insertPosition, new CheckListsEntry(cleanedCurrentText, data.get(position).isChecked(), true));
                    notifyItemInserted(insertPosition);
                    return;
                }

                if (original.contains("\n")) {
                    String cleaned = original.replace("\n", "");
                    editText.setText(cleaned);
                    editText.setSelection(cleaned.length());
                    data.get(position).setTitle(cleaned);
                    data.get(position).isModified(true);
                } else {
                    data.get(position).setTitle(original);
                    data.get(position).isModified(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatEditText mTitle;
        private final MaterialCheckBox mCheckBox;

        public ViewHolder(View view) {
            super(view);
            this.mTitle = view.findViewById(R.id.title);
            this.mCheckBox = view.findViewById(R.id.checkbox);
        }
    }

}