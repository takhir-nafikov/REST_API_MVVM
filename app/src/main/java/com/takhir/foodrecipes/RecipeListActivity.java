package com.takhir.foodrecipes;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.takhir.foodrecipes.models.Recipe;
import com.takhir.foodrecipes.requests.RecipeApi;
import com.takhir.foodrecipes.requests.ServiceGenerator;
import com.takhir.foodrecipes.requests.responses.RecipeSearchResponse;
import com.takhir.foodrecipes.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeListActivity extends BaseActivity {

    private static final String TAG = "RecipeListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testRetrofit();
            }
        });
    }

    private void testRetrofit() {
        RecipeApi recipeApi = ServiceGenerator.getRecipeApi();

        Call<RecipeSearchResponse> responseCall = recipeApi
                .searchResponse(
                        Constants.API_KEY,
                        "chicken breast",
                        "1"
                );
        responseCall.enqueue(new Callback<RecipeSearchResponse>() {
            @Override
            public void onResponse(Call<RecipeSearchResponse> call, Response<RecipeSearchResponse> response) {
                Log.d(TAG, "onResponse:" + response.toString());
                if(response.code() == 200) {
                    Log.d(TAG, "onResponse:" + response.body().toString());
                    List<Recipe> recipes = new ArrayList<>(response.body().getRecipes());
                    for (Recipe recipe : recipes) {
                        Log.d(TAG, "onResponse:" + recipe.getTitle());
                    }
                } else {
                    try {
                        Log.d(TAG, "onResponse" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RecipeSearchResponse> call, Throwable t) {

            }
        });
    }
}
