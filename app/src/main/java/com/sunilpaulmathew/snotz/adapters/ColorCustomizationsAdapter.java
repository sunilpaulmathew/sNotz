package com.sunilpaulmathew.snotz.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.CheckListItems;
import com.sunilpaulmathew.snotz.utils.sNotzColor;

import java.util.ArrayList;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on February 25, 2023
 */
public class ColorCustomizationsAdapter extends RecyclerView.Adapter<ColorCustomizationsAdapter.ViewHolder> {

    private final ArrayList<CheckListItems> mData;
    private static ClickListener mClickListener;

    public ColorCustomizationsAdapter(ArrayList<CheckListItems> data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public ColorCustomizationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_color_customizations, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorCustomizationsAdapter.ViewHolder holder, int position) {
        holder.mText.setText(this.mData.get(position).getTitle());
        holder.mCircle.setCardBackgroundColor(sNotzColor.getDefaultColor(position, holder.mCircle.getContext()));
    }

    @Override
    public int getItemCount() {
        return this.mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final MaterialCardView mCircle;
        private final MaterialTextView mText;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.mText = view.findViewById(R.id.text);
            this.mCircle = view.findViewById(R.id.circle);
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