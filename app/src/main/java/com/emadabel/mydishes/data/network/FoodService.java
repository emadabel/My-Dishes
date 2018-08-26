package com.emadabel.mydishes.data.network;

import com.emadabel.mydishes.data.model.RecipeGetResponse;
import com.emadabel.mydishes.data.model.RecipeSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FoodService {
    @GET("search")
    Call<RecipeSearchResponse> listRecipes(
            @Query("q") String query,
            @Query("sort") String sort,
            @Query("page") Integer page);

    @GET("get")
    Call<RecipeGetResponse> getRecipe(@Query("rId") String recipeId);
}
