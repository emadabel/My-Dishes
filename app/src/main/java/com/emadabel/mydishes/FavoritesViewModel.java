package com.emadabel.mydishes;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.emadabel.mydishes.database.AppDatabase;
import com.emadabel.mydishes.model.Recipe;

import java.util.List;

public class FavoritesViewModel extends AndroidViewModel {

    private LiveData<List<Recipe>> recipes;

    public FavoritesViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        recipes = database.recipeDao().loadAllFavorites();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return recipes;
    }
}
