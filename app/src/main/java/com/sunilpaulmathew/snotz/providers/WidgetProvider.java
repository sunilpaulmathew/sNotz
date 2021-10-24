package com.sunilpaulmathew.snotz.providers;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.activities.StartActivity;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzData;
import com.sunilpaulmathew.snotz.utils.sNotzItems;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;
import com.sunilpaulmathew.snotz.utils.sNotzWidgets;

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
            SharedPreferences.Editor prefs = context.getSharedPreferences("AppWidget", 0).edit();
            prefs.remove("appwidget" + appWidgetId);
            prefs.apply();
        }
    }

    public static void update(AppWidgetManager appWidgetManager, int appWidgetId, Context context) {
        RemoteViews mViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        Intent mIntent = new Intent(context, StartActivity.class);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mIntent, 0);

        if (sNotzWidgets.getChecklistPath(appWidgetId, context) != null) {
            if (Utils.exist(sNotzWidgets.getChecklistPath(appWidgetId, context))) {
                mViews.setTextViewText(R.id.note, sNotzWidgets.getWidgetText(sNotzWidgets.getChecklistPath(appWidgetId, context)));
                mViews.setInt(R.id.layout, "setBackgroundColor", sNotzUtils.getColor(android.R.color.transparent, context));
                mViews.setOnClickPendingIntent(R.id.layout, pendingIntent);
                appWidgetManager.updateAppWidget(appWidgetId, mViews);
            } else if (sNotzWidgets.getNoteID(appWidgetId, context) != -1) {
                for (sNotzItems items : sNotzData.getRawData(context)) {
                    if (items.getNoteID() == sNotzWidgets.getNoteID(appWidgetId, context)) {
                        mViews.setTextViewText(R.id.note, items.getNote());
                        mViews.setTextColor(R.id.note, items.getColorText());
                        mViews.setInt(R.id.layout, "setBackgroundColor", items.getColorBackground());
                        mViews.setOnClickPendingIntent(R.id.layout, pendingIntent);
                        appWidgetManager.updateAppWidget(appWidgetId, mViews);
                    }
                }
            }
        }
    }

}