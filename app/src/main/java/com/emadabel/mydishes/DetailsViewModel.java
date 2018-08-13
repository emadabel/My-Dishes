package com.emadabel.mydishes;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.emadabel.mydishes.database.AppDatabase;
import com.emadabel.mydishes.model.Recipe;

public class DetailsViewModel extends ViewModel {

    private LiveData<Recipe> recipe;

    public DetailsViewModel(AppDatabase database, String recipeId) {
        recipe = database.recipeDao().loadRecipeById(recipeId);
    }

    public LiveData<Recipe> getRecipe() {
        return recipe;
    }
}
