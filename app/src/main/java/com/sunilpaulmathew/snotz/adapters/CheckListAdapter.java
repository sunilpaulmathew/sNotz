package com.sunilpaulmathew.snotz.adapters;

import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.CheckListItems;

import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 10, 2021
 */
public class CheckListAdapter extends RecyclerView.Adapter<CheckListAdapter.ViewHolder> {

    private final List<CheckListItems> data;
    public CheckListAdapter(List<CheckListItems> data) {
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
        holder.mCheckBox.setChecked(this.data.get(position).isChecked());
        holder.mTitle.setText(this.data.get(position).getTitle());
        holder.mTitle.setPaintFlags(this.data.get(position).isChecked() ? Paint.STRIKE_THRU_TEXT_FLAG : Paint.LINEAR_TEXT_FLAG);

        holder.mCheckBox.setOnClickListener(v -> {
            this.data.get(position).isChecked(!this.data.get(position).isChecked());
            holder.mTitle.setPaintFlags(this.data.get(position).isChecked() ? Paint.STRIKE_THRU_TEXT_FLAG : Paint.LINEAR_TEXT_FLAG);
        });

        manageData(holder.mTitle, position);
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
                if (s != null && !s.toString().isEmpty() && !s.toString().equals("")) {
                    if (s.toString().equals("\n")) {
                        editText.setText(null);
                        return;
                    }
                    if (s.toString().endsWith("\n")) {
                        editText.setText(data.get(position).getTitle());
                        if (position == data.size() - 1) {
                            data.add(data.size(), new CheckListItems("", false));
                            editText.clearFocus();
                        }
                    }
                    data.get(position).setTitle(s.toString());
                } else {
                    data.get(position).setTitle("");
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