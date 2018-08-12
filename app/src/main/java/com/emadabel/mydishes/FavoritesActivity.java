package com.emadabel.mydishes;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;

import com.emadabel.mydishes.adapter.RecipesAdapter;
import com.emadabel.mydishes.database.AppDatabase;
import com.emadabel.mydishes.database.AppExecutors;
import com.emadabel.mydishes.model.Recipe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesActivity extends AppCompatActivity implements RecipesAdapter.RecipesAdapterOnClickHandler {

    @BindView(R.id.favorites_toolbar)
    Toolbar favoritesToolbar;
    @BindView(R.id.favorites_list_rv)
    RecyclerView favoritesRecyclerView;

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
            getSupportActionBar().setDisplayShowTitleEnabled(false);
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
                    }
                });
            }
        }).attachToRecyclerView(favoritesRecyclerView);

        mDb = AppDatabase.getInstance(getApplicationContext());
        retriveRecipes();
    }

    private void retriveRecipes() {
        final LiveData<List<Recipe>> recipe = mDb.recipeDao().loadAllFavorites();
        recipe.observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                mRecipesAdapter.setRecipeData(recipes);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(String rId, String recipeTitle) {
        Intent intent = new Intent(FavoritesActivity.this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.DETAILS_RECIPE_ID_EXTRA, rId);
        intent.putExtra(DetailsActivity.DETAILS_RECIPE_TITLE_EXTRA, recipeTitle);
        startActivity(intent);
    }
}
