package com.emadabel.mydishes.data;

import android.arch.lifecycle.LiveData;

import com.emadabel.mydishes.data.database.RecipeDao;
import com.emadabel.mydishes.data.model.Recipe;

import timber.log.Timber;

public class MyDishesRepository {

    private static final Object LOCK = new Object();
    private static MyDishesRepository sInstance;
    private final RecipeDao mRecipeDao;

    private MyDishesRepository(RecipeDao recipeDao) {
        this.mRecipeDao = recipeDao;
    }

    public static MyDishesRepository getInstance(RecipeDao recipeDao) {
        Timber.d("Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MyDishesRepository(recipeDao);
                Timber.d("Made new repository");
            }
        }
        return sInstance;
    }

    public LiveData<Recipe> getRecipeById(String recipeId) {
        return mRecipeDao.getRecipeById(recipeId);
    }
}
