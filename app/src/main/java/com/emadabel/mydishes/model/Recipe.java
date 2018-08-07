package com.emadabel.mydishes.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Recipe {
    @SerializedName("publisher")
    private String publisher;
    @SerializedName("f2f_url")
    private String f2fUrl;
    @SerializedName("ingredients")
    private List<String> ingredients;
    @SerializedName("source_url")
    private String sourceUrl;
    @SerializedName("recipe_id")
    private String recipeId;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("social_rank")
    private Double socialRank;
    @SerializedName("publisher_url")
    private String publisherUrl;
    @SerializedName("title")
    private String title;

    public String getPublisher() {
        return publisher;
    }

    public String getF2fUrl() {
        return f2fUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Double getSocialRank() {
        return socialRank;
    }

    public String getPublisherUrl() {
        return publisherUrl;
    }

    public List<String> getIngredients() {
        return ingredients;
    }
}
