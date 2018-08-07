package com.emadabel.mydishes.api;


// Retrofit calling class without using AsyncTask
public class Downloader {

    /*public static void downloadRecipes(final DownloaderCallback downloaderCallback) {

        FoodClient foodClient = FoodClient.getInstance();
        Call<RecipeSearchResponse> call = foodClient.getFoodService().listRecipes(null, null, null);
        call.enqueue(new Callback<RecipeSearchResponse>() {
            @Override
            public void onResponse(Call<RecipeSearchResponse> call, retrofit2.Response<RecipeSearchResponse> response) {
                if (downloaderCallback != null) {
                    downloaderCallback.onRecipesFetched(response.body());
                }
            }

            @Override
            public void onFailure(Call<RecipeSearchResponse> call, Throwable t) {

            }
        });
    }

    public static void getRecipe(final DownloaderCallback downloaderCallback, String rId) {
        FoodClient foodClient = FoodClient.getInstance();
        Call<RecipeGetResponse> call = foodClient.getFoodService().getRecipe(rId);
        call.enqueue(new Callback<RecipeGetResponse>() {
            @Override
            public void onResponse(Call<RecipeGetResponse> call, Response<RecipeGetResponse> response) {
                if (downloaderCallback != null) {
                    downloaderCallback.onRecipeFetched(response.body());
                }
            }

            @Override
            public void onFailure(Call<RecipeGetResponse> call, Throwable t) {

            }
        });
    }

    public interface DownloaderCallback {
        void onRecipesFetched(RecipeSearchResponse recipeSearchResponse);
        void onRecipeFetched(RecipeGetResponse recipeResponse);
    }*/
}
