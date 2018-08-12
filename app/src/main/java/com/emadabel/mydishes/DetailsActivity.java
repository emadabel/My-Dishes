package com.emadabel.mydishes;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.emadabel.mydishes.api.DownloaderAsyncTask;
import com.emadabel.mydishes.model.Recipe;
import com.emadabel.mydishes.model.RecipeGetResponse;
import com.emadabel.mydishes.model.RecipeSearchResponse;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements DownloaderAsyncTask.DownloaderCallback {

    public static final String DETAILS_RECIPE_ID_EXTRA = "recipe_id";
    public static final String DETAILS_RECIPE_TITLE_EXTRA = "recipe_title";

    @BindView(R.id.details_collapsing_layout)
    CollapsingToolbarLayout detailsCollapsingLayout;
    @BindView(R.id.details_toolbar)
    Toolbar detailsToolbar;
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

    private Recipe recipeDetails;

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

        String rId = getIntent().getStringExtra(DETAILS_RECIPE_ID_EXTRA);
        String title = getIntent().getStringExtra(DETAILS_RECIPE_TITLE_EXTRA);

        detailsCollapsingLayout.setTitle(title);

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

        DownloaderAsyncTask service = new DownloaderAsyncTask(null, null, null, rId);
        service.setListener(this);
        service.execute();
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

    }

    @Override
    public void onRecipeFetched(RecipeGetResponse recipeResponse) {
        recipeDetails = recipeResponse.getRecipe();
        String posterUrl = recipeDetails.getImageUrl();
        String publisher = recipeDetails.getPublisher();
        double rank = recipeDetails.getSocialRank();

        Glide.with(this)
                .load(posterUrl)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(recipePosterImageView);

        ranking.setRating((float) (rank * 0.05));

        ingredientsListView.setAdapter(new ArrayAdapter<>(this, R.layout.ingredients_list, R.id.ingredient_item_tv, recipeDetails.getIngredients()));
        ingredientsListView.setExpanded(true);

        publisherTextView.setText(publisher);
    }
}
