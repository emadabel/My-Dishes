package com.emadabel.mydishes.data.model;

import com.google.gson.annotations.SerializedName;

public class RecipeGetResponse {
    @SerializedName("recipe")
    private Recipe recipe;

    public Recipe getRecipe() {
        return recipe;
    }
}
