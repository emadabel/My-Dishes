package com.emadabel.mydishes.data.network;

import android.os.AsyncTask;

import com.emadabel.mydishes.data.model.RecipeGetResponse;
import com.emadabel.mydishes.data.model.RecipeSearchResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// AsyncTask here is redundant, using here for capstone rubric
// instead we should use Downloader class directly
public class DownloaderAsyncTask extends AsyncTask<Void, Void, Void> {

    private String query;
    private String sort;
    private Integer page;
    private String recipeId;

    private DownloaderCallback mListener;

    public DownloaderAsyncTask(String query, String sort, Integer page, String recipeId) {
        this.query = query;
        this.sort = sort;
        this.page = page;
        this.recipeId = recipeId;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        if (recipeId == null) {
            FoodClient foodClient = FoodClient.getInstance();
            Call<RecipeSearchResponse> call = foodClient.getFoodService().listRecipes(query, sort, page);
            call.enqueue(new Callback<RecipeSearchResponse>() {
                @Override
                public void onResponse(Call<RecipeSearchResponse> call, retrofit2.Response<RecipeSearchResponse> response) {
                    if (mListener != null) {
                        mListener.onRecipesFetched(response.body());
                    }
                }

                @Override
                public void onFailure(Call<RecipeSearchResponse> call, Throwable t) {

                }
            });
        } else {
            FoodClient foodClient = FoodClient.getInstance();
            Call<RecipeGetResponse> call = foodClient.getFoodService().getRecipe(recipeId);
            call.enqueue(new Callback<RecipeGetResponse>() {
                @Override
                public void onResponse(Call<RecipeGetResponse> call, Response<RecipeGetResponse> response) {
                    if (mListener != null) {
                        mListener.onRecipeFetched(response.body());
                    }
                }

                @Override
                public void onFailure(Call<RecipeGetResponse> call, Throwable t) {

                }
            });
        }

        return null;
    }

    public void setListener(DownloaderCallback listener) {
        this.mListener = listener;
    }

    public interface DownloaderCallback {
        void onRecipesFetched(RecipeSearchResponse recipeSearchResponse);

        void onRecipeFetched(RecipeGetResponse recipeResponse);
    }
}
