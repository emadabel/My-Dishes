package com.emadabel.mydishes.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.widget.RemoteViews;

import com.emadabel.mydishes.ui.activity.detail.DetailsActivity;
import com.emadabel.mydishes.ui.activity.favorite.FavoritesActivity;
import com.emadabel.mydishes.ui.activity.main.MainActivity;
import com.emadabel.mydishes.R;
import com.emadabel.mydishes.data.model.Recipe;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class FavoritesWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                List<Recipe> recipeList, int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.favorites_widget_provider);
        if (recipeList.size() > 0) {
            views.setViewVisibility(R.id.empty_view, View.GONE);
            views.setViewVisibility(R.id.widget_container, View.VISIBLE);
            Intent intent = new Intent(context, ListViewWidgetService.class);
            views.setRemoteAdapter(R.id.appwidget_favorites_lv, intent);

            Intent favoritesIntent = new Intent(context, FavoritesActivity.class);

            PendingIntent favoritesPendingIntent =
                    TaskStackBuilder.create(context)
                            // add all of FavoritesActivity's parents to the stack,
                            // followed by FavoritesActivity itself
                            .addNextIntentWithParentStack(favoritesIntent)
                            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setOnClickPendingIntent(R.id.appwidget_title_tv, favoritesPendingIntent);

            Intent detailsIntent = new Intent(context, DetailsActivity.class);
            //PendingIntent detailsPendingIntent = PendingIntent.getActivity(context, 0, detailsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent detailsPendingIntent =
                    TaskStackBuilder.create(context)
                            // add all of DetailsActivity's parents to the stack,
                            // followed by DetailsActivity itself
                            .addNextIntentWithParentStack(detailsIntent)
                            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.appwidget_favorites_lv, detailsPendingIntent);
        } else {
            views.setViewVisibility(R.id.empty_view, View.VISIBLE);
            views.setViewVisibility(R.id.widget_container, View.GONE);
            Intent appIntent = new Intent(context, MainActivity.class);
            PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.no_favorite_btn, appPendingIntent);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        UpdatingWidgetService.startActionUpdateWidgets(context);
    }

    public static void updateRecipeWidgets(Context context, AppWidgetManager appWidgetManager,
                                           List<Recipe> recipeList, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, recipeList, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

