package com.sunilpaulmathew.snotz.providers;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.activities.StartActivity;
import com.sunilpaulmathew.snotz.utils.sNotzColor;
import com.sunilpaulmathew.snotz.utils.sNotzData;
import com.sunilpaulmathew.snotz.utils.sNotzWidgets;
import com.sunilpaulmathew.snotz.utils.serializableItems.sNotzItems;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 19, 2021
 * Based mainly on the following Stack Overflow discussions
 * Reference 1: https://stackoverflow.com/questions/34588501/how-to-create-an-android-widget-with-options
 * Reference 2: https://stackoverflow.com/questions/6264809/how-to-build-a-simple-android-widget
 */
public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            update(appWidgetManager, appWidgetId, context);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            sCommonUtils.remove("appwidget" + appWidgetId, context);
        }
    }

    public static void update(AppWidgetManager appWidgetManager, int appWidgetId, Context context) {
        for (sNotzItems items : sNotzData.getRawData(context)) {
            int noteId = items.getNoteID();
            if (noteId == sNotzWidgets.getNoteID(appWidgetId, context)) {
                RemoteViews mViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout_notes);
                if (items.isChecklist()) {
                    mViews.setTextViewText(R.id.note, sNotzWidgets.getWidgetText(items.getNote()));
                    mViews.setTextColor(R.id.note, sCommonUtils.getInt("checklist_color", sNotzColor.getMaterial3Colors(
                            0, sCommonUtils.getColor(R.color.color_teal, context), context), context));
                    mViews.setInt(R.id.layout, "setBackgroundColor", android.R.color.transparent);
                } else {
                    mViews.setTextViewText(R.id.note, items.getNote());
                    mViews.setTextColor(R.id.note, items.getColorText());
                    mViews.setInt(R.id.layout, "setBackgroundColor", items.getColorBackground());
                }

                Intent mIntent = new Intent(context, StartActivity.class);
                mIntent.putExtra("noteId", noteId);
                /*
                 * It shouldn't be set to PendingIntent.FLAG_CANCEL_CURRENT as we need to update our widgets occasionally
                 * (once in every 30 min as per https://github.com/sunilpaulmathew/sNotz/blob/1eb52b17b275fc87ea58e371bc4c2f26409a82e7/app/src/main/res/xml/widget_provider.xml#L9)
                 * Probably, use PendingIntent.FLAG_UPDATE_CURRENT?
                 */
                PendingIntent mPendingIntent = PendingIntent.getActivity(context, appWidgetId, mIntent, Build.VERSION.SDK_INT >=
                        android.os.Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0);
                mViews.setOnClickPendingIntent(R.id.layout, mPendingIntent);
                appWidgetManager.updateAppWidget(appWidgetId, mViews);
            }
        }
    }

}