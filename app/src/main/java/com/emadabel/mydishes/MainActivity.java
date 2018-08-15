package com.emadabel.mydishes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.emadabel.mydishes.adapter.RecipesAdapter;
import com.emadabel.mydishes.api.DownloaderAsyncTask;
import com.emadabel.mydishes.api.NetworkState;
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

public class MainActivity extends AppCompatActivity implements DownloaderAsyncTask.DownloaderCallback,
        RecipesAdapter.RecipesAdapterOnClickHandler {

    private static final String RECIPE_LIST_INSTANCE = "recipes";
    private static final String CONNECTIVITY_STATE_INSTANCE = "connectivity";

    @BindView(R.id.main_activity_layout)
    CoordinatorLayout mainActivityLayout;
    @BindView(R.id.main_toolbar)
    Toolbar mainToolbar;
    @BindView(R.id.recipe_list_rv)
    RecyclerView recipeListRecyclerView;
    @BindView(R.id.loading_indicator_pb)
    ProgressBar loadingIndicatorProgressBar;
    @BindView(R.id.offline_frame)
    FrameLayout offlineFrame;
    @BindView(R.id.retry_button)
    Button retryButton;
    @BindView(R.id.adView)
    AdView mAdView;

    private BroadcastReceiver receiver;
    private List<Recipe> recipeList;
    private boolean backPressedTwice;
    private boolean mIsConnected;

    private RecipesAdapter mRecipesAdapter;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (recipeList != null) {
            outState.putParcelableArrayList(RECIPE_LIST_INSTANCE, new ArrayList<>(recipeList));
        }
        outState.putBoolean(CONNECTIVITY_STATE_INSTANCE, mIsConnected);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mainToolbar);

        if (savedInstanceState != null) {
            mIsConnected = savedInstanceState.getBoolean(CONNECTIVITY_STATE_INSTANCE);
            if (savedInstanceState.containsKey(RECIPE_LIST_INSTANCE)) {
                recipeList = savedInstanceState.getParcelableArrayList(RECIPE_LIST_INSTANCE);
            }
        } else {
            mIsConnected = NetworkState.isConnected(this);
        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!mIsConnected && NetworkState.isConnected(context)) {
                    Snackbar.make(mainActivityLayout, R.string.snake_message_connected, Snackbar.LENGTH_SHORT).show();
                } else if (!NetworkState.isConnected(context)) {
                    Snackbar.make(mainActivityLayout, R.string.snake_message_disconnected, Snackbar.LENGTH_INDEFINITE).show();
                }
                mIsConnected = NetworkState.isConnected(context);
            }
        };

        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDataTask();
            }
        });

        MobileAds.initialize(this,
                getString(R.string.admob_test_app_id));

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        mRecipesAdapter = new RecipesAdapter(R.layout.recipe_list, this, this);
        recipeListRecyclerView.setHasFixedSize(true);
        recipeListRecyclerView.setAdapter(mRecipesAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (recipeList != null) {
            showData();
        } else {
            loadDataTask();
        }
    }

    private void loadDataTask() {
        if (!mIsConnected) {
            loadingOrError(View.INVISIBLE, View.VISIBLE);
        } else {
            loadingOrError(View.VISIBLE, View.INVISIBLE);
            DownloaderAsyncTask service = new DownloaderAsyncTask(null, null, null, null);
            service.setListener(this);
            service.execute();
        }
    }

    private void loadingOrError(int invisible, int visible) {
        loadingIndicatorProgressBar.setVisibility(invisible);
        offlineFrame.setVisibility(visible);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
        if (backPressedTwice) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, R.string.exit_message, Toast.LENGTH_SHORT).show();
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
        showData();
    }

    private void showData() {
        loadingIndicatorProgressBar.setVisibility(View.INVISIBLE);
        recipeListRecyclerView.setVisibility(View.VISIBLE);
        offlineFrame.setVisibility(View.INVISIBLE);
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
