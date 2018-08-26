package com.emadabel.mydishes.ui.activity.search;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.emadabel.mydishes.R;
import com.emadabel.mydishes.ui.adapter.RecipesAdapter;
import com.emadabel.mydishes.data.network.DownloaderAsyncTask;
import com.emadabel.mydishes.data.network.NetworkState;
import com.emadabel.mydishes.firebase.Analytics;
import com.emadabel.mydishes.data.model.Recipe;
import com.emadabel.mydishes.data.model.RecipeGetResponse;
import com.emadabel.mydishes.data.model.RecipeSearchResponse;
import com.emadabel.mydishes.ui.activity.detail.DetailsActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchResultsActivity extends AppCompatActivity implements DownloaderAsyncTask.DownloaderCallback, RecipesAdapter.RecipesAdapterOnClickHandler {

    private static final String SEARCH_QUERY_INSTANCE = "query";
    private static final String SEARCH_FOCUS_INSTANCE = "focus";
    private static final String SEARCH_RESULT_INSTANCE = "result";
    private static final String CONNECTIVITY_STATE_INSTANCE = "connectivity";

    @BindView(R.id.search_activity_layout)
    CoordinatorLayout searchActivityLayout;
    @BindView(R.id.search_toolbar)
    Toolbar searchToolbar;
    @BindView(R.id.search_result_rv)
    RecyclerView searchResultRecyclerView;
    @BindView(R.id.loading_indicator_pb)
    ProgressBar loadingIndicatorProgressBar;
    @BindView(R.id.offline_frame)
    FrameLayout offlineFrame;
    @BindView(R.id.retry_button)
    Button retryButton;
    @BindView(R.id.search_hint_tv)
    TextView searchHintTextView;
    SearchView searchView;

    private BroadcastReceiver receiver;
    private List<Recipe> recipeList;
    private String mQuery;
    private boolean mIsFocused = true;
    private boolean mIsConnected;

    private RecipesAdapter mRecipesAdapter;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SEARCH_QUERY_INSTANCE, mQuery);
        outState.putBoolean(SEARCH_FOCUS_INSTANCE, mIsFocused);
        outState.putBoolean(CONNECTIVITY_STATE_INSTANCE, mIsConnected);
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

        if (savedInstanceState != null) {
            mQuery = savedInstanceState.getString(SEARCH_QUERY_INSTANCE);
            mIsFocused = savedInstanceState.getBoolean(SEARCH_FOCUS_INSTANCE);
            mIsConnected = savedInstanceState.getBoolean(CONNECTIVITY_STATE_INSTANCE);
            if (savedInstanceState.containsKey(SEARCH_RESULT_INSTANCE)) {
                recipeList = savedInstanceState.getParcelableArrayList(SEARCH_RESULT_INSTANCE);
            }
        } else {
            mIsConnected = NetworkState.isConnected(this);
        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!mIsConnected && NetworkState.isConnected(context)) {
                    Snackbar.make(searchActivityLayout, R.string.snake_message_connected, Snackbar.LENGTH_SHORT).show();
                } else if (!NetworkState.isConnected(context)) {
                    Snackbar.make(searchActivityLayout, R.string.snake_message_disconnected, Snackbar.LENGTH_INDEFINITE).show();
                }
                mIsConnected = NetworkState.isConnected(context);
            }
        };

        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsConnected) {
                    showError();
                } else {
                    showLoading();
                    DownloaderAsyncTask service = new DownloaderAsyncTask(mQuery, null, null, null);
                    service.setListener(SearchResultsActivity.this);
                    service.execute();
                }
            }
        });

        mRecipesAdapter = new RecipesAdapter(R.layout.search_list, this, this);
        searchResultRecyclerView.setHasFixedSize(true);
        searchResultRecyclerView.setAdapter(mRecipesAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (recipeList != null) {
            showData();
        } else {
            if (!mIsConnected) {
                showError();
            }
        }
    }

    private void showError() {
        loadingIndicatorProgressBar.setVisibility(View.INVISIBLE);
        searchResultRecyclerView.setVisibility(View.INVISIBLE);
        searchHintTextView.setVisibility(View.INVISIBLE);
        offlineFrame.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(mQuery)) {
            retryButton.setVisibility(View.INVISIBLE);
        } else {
            retryButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            Analytics.logEventSearch(this, query);

            if (!mIsConnected) {
                showError();
            } else {
                showLoading();
                DownloaderAsyncTask service = new DownloaderAsyncTask(query, null, null, null);
                service.setListener(this);
                service.execute();
            }
        }
    }

    private void showLoading() {
        loadingIndicatorProgressBar.setVisibility(View.VISIBLE);
        searchResultRecyclerView.setVisibility(View.INVISIBLE);
        searchHintTextView.setVisibility(View.INVISIBLE);
        offlineFrame.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
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
                recipeList = null;
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
        if (recipeList.size() == 0) {
            searchHintTextView.setText(getString(R.string.no_search_result_message, mQuery));
            loadingIndicatorProgressBar.setVisibility(View.INVISIBLE);
            searchResultRecyclerView.setVisibility(View.INVISIBLE);
            searchHintTextView.setVisibility(View.VISIBLE);
            offlineFrame.setVisibility(View.INVISIBLE);
        } else {
            showData();
            searchResultRecyclerView.smoothScrollToPosition(0);
        }
    }

    private void showData() {
        loadingIndicatorProgressBar.setVisibility(View.INVISIBLE);
        searchResultRecyclerView.setVisibility(View.VISIBLE);
        searchHintTextView.setVisibility(View.INVISIBLE);
        offlineFrame.setVisibility(View.INVISIBLE);
        mRecipesAdapter.setRecipeData(recipeList);
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
