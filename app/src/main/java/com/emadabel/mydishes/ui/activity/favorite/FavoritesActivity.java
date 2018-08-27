package com.emadabel.mydishes.ui.activity.favorite;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.emadabel.mydishes.R;
import com.emadabel.mydishes.ui.adapter.RecipesAdapter;
import com.emadabel.mydishes.data.database.AppDatabase;
import com.emadabel.mydishes.AppExecutors;
import com.emadabel.mydishes.data.model.Recipe;
import com.emadabel.mydishes.ui.activity.detail.DetailActivity;
import com.emadabel.mydishes.ui.widget.UpdatingWidgetService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesActivity extends AppCompatActivity implements RecipesAdapter.RecipesAdapterOnClickHandler {

    @BindView(R.id.favorites_activity_layout)
    CoordinatorLayout favoritesActivityLayout;
    @BindView(R.id.favorites_toolbar)
    Toolbar favoritesToolbar;
    @BindView(R.id.favorites_list_rv)
    RecyclerView favoritesRecyclerView;
    @BindView(R.id.no_favorite_tv)
    TextView noFavoriteTextView;

    private RecipesAdapter mRecipesAdapter;

    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        ButterKnife.bind(this);

        setSupportActionBar(favoritesToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        mRecipesAdapter = new RecipesAdapter(R.layout.search_list, this, this);
        favoritesRecyclerView.setHasFixedSize(true);
        favoritesRecyclerView.setAdapter(mRecipesAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<Recipe> recipes = mRecipesAdapter.getRecipeList();
                        mDb.recipeDao().deleteFavoriteItem(recipes.get(position));
                        UpdatingWidgetService.startActionUpdateWidgets(getBaseContext());
                        Snackbar.make(favoritesActivityLayout, R.string.snake_message_remove_fav, Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        }).attachToRecyclerView(favoritesRecyclerView);

        mDb = AppDatabase.getInstance(getApplicationContext());
        setupViewModel();
    }

    private void setupViewModel() {
        FavoritesViewModel viewModel = ViewModelProviders.of(this).get(FavoritesViewModel.class);
        viewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                if (recipes == null || recipes.size() == 0) {
                    favoritesRecyclerView.setVisibility(View.INVISIBLE);
                    noFavoriteTextView.setVisibility(View.VISIBLE);
                } else {
                    favoritesRecyclerView.setVisibility(View.VISIBLE);
                    noFavoriteTextView.setVisibility(View.INVISIBLE);
                    mRecipesAdapter.setRecipeData(recipes);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(String rId, String recipeTitle) {
        Intent intent = new Intent(FavoritesActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivity.DETAILS_RECIPE_ID_EXTRA, rId);
        intent.putExtra(DetailActivity.DETAILS_RECIPE_TITLE_EXTRA, recipeTitle);
        startActivity(intent);
    }
}
