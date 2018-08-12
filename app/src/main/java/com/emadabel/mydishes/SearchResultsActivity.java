package com.emadabel.mydishes;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.emadabel.mydishes.adapter.RecipesAdapter;
import com.emadabel.mydishes.api.DownloaderAsyncTask;
import com.emadabel.mydishes.model.Recipe;
import com.emadabel.mydishes.model.RecipeGetResponse;
import com.emadabel.mydishes.model.RecipeSearchResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchResultsActivity extends AppCompatActivity implements DownloaderAsyncTask.DownloaderCallback, RecipesAdapter.RecipesAdapterOnClickHandler {

    private static final String SEARCH_QUERY_INSTANCE = "query";
    private static final String SEARCH_FOCUS_INSTANCE = "focus";
    private static final String SEARCH_RESULT_INSTANCE = "result";

    @BindView(R.id.search_toolbar)
    Toolbar searchToolbar;
    @BindView(R.id.search_result_rv)
    RecyclerView searchResultRecyclerView;
    SearchView searchView;

    private List<Recipe> recipeList;
    private String mQuery;
    private boolean mIsFocused = true;
    private RecipesAdapter mRecipesAdapter;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SEARCH_QUERY_INSTANCE, mQuery);
        outState.putBoolean(SEARCH_FOCUS_INSTANCE, mIsFocused);
        if (recipeList != null) {
            outState.putParcelableArrayList(SEARCH_RESULT_INSTANCE, new ArrayList<>(recipeList));
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        ButterKnife.bind(this);

        setSupportActionBar(searchToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mRecipesAdapter = new RecipesAdapter(R.layout.search_list, this, this);
        searchResultRecyclerView.setHasFixedSize(true);
        searchResultRecyclerView.setAdapter(mRecipesAdapter);

        if (savedInstanceState != null) {
            mQuery = savedInstanceState.getString(SEARCH_QUERY_INSTANCE);
            mIsFocused = savedInstanceState.getBoolean(SEARCH_FOCUS_INSTANCE);
            recipeList = savedInstanceState.getParcelableArrayList(SEARCH_RESULT_INSTANCE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (recipeList != null) {
            mRecipesAdapter.setRecipeData(recipeList);
        }
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
        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setFocusable(true);
        searchView.requestFocusFromTouch();
        if (!mIsFocused) {
            searchView.clearFocus();
        }
        if (!TextUtils.isEmpty(mQuery)) {
            searchView.setQuery(mQuery, false);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mQuery = newText;
                return false;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mIsFocused = hasFocus;
            }
        });
        return super.onCreateOptionsMenu(menu);
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
    public void onRecipesFetched(RecipeSearchResponse recipeSearchResponse) {
        recipeList = recipeSearchResponse.recipes;
        mRecipesAdapter.setRecipeData(recipeList);
        searchResultRecyclerView.smoothScrollToPosition(0);
    }

    @Override
    public void onRecipeFetched(RecipeGetResponse recipeResponse) {

    }

    @Override
    public void onClick(String rId, String recipeTitle) {
        Intent intent = new Intent(SearchResultsActivity.this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.DETAILS_RECIPE_ID_EXTRA, rId);
        intent.putExtra(DetailsActivity.DETAILS_RECIPE_TITLE_EXTRA, recipeTitle);
        startActivity(intent);
    }
}
