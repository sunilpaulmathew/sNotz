package com.sunilpaulmathew.snotz.utils.dialogs;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.RadioGroup;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.sunilpaulmathew.snotz.R;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on July 13, 2026
 */
public abstract class SortOptionsDialog extends BottomSheetDialog {

    public SortOptionsDialog(Context context) {
        super(context);

        View view = View.inflate(context, R.layout.layout_sort_options, null);

        ChipGroup chipGroupShow = view.findViewById(R.id.chipGroupShow);
        RadioGroup radioGroupSort = view.findViewById(R.id.radioGroupSort);
        SwitchMaterial switchReverse = view.findViewById(R.id.switchReverseOrder);

        int currentShow = sCommonUtils.getInt("show_all", 0, context);
        if (currentShow == 0) chipGroupShow.check(R.id.chipShowAll);
        else if (currentShow == 1) chipGroupShow.check(R.id.chipShowChecklists);
        else if (currentShow == 2) chipGroupShow.check(R.id.chipShowNotes);

        int currentSort = sCommonUtils.getInt("sort_notes", 2, context);
        if (currentSort == 2) radioGroupSort.check(R.id.radioSortDate);
        else if (currentSort == 1) radioGroupSort.check(R.id.radioSortColor);
        else if (currentSort == 0) radioGroupSort.check(R.id.radioSortAlpha);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            view.findViewById(R.id.radioSortColor).setVisibility(View.GONE);
        }

        switchReverse.setChecked(sCommonUtils.getBoolean("reverse_order", false, context));

        chipGroupShow.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int checkedId = checkedIds.get(0);

                int targetShow = 0;
                if (checkedId == R.id.chipShowChecklists) targetShow = 1;
                else if (checkedId == R.id.chipShowNotes) targetShow = 2;

                sCommonUtils.saveInt("show_all", targetShow, context);

                onItemClicked();
            }
        });

        radioGroupSort.setOnCheckedChangeListener((group, checkedId) -> {
            int targetSort = 2;
            if (checkedId == R.id.radioSortColor) targetSort = 1;
            else if (checkedId == R.id.radioSortAlpha) targetSort = 0;

            sCommonUtils.saveInt("sort_notes", targetSort, context);
            onItemClicked();
        });

        switchReverse.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sCommonUtils.saveBoolean("reverse_order", isChecked, context);

            onItemClicked();
        });

        setContentView(view);
        show();
    }

    public abstract void onItemClicked();

}