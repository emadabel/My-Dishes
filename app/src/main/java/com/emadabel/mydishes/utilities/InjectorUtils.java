package com.emadabel.mydishes.utilities;

import android.content.Context;

import com.emadabel.mydishes.data.MyDishesRepository;
import com.emadabel.mydishes.data.database.AppDatabase;
import com.emadabel.mydishes.ui.activity.detail.DetailViewModelFactory;

public class InjectorUtils {

    public static MyDishesRepository provideRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context.getApplicationContext());
        return MyDishesRepository.getInstance(database.recipeDao());
    }

    public static DetailViewModelFactory provideDetailViewModelFactory(Context context, String recipeId) {
        MyDishesRepository repository = provideRepository(context.getApplicationContext());
        return new DetailViewModelFactory(repository, recipeId);
    }
}
