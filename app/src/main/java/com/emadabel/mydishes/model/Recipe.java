package com.emadabel.mydishes.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Recipe implements Parcelable {
    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
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

    private Recipe(Parcel in) {
        publisher = in.readString();
        f2fUrl = in.readString();
        ingredients = in.createStringArrayList();
        sourceUrl = in.readString();
        recipeId = in.readString();
        imageUrl = in.readString();
        if (in.readByte() == 0) {
            socialRank = null;
        } else {
            socialRank = in.readDouble();
        }
        publisherUrl = in.readString();
        title = in.readString();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(publisher);
        dest.writeString(f2fUrl);
        dest.writeString(title);
        dest.writeString(sourceUrl);
        dest.writeString(recipeId);
        dest.writeString(imageUrl);
        dest.writeDouble(socialRank);
        dest.writeString(publisherUrl);
        dest.writeStringList(ingredients);
    }
}
