package com.takhir.foodrecipes.requests;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.takhir.foodrecipes.AppExecutors;
import com.takhir.foodrecipes.models.Recipe;
import com.takhir.foodrecipes.requests.responses.RecipeResponse;
import com.takhir.foodrecipes.requests.responses.RecipeSearchResponse;
import com.takhir.foodrecipes.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

import static com.takhir.foodrecipes.util.Constants.NETWORK_TIMEOUT;

public class RecipeApiClient {

    private static final String TAG = "RecipeApiClient";

    private static  RecipeApiClient instance;
    private MutableLiveData<List<Recipe>> recipes;
    private MutableLiveData<Recipe> recipe;
    private RetrieveRecipesRunnable retrieveRecipesRunnable;
    private RetrieveRecipeRunnable retrieveRecipeRunnable;
    private MutableLiveData<Boolean> recipeRequestTimeout = new MutableLiveData<>();

    public static RecipeApiClient getInstance() {
        if (instance == null) {
            instance = new RecipeApiClient();
        }
        return instance;
    }

    private RecipeApiClient() {
        recipes = new MutableLiveData<>();
        recipe = new MutableLiveData<>();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return recipes;
    }

    public LiveData<Recipe> getRecipe() {
        return recipe;
    }

    public LiveData<Boolean> isRecipeRequestTimedOut(){
        return recipeRequestTimeout;
    }

    public void searchRecipeApi(String query, int pageNumber) {
        if (retrieveRecipesRunnable != null) {
            retrieveRecipesRunnable = null;
        }
        retrieveRecipesRunnable = new RetrieveRecipesRunnable(query, pageNumber);
        final Future handler = AppExecutors.getInstance().networkIo().submit(retrieveRecipesRunnable);

        AppExecutors.getInstance().networkIo().schedule(new Runnable() {
            @Override
            public void run() {
                handler.cancel(true);
            }
        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void searchRecipeById(String recipeId) {
        if (retrieveRecipeRunnable != null) {
            retrieveRecipeRunnable = null;
        }
        retrieveRecipeRunnable = new RetrieveRecipeRunnable(recipeId);

        final Future handler = AppExecutors.getInstance().networkIo().submit(retrieveRecipeRunnable);

        recipeRequestTimeout.setValue(false);
        AppExecutors.getInstance().networkIo().schedule(new Runnable() {
            @Override
            public void run() {
                recipeRequestTimeout.postValue(true);
                handler.cancel(true);
            }
        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private class RetrieveRecipesRunnable implements Runnable {

        private String query;
        private int pageNumber;
        boolean cancelRequest;

        public RetrieveRecipesRunnable(String query, int pageNumber) {
            this.query = query;
            this.pageNumber = pageNumber;
            this.cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getRecipes(query, pageNumber).execute();
                if (cancelRequest) {
                    return;
                }
                if (response.code() == 200) {
                    List<Recipe> list = new ArrayList<>(((RecipeSearchResponse)response.body()).getRecipes());
                    if (pageNumber == 1) {
                       recipes.postValue(list);
                    } else {
                        List<Recipe> currentRecipes = recipes.getValue();
                        currentRecipes.addAll(list);
                        recipes.postValue(currentRecipes);
                    }
                } else {
                    String error = response.errorBody().string();
                    Log.e(TAG, "run: " + error);
                    recipes.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                recipes.postValue(null);
            }

        }

        private Call<RecipeSearchResponse> getRecipes(String query, int pageNumber) {
            return ServiceGenerator.getRecipeApi().getRecipes(
                    Constants.API_KEY,
                    query,
                    String.valueOf(pageNumber)
            );
        }

        private void cancelRequest() {
            Log.d(TAG, "cancelRequest: canceling the search request");
            cancelRequest = true;
        }
    }

    private class  RetrieveRecipeRunnable implements Runnable {

        private String recipeId;
        boolean cancelRequest;

        public RetrieveRecipeRunnable(String recipeId) {
            this.recipeId = recipeId;
            this.cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getRecipe(recipeId).execute();
                if (cancelRequest) {
                    return;
                }
                if (response.code() == 200) {
                    Recipe recipeRes = ((RecipeResponse)response.body()).getRecipe();
                    recipe.postValue(recipeRes);
                } else {
                    String error = response.errorBody().string();
                    Log.e(TAG, "run: " + error);
                    recipe.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                recipe.postValue(null);
            }

        }

        private Call<RecipeResponse> getRecipe(String resiceId) {
            return ServiceGenerator.getRecipeApi().getRecipe(
                    Constants.API_KEY,
                    recipeId
            );
        }

        private void cancelRequest() {
            Log.d(TAG, "cancelRequest: canceling the search request");
            cancelRequest = true;
        }
    }

    public void cancelRequest() {
        if (retrieveRecipesRunnable != null) {
            retrieveRecipesRunnable.cancelRequest();
        }
        if (retrieveRecipeRunnable != null) {
            retrieveRecipeRunnable.cancelRequest();
        }
    }
}
