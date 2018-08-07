package com.emadabel.mydishes.model;

import java.util.ArrayList;
import java.util.List;

public class RecipeSearchResponse {
    public List<Recipe> recipes;

    public RecipeSearchResponse() {
        recipes = new ArrayList<>();
    }
}
