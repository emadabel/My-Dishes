package com.emadabel.mydishes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.emadabel.mydishes.adapter.RecipesAdapter;
import com.emadabel.mydishes.api.DownloaderAsyncTask;
import com.emadabel.mydishes.model.Recipe;
import com.emadabel.mydishes.model.RecipeGetResponse;
import com.emadabel.mydishes.model.RecipeSearchResponse;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements DownloaderAsyncTask.DownloaderCallback, RecipesAdapter.RecipesAdapterOnClickHandler {

    private static final String RECIPE_LIST_INSTANCE = "recipes";

    @BindView(R.id.main_toolbar)
    Toolbar mainToolbar;
    @BindView(R.id.recipe_list_rv)
    RecyclerView recipeListRecyclerView;
    @BindView(R.id.adView)
    AdView mAdView;

    private List<Recipe> recipeList;
    private boolean backPressedTwice;

    private RecipesAdapter mRecipesAdapter;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (recipeList != null) {
            outState.putParcelableArrayList(RECIPE_LIST_INSTANCE, new ArrayList<>(recipeList));
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mainToolbar);

        MobileAds.initialize(this,
                "ca-app-pub-3940256099942544~3347511713");

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        mRecipesAdapter = new RecipesAdapter(R.layout.recipe_list, this, this);
        recipeListRecyclerView.setHasFixedSize(true);
        recipeListRecyclerView.setAdapter(mRecipesAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(RECIPE_LIST_INSTANCE)) {
            recipeList = savedInstanceState.getParcelableArrayList(RECIPE_LIST_INSTANCE);
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
    public void onBackPressed() {
        if (backPressedTwice) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            backPressedTwice = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressedTwice = false;
                }
            }, 3000);
        }
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
        } else if (item.getItemId() == R.id.action_favorites) {
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRecipesFetched(RecipeSearchResponse recipeSearchResponse) {
        recipeList = recipeSearchResponse.recipes;
        mRecipesAdapter.setRecipeData(recipeList);
    }

    @Override
    public void onRecipeFetched(RecipeGetResponse recipeResponse) {

    }

    @Override
    public void onClick(String rId, String recipeTitle) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.DETAILS_RECIPE_ID_EXTRA, rId);
        intent.putExtra(DetailsActivity.DETAILS_RECIPE_TITLE_EXTRA, recipeTitle);
        startActivity(intent);
    }
}
