package com.emadabel.mydishes;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.emadabel.mydishes.adapter.RecipesAdapter;
import com.emadabel.mydishes.api.DownloaderAsyncTask;
import com.emadabel.mydishes.database.AppDatabase;
import com.emadabel.mydishes.model.Recipe;
import com.emadabel.mydishes.model.RecipeGetResponse;
import com.emadabel.mydishes.model.RecipeSearchResponse;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements DownloaderAsyncTask.DownloaderCallback {

    @BindView(R.id.main_toolbar)
    Toolbar mainToolbar;
    @BindView(R.id.recipe_list_rv)
    RecyclerView recipeListRecyclerView;

    private AppDatabase mDb;
    private Recipe mRecipe;
    private RecipesAdapter mRecipesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mainToolbar);

        mDb = AppDatabase.getInstance(getApplicationContext());

        mRecipesAdapter = new RecipesAdapter(this, null, null);
        recipeListRecyclerView.setHasFixedSize(true);
        recipeListRecyclerView.setAdapter(mRecipesAdapter);

        DownloaderAsyncTask service = new DownloaderAsyncTask(null, null, null, null);
        service.setListener(this);
        service.execute();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            DownloaderAsyncTask service = new DownloaderAsyncTask(query, null, null, null);
            service.setListener(this);
            service.execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
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
        mRecipesAdapter.setRecipeData(recipeSearchResponse.recipes);
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
