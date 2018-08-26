package com.emadabel.mydishes.data.database;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class IngredientsConverter {
    @TypeConverter
    public static List<String> toIngredientsList(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<List<String>>() {
        }.getType());
    }

    @TypeConverter
    public static String toIngredientsString(List<String> ingredients) {
        Gson gson = new Gson();
        return gson.toJson(ingredients);
    }
}
