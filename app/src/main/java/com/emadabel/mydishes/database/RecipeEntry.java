package com.emadabel.mydishes.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "favorites")
public class RecipeEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String publisher;
    private String f2fUrl;
    private List<String> ingredients;
    private String sourceUrl;
    private String recipeId;
    private String imageUrl;
    private Double socialRank;
    private String publisherUrl;
    private String title;

    @Ignore
    public RecipeEntry(String publisher, String f2fUrl, List<String> ingredients, String sourceUrl, String recipeId, String imageUrl, Double socialRank, String publisherUrl, String title) {
        this.publisher = publisher;
        this.f2fUrl = f2fUrl;
        this.ingredients = ingredients;
        this.sourceUrl = sourceUrl;
        this.recipeId = recipeId;
        this.imageUrl = imageUrl;
        this.socialRank = socialRank;
        this.publisherUrl = publisherUrl;
        this.title = title;
    }

    public RecipeEntry(int id, String publisher, String f2fUrl, List<String> ingredients, String sourceUrl, String recipeId, String imageUrl, Double socialRank, String publisherUrl, String title) {
        this.id = id;
        this.publisher = publisher;
        this.f2fUrl = f2fUrl;
        this.ingredients = ingredients;
        this.sourceUrl = sourceUrl;
        this.recipeId = recipeId;
        this.imageUrl = imageUrl;
        this.socialRank = socialRank;
        this.publisherUrl = publisherUrl;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getF2fUrl() {
        return f2fUrl;
    }

    public void setF2fUrl(String f2fUrl) {
        this.f2fUrl = f2fUrl;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getSocialRank() {
        return socialRank;
    }

    public void setSocialRank(Double socialRank) {
        this.socialRank = socialRank;
    }

    public String getPublisherUrl() {
        return publisherUrl;
    }

    public void setPublisherUrl(String publisherUrl) {
        this.publisherUrl = publisherUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
