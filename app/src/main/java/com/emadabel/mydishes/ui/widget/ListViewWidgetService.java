package com.emadabel.mydishes.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.emadabel.mydishes.ui.activity.detail.DetailsActivity;
import com.emadabel.mydishes.R;
import com.emadabel.mydishes.data.database.AppDatabase;
import com.emadabel.mydishes.data.model.Recipe;

import java.util.List;

public class ListViewWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new AppWidgetListView(this.getApplicationContext());
    }

    class AppWidgetListView implements RemoteViewsService.RemoteViewsFactory {

        private List<Recipe> recipeList;
        private Context context;

        public AppWidgetListView(Context context) {
            this.context = context;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            recipeList = AppDatabase.getInstance(context).recipeDao().loadFavoritesForWidget();
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            if (recipeList == null) return 0;
            return recipeList.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);

            views.setTextViewText(R.id.recipe_name_tv, recipeList.get(position).getTitle());

            Intent fillIntent = new Intent();
            fillIntent.putExtra(DetailsActivity.DETAILS_RECIPE_ID_EXTRA, recipeList.get(position).getRecipeId());
            fillIntent.putExtra(DetailsActivity.DETAILS_RECIPE_TITLE_EXTRA, recipeList.get(position).getTitle());
            views.setOnClickFillInIntent(R.id.parentView, fillIntent);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
