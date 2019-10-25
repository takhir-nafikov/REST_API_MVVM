package com.takhir.foodrecipes.util;

import android.util.Log;

import com.takhir.foodrecipes.models.Recipe;

import java.util.List;

public class Testing {

    public static void prinnRecipes(List<Recipe> list, String tag) {
        for (Recipe recipe : list) {
            Log.d(tag, "onChanged: " + recipe.getTitle());
        }
    }
}
