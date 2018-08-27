package com.emadabel.mydishes.ui.activity.detail;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.emadabel.mydishes.data.MyDishesRepository;

public class DetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final MyDishesRepository mRepository;
    private final String mRecipeId;

    public DetailViewModelFactory(MyDishesRepository repository, String recipeId) {
        this.mRepository = repository;
        this.mRecipeId = recipeId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DetailViewModel(mRepository, mRecipeId);
    }
}
