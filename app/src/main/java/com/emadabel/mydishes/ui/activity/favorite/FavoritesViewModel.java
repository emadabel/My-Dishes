package com.emadabel.mydishes.ui.activity.favorite;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.emadabel.mydishes.data.database.AppDatabase;
import com.emadabel.mydishes.data.model.Recipe;

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
