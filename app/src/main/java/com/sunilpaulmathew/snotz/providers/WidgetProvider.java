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
import com.sunilpaulmathew.snotz.utils.Consts;
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

    @SuppressLint("UnspecifiedImmutableFlag")
    public static void update(AppWidgetManager appWidgetManager, int appWidgetId, Context context) {
        RemoteViews mViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        Intent mIntent = new Intent(context, StartActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mIntent, 0);

        if (sNotzWidgets.getChecklistPath(appWidgetId, context) != null) {
            if (Utils.exist(sNotzWidgets.getChecklistPath(appWidgetId, context))) {
                mViews.setTextViewText(R.id.note, sNotzWidgets.getWidgetText(sNotzWidgets.getChecklistPath(appWidgetId, context)));
                mViews.setInt(R.id.layout, "setBackgroundColor", sNotzUtils.getColor(android.R.color.transparent, context));
                mViews.setOnClickPendingIntent(R.id.layout, pendingIntent);
                appWidgetManager.updateAppWidget(appWidgetId, mViews);
            } else if (sNotzWidgets.getNoteID(appWidgetId, context) != -1) {
                for (sNotzItems items : sNotzData.getRawData(context)) {
                    int noteId = items.getNoteID();
                    if (noteId == sNotzWidgets.getNoteID(appWidgetId, context)) {
                        mViews.setTextViewText(R.id.note, items.getNote());
                        mViews.setTextColor(R.id.note, items.getColorText());
                        mViews.setInt(R.id.layout, "setBackgroundColor", items.getColorBackground());

                        Intent noteIntent = new Intent(context, StartActivity.class);

                        // Only passing the id here, we can pass the whole sNotzItems as Serializable
                        // but it will fail if the item is too big (>= 1MB in size)
                        // We can retrieve the item back later by its ID
                        // TODO: Use a better persistence technique so that the Common class is not necessary (it is memory hungry and unreliable)
                        noteIntent.putExtra(Consts.EXTRAS.NOTE_ID, noteId);
                        PendingIntent notePendingIntent = PendingIntent.getActivity(
                                context, noteId, noteIntent, PendingIntent.FLAG_CANCEL_CURRENT
                        );

                        mViews.setOnClickPendingIntent(R.id.layout, notePendingIntent);
                        appWidgetManager.updateAppWidget(appWidgetId, mViews);
                    }
                }
            }
        }
    }

}