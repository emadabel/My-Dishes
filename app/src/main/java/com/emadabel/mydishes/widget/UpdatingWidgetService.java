package com.emadabel.mydishes.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.emadabel.mydishes.R;
import com.emadabel.mydishes.database.AppDatabase;
import com.emadabel.mydishes.database.AppExecutors;
import com.emadabel.mydishes.model.Recipe;

import java.util.List;

public class UpdatingWidgetService extends IntentService {

    public static final String ACTION_UPDATE_ALL_WIDGETS = "com.emadabel.mydishes.action.update_widgets";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *
     */
    public UpdatingWidgetService() {
        super("UpdatingWidgetService");
    }

    public static void startActionUpdateWidgets(Context context) {
        Intent intent = new Intent(context, UpdatingWidgetService.class);
        intent.setAction(ACTION_UPDATE_ALL_WIDGETS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_ALL_WIDGETS.equals(action)) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        List<Recipe> recipeList = AppDatabase.getInstance(UpdatingWidgetService.this).recipeDao().loadFavoritesForWidget();

                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(UpdatingWidgetService.this);
                        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(UpdatingWidgetService.this, FavoritesWidgetProvider.class));
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_favorits_lv);
                        FavoritesWidgetProvider.updateRecipeWidgets(UpdatingWidgetService.this, appWidgetManager, recipeList, appWidgetIds);
                    }
                });
            }
        }
    }
}
