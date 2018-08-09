package com.emadabel.mydishes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.emadabel.mydishes.api.DownloaderAsyncTask;
import com.emadabel.mydishes.model.RecipeGetResponse;
import com.emadabel.mydishes.model.RecipeSearchResponse;

public class SearchResultsActivity extends AppCompatActivity implements DownloaderAsyncTask.DownloaderCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
    }

    @Override
    public void onRecipesFetched(RecipeSearchResponse recipeSearchResponse) {

    }

    @Override
    public void onRecipeFetched(RecipeGetResponse recipeResponse) {

    }
}
