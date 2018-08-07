package com.emadabel.mydishes;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.emadabel.mydishes.api.DownloaderAsyncTask;
import com.emadabel.mydishes.database.AppDatabase;
import com.emadabel.mydishes.database.RecipeEntry;
import com.emadabel.mydishes.model.Recipe;
import com.emadabel.mydishes.model.RecipeGetResponse;
import com.emadabel.mydishes.model.RecipeSearchResponse;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements DownloaderAsyncTask.DownloaderCallback, View.OnClickListener {

    @BindView(R.id.imageView)
    ImageView posterImageView;
    @BindView(R.id.textView)
    TextView titleTextView;

    private AppDatabase mDb;
    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mDb = AppDatabase.getInstance(getApplicationContext());

        posterImageView.setOnClickListener(this);

        DownloaderAsyncTask service = new DownloaderAsyncTask(null, null, null, null);
        service.setListener(this);
        service.execute();
    }

    @Override
    public void onRecipesFetched(RecipeSearchResponse recipeSearchResponse) {
        String rId = recipeSearchResponse.recipes.get(0).getRecipeId();
        DownloaderAsyncTask service = new DownloaderAsyncTask(null, null, null, rId);
        service.setListener(this);
        service.execute();
    }

    @Override
    public void onRecipeFetched(RecipeGetResponse recipeResponse) {
        mRecipe = recipeResponse.getRecipe();
        Glide.with(this)
                .load(mRecipe.getImageUrl())
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))

                .into(posterImageView);
    }

    @Override
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
    }
}
