package com.takhir.foodrecipes.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.takhir.foodrecipes.models.Recipe;
import com.takhir.foodrecipes.requests.RecipeApiClient;

import java.util.List;

public class RecipeRepository {

    private static RecipeRepository instance;
    private RecipeApiClient recipeApiClient;
    private String query;
    private int pageNumber;
    private MutableLiveData<Boolean> isQueryExhausted = new MutableLiveData<>();
    private MediatorLiveData<List<Recipe>> recipes = new MediatorLiveData<>();

    public static RecipeRepository getInstance() {
        if (instance == null) {
            instance = new RecipeRepository();
        }
        return instance;
    }

    private RecipeRepository() {
        recipeApiClient = RecipeApiClient.getInstance();
        initMediators();
    }

    private void initMediators() {
        LiveData<List<Recipe>> recipeListApiSource = recipeApiClient.getRecipes();
        recipes.addSource(recipeListApiSource, new Observer<List<Recipe>>() {
            @Override
            public void onChanged( List<Recipe> recipes) {

                if(recipes != null){
                    RecipeRepository.this.recipes.setValue(recipes);
                    doneQuery(recipes);
                }
                else{
                    // search database cache
                    doneQuery(null);
                }
            }
        });
    }

    private void doneQuery(List<Recipe> list) {
        if(list != null){
            if (list.size() % 30 != 0) {
                isQueryExhausted.setValue(true);
            }
        }
        else{
            isQueryExhausted.setValue(true);
        }
    }

    public LiveData<Boolean> isQueryExhausted() {
        return isQueryExhausted;
    }

    public LiveData<List<Recipe>> getRecipes() {
        return recipes;
    }

    public LiveData<Recipe> getRecipe() {
        return recipeApiClient.getRecipe();
    }

    public void searchRecipeById(String recipeId) {
        recipeApiClient.searchRecipeById(recipeId);
    }

    public void searchRecipesApi(String query, int pageNumber) {
        if (pageNumber == 0) {
            pageNumber = 1;
        }
        this.query = query;
        this.pageNumber = pageNumber;
        isQueryExhausted.setValue(false);
        recipeApiClient.searchRecipeApi(query, pageNumber);
    }

    public void searchNextPage() {
        searchRecipesApi(query, pageNumber + 1);
    }

    public void cancelRequest() {
        recipeApiClient.cancelRequest();
    }

    public LiveData<Boolean> isRecipeRequestTimedOut(){
        return recipeApiClient.isRecipeRequestTimedOut();
    }
}
