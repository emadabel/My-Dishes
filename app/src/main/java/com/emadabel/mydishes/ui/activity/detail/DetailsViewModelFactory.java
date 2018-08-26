package com.emadabel.mydishes.ui.activity.detail;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.emadabel.mydishes.data.database.AppDatabase;

public class DetailsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final String mRecipeId;

    public DetailsViewModelFactory(AppDatabase database, String recipeId) {
        this.mDb = database;
        this.mRecipeId = recipeId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DetailsViewModel(mDb, mRecipeId);
    }
}
