package com.emadabel.mydishes.ui.activity.detail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.emadabel.mydishes.R;
import com.emadabel.mydishes.data.network.DownloaderAsyncTask;
import com.emadabel.mydishes.data.network.NetworkState;
import com.emadabel.mydishes.data.database.AppDatabase;
import com.emadabel.mydishes.AppExecutors;
import com.emadabel.mydishes.data.model.Recipe;
import com.emadabel.mydishes.data.model.RecipeGetResponse;
import com.emadabel.mydishes.data.model.RecipeSearchResponse;
import com.emadabel.mydishes.ui.widget.UpdatingWidgetService;
import com.emadabel.mydishes.utilities.InjectorUtils;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class DetailActivity extends AppCompatActivity implements DownloaderAsyncTask.DownloaderCallback {

    public static final String DETAILS_RECIPE_ID_EXTRA = "recipe_id";
    public static final String DETAILS_RECIPE_TITLE_EXTRA = "recipe_title";
    public static final String DETAILS_ID_INSTANCE = "id";
    public static final String DETAILS_RECIPE_INSTANCE = "recipe";
    private static final int DEFAULT_ID = -1;

    @BindView(R.id.details_coordinator_layout)
    CoordinatorLayout detailsCoordinatorLayout;
    @BindView(R.id.details_collapsing_layout)
    CollapsingToolbarLayout detailsCollapsingLayout;
    @BindView(R.id.appbar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.details_toolbar)
    Toolbar detailsToolbar;
    @BindView(R.id.details_container)
    NestedScrollView detailsContainer;
    @BindView(R.id.loading_indicator_pb)
    ProgressBar loadingIndicatorProgressBar;
    @BindView(R.id.offline_frame)
    FrameLayout offlineFrame;
    @BindView(R.id.retry_button)
    Button retryButton;
    @BindView(R.id.recipe_poster_iv)
    ImageView recipePosterImageView;
    @BindView(R.id.source_tv)
    TextView sourceTextView;
    @BindView(R.id.ratingBar)
    RatingBar ranking;
    @BindView(R.id.ingredients_lv)
    ExpandableHeightListView ingredientsListView;
    @BindView(R.id.publisher_tv)
    TextView publisherTextView;
    @BindView(R.id.favorites_fab)
    FloatingActionButton favoritesFab;
    MenuItem shareMenuItem;

    private Recipe recipeDetails;
    private int mId = DEFAULT_ID;

    private AppDatabase mDb;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(DETAILS_ID_INSTANCE, mId);
        if (recipeDetails != null) {
            outState.putParcelable(DETAILS_RECIPE_INSTANCE, recipeDetails);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        setSupportActionBar(detailsToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        mDb = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && (savedInstanceState.containsKey(DETAILS_ID_INSTANCE)
                || savedInstanceState.containsKey(DETAILS_RECIPE_INSTANCE))) {
            mId = savedInstanceState.getInt(DETAILS_ID_INSTANCE, DEFAULT_ID);
            recipeDetails = savedInstanceState.getParcelable(DETAILS_RECIPE_INSTANCE);
        }

        initViews();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(DETAILS_RECIPE_ID_EXTRA)) {
            final String rId = getIntent().getStringExtra(DETAILS_RECIPE_ID_EXTRA);
            String title = getIntent().getStringExtra(DETAILS_RECIPE_TITLE_EXTRA);

            detailsCollapsingLayout.setTitle(title);

            DetailViewModelFactory factory = InjectorUtils.provideDetailViewModelFactory(this, rId);
            DetailViewModel viewModel = ViewModelProviders.of(this, factory).get(DetailViewModel.class);
            viewModel.getRecipe().observe(this, new Observer<Recipe>() {
                @Override
                public void onChanged(@Nullable Recipe recipe) {
                    if (recipe != null) {
                        Timber.d("loading data from favorites and isFavorite=true");
                        recipeDetails = recipe;
                        mId = recipe.getId();
                        populateUi(recipe);
                    } else {
                        Timber.d("Loading data from network and isFavorite=false");
                        if (recipeDetails == null) {
                            if (NetworkState.isConnected(DetailActivity.this)) {
                                loadingIndicatorProgressBar.setVisibility(View.VISIBLE);
                            } else {
                                offlineFrame.setVisibility(View.VISIBLE);
                            }
                            DownloaderAsyncTask service = new DownloaderAsyncTask(null, null, null, rId);
                            service.setListener(DetailActivity.this);
                            service.execute();
                        } else {
                            populateUi(recipeDetails);
                        }
                    }
                }
            });
        }
    }

    private void initViews() {
        appBarLayout.setExpanded(false);
        sourceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(recipeDetails.getSourceUrl()));
                startActivity(browserIntent);
            }
        });

        publisherTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(recipeDetails.getPublisherUrl()));
                startActivity(browserIntent);
            }
        });

        favoritesFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mId == DEFAULT_ID) {
                            Timber.d("Add recipe to favorites");
                            mDb.recipeDao().insertFavoriteItem(recipeDetails);
                            Snackbar.make(detailsCoordinatorLayout, R.string.snake_message_add_fav, Snackbar.LENGTH_SHORT).show();
                        } else {
                            Timber.d("Remove recipe from favorites");
                            recipeDetails.setId(mId);
                            mDb.recipeDao().deleteFavoriteItem(recipeDetails);
                            mId = DEFAULT_ID;
                            Snackbar.make(detailsCoordinatorLayout, R.string.snake_message_remove_fav, Snackbar.LENGTH_SHORT).show();
                        }
                        UpdatingWidgetService.startActionUpdateWidgets(getBaseContext());
                    }
                });
            }
        });

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                if (intent != null && intent.hasExtra(DETAILS_RECIPE_ID_EXTRA)) {
                    String rId = getIntent().getStringExtra(DETAILS_RECIPE_ID_EXTRA);
                    if (NetworkState.isConnected(DetailActivity.this)) {
                        loadingIndicatorProgressBar.setVisibility(View.VISIBLE);
                        offlineFrame.setVisibility(View.INVISIBLE);
                    } else {
                        offlineFrame.setVisibility(View.VISIBLE);
                    }
                    DownloaderAsyncTask service = new DownloaderAsyncTask(null, null, null, rId);
                    service.setListener(DetailActivity.this);
                    service.execute();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);

        shareMenuItem = menu.findItem(R.id.action_share);
        populateUi(recipeDetails);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else if (id == R.id.action_share) {
            ShareCompat.IntentBuilder.from(this)
                    .setChooserTitle(R.string.share_intent_title)
                    .setType("text/plain")
                    .setText(getString(R.string.share_message_format, recipeDetails.getTitle(), recipeDetails.getSourceUrl()))
                    .startChooser();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRecipesFetched(RecipeSearchResponse recipeSearchResponse) {

    }

    @Override
    public void onRecipeFetched(RecipeGetResponse recipeResponse) {
        recipeDetails = recipeResponse.getRecipe();
        populateUi(recipeDetails);
    }

    private void populateUi(Recipe recipe) {
        if (recipe == null) {
            return;
        }

        if (mId == DEFAULT_ID) {
            favoritesFab.setImageResource(R.drawable.ic_fav_off);
        } else {
            favoritesFab.setImageResource(R.drawable.ic_fav_on);
        }

        if (shareMenuItem != null) {
            shareMenuItem.setVisible(true);
        }
        loadingIndicatorProgressBar.setVisibility(View.INVISIBLE);
        detailsContainer.setVisibility(View.VISIBLE);
        appBarLayout.setExpanded(true, false);
        favoritesFab.show();

        String posterUrl = recipe.getImageUrl();
        String publisher = recipe.getPublisher();
        double rank = recipe.getSocialRank();

        Glide.with(this)
                .load(posterUrl)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(recipePosterImageView);

        ranking.setRating((float) (rank * 0.05));

        ingredientsListView.setAdapter(new ArrayAdapter<>(this, R.layout.ingredients_list, R.id.ingredient_item_tv, recipe.getIngredients()));
        ingredientsListView.setExpanded(true);

        publisherTextView.setText(publisher);
    }
}
