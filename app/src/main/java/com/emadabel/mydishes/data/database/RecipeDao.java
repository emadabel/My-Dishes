package com.emadabel.mydishes.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.emadabel.mydishes.data.model.Recipe;

import java.util.List;

@Dao
public interface RecipeDao {

    @Query("SELECT * FROM favorites")
    LiveData<List<Recipe>> loadAllFavorites();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavoriteItem(Recipe recipe);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFavoriteItem(Recipe recipe);

    @Delete
    void deleteFavoriteItem(Recipe recipe);

    @Query("SELECT * FROM favorites WHERE recipeId = :rId")
    LiveData<Recipe> loadRecipeById(String rId);

    @Query("SELECT * FROM favorites")
    List<Recipe> loadFavoritesForWidget();
}
