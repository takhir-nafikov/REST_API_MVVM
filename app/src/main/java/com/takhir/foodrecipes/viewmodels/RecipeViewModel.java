package com.takhir.foodrecipes.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.takhir.foodrecipes.models.Recipe;
import com.takhir.foodrecipes.repositories.RecipeRepository;

public class RecipeViewModel extends ViewModel {

    private RecipeRepository recipeRepository;
    private String recipeId;
    private boolean didRetrieveRecipe;

    public RecipeViewModel() {
        this.recipeRepository = RecipeRepository.getInstance();
        didRetrieveRecipe = false;
    }

    public LiveData<Recipe> getRecipe() {
        return recipeRepository.getRecipe();
    }

    public LiveData<Boolean> isRecipeRequestTimedOut(){
        return recipeRepository.isRecipeRequestTimedOut();
    }

    public void searchRecipeById(String recipeId) {
        this.recipeId = recipeId;
        recipeRepository.searchRecipeById(recipeId);
    }

    public void setRetrievedRecipe(boolean retrievedRecipe){
        didRetrieveRecipe = retrievedRecipe;
    }

    public boolean didRetrieveRecipe(){
        return didRetrieveRecipe;
    }

    public String getRecipeId() {
        return recipeId;
    }
}
