package com.emadabel.mydishes.ui.activity.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.emadabel.mydishes.data.MyDishesRepository;
import com.emadabel.mydishes.data.model.Recipe;

public class DetailViewModel extends ViewModel {

    private final LiveData<Recipe> mRecipe;

    private final String mRecipeId;
    private final MyDishesRepository mRepository;

    public DetailViewModel(MyDishesRepository repository, String recipeId) {
        mRepository = repository;
        mRecipeId = recipeId;
        mRecipe = mRepository.getRecipeById(mRecipeId);
    }

    public LiveData<Recipe> getRecipe() {
        return mRecipe;
    }
}
