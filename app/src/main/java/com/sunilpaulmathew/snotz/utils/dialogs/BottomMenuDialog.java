package com.sunilpaulmathew.snotz.utils.dialogs;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.adapters.BottomMenuAdapter;
import com.sunilpaulmathew.snotz.utils.serializableItems.MenuEntry;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on July 13, 2026
 */
public abstract class BottomMenuDialog extends BottomSheetDialog {

    public BottomMenuDialog(List<MenuEntry> menuEntries, Context context) {
        super(context);

        loadDialog(menuEntries, null, null, context);
    }

    public BottomMenuDialog(List<MenuEntry> menuEntries, int icon, String title, Context context) {
        super(context);

        loadDialog(menuEntries, sCommonUtils.getDrawable(icon, context), title, context);
    }

    private void loadDialog(List<MenuEntry> menuEntries, Drawable icon, String title, Context context) {
        View rootView = View.inflate(context, R.layout.layout_bottom_menu, null);
        AppCompatImageButton dialogIcon = rootView.findViewById(R.id.icon);
        MaterialButton cancel = rootView.findViewById(R.id.cancel);
        MaterialTextView dialogTitle = rootView.findViewById(R.id.title);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);

        if (icon != null) {
            dialogIcon.setImageDrawable(icon);
            dialogIcon.setVisibility(VISIBLE);
        } else {
            dialogIcon.setVisibility(GONE);
        }

        if (title != null) {
            dialogTitle.setText(title);
            dialogTitle.setVisibility(VISIBLE);
        } else {
            dialogTitle.setVisibility(GONE);
        }

        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(new BottomMenuAdapter(menuEntries, id -> {
            onMenuItemClicked(id);
            dismiss();
        }));

        cancel.setOnClickListener(v -> dismiss());
        setContentView(rootView);
        show();
    }

    public abstract void onMenuItemClicked(int menuID);

}