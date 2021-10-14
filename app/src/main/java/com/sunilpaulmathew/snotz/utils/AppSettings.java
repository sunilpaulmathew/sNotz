package com.sunilpaulmathew.snotz.utils;

import android.content.Context;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.adapters.SettingsAdapter;

import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 14, 2021
 */
public class AppSettings {

    private static int getFontPosition(Context context) {
        String value = String.valueOf(Utils.getInt("font_size", 18, context));
        for (int i = 0; i < getFonts().length; i++) {
            if (getFonts()[i].contains(value)) {
                return i;
            }
        }
        return 0;
    }

    private static String[] getFonts() {
        return new String[]{"10sp", "11sp", "12sp", "13sp", "14sp", "15sp", "16sp", "17sp", "18sp", "19sp", "20sp",
                "21sp", "22sp", "23sp", "24sp", "25sp"};
    }

    public static void setFontSize(int position, List<SettingsItems> items, SettingsAdapter adapter, Context context) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.font_size)
                .setSingleChoiceItems(getFonts(), getFontPosition(context), (dialog, itemPosition) -> {
                    Utils.saveInt("font_size", Integer.parseInt(getFonts()[itemPosition].replace("sp","")), context);
                    items.set(position, new SettingsItems(context.getString(R.string.font_size), context.getString(R.string.font_size_summary,
                            "" + Integer.parseInt(getFonts()[itemPosition].replace("sp",""))),
                            sNotzUtils.getDrawable(R.drawable.ic_format_size, context), null));
                    adapter.notifyItemChanged(position);
                })
                .setNeutralButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss()).show();
    }

}