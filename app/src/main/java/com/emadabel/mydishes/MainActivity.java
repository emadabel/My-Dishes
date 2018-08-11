package com.emadabel.mydishes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.emadabel.mydishes.adapter.RecipesAdapter;
import com.emadabel.mydishes.api.DownloaderAsyncTask;
import com.emadabel.mydishes.database.AppDatabase;
import com.emadabel.mydishes.model.Recipe;
import com.emadabel.mydishes.model.RecipeGetResponse;
import com.emadabel.mydishes.model.RecipeSearchResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements DownloaderAsyncTask.DownloaderCallback {

    private static final String RECIPE_LIST_EXTRA = "recipes";

    @BindView(R.id.main_toolbar)
    Toolbar mainToolbar;
    @BindView(R.id.recipe_list_rv)
    RecyclerView recipeListRecyclerView;

    private List<Recipe> recipeList;

    private AppDatabase mDb;
    private Recipe mRecipe;
    private RecipesAdapter mRecipesAdapter;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (recipeList != null) {
            outState.putParcelableArrayList(RECIPE_LIST_EXTRA, new ArrayList<>(recipeList));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mainToolbar);

        //mDb = AppDatabase.getInstance(getApplicationContext());

        mRecipesAdapter = new RecipesAdapter(R.layout.recipe_list, this, null);
        recipeListRecyclerView.setHasFixedSize(true);
        recipeListRecyclerView.setAdapter(mRecipesAdapter);

        if (savedInstanceState != null) {
            recipeList = savedInstanceState.getParcelableArrayList(RECIPE_LIST_EXTRA);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (recipeList != null) {
            mRecipesAdapter.setRecipeData(recipeList);
            return;
        }
        DownloaderAsyncTask service = new DownloaderAsyncTask(null, null, null, null);
        service.setListener(this);
        service.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onClick(View v) {
        if (v.getId() == posterImageView.getId()) {
            RecipeEntry entry = new RecipeEntry(mRecipe.getPublisher(), mRecipe.getF2fUrl(),
                    mRecipe.getIngredients(), mRecipe.getSourceUrl(), mRecipe.getRecipeId(),
                    mRecipe.getImageUrl(), mRecipe.getSocialRank(), mRecipe.getPublisherUrl(),
                    mRecipe.getTitle());
            mDb.recipeDao().insertFavoriteItem(entry);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    List<RecipeEntry> recipeEntries = mDb.recipeDao().loadAllFavorites();
                    final RecipeEntry firstEntry = recipeEntries.get(0);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            titleTextView.setText(firstEntry.getTitle());
                        }
                    });
                }
            }, 3000);
        }
    }*/

    @Override
    public void onRecipesFetched(RecipeSearchResponse recipeSearchResponse) {
        recipeList = recipeSearchResponse.recipes;
        mRecipesAdapter.setRecipeData(recipeList);
        /*String rId = recipeSearchResponse.recipes.get(0).getRecipeId();
        DownloaderAsyncTask service = new DownloaderAsyncTask(null, null, null, rId);
        service.setListener(this);
        service.execute();*/
    }

    @Override
    public void onRecipeFetched(RecipeGetResponse recipeResponse) {
        /*mRecipe = recipeResponse.getRecipe();
        Glide.with(this)
                .load(mRecipe.getImageUrl())
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))

                .into(posterImageView);*/
    }

}
