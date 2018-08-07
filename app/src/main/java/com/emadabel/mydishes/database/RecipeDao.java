package com.emadabel.mydishes.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface RecipeDao {

    @Query("SELECT * FROM favorites")
    List<RecipeEntry> loadAllFavorites();

    @Insert
    void insertFavoriteItem(RecipeEntry recipe);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFavoriteItem(RecipeEntry recipe);

    @Delete
    void deleteFavoriteItem(RecipeEntry recipe);
}
