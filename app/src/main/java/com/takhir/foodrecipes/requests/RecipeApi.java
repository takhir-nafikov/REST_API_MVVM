package com.takhir.foodrecipes.requests;

import com.takhir.foodrecipes.models.Recipe;
import com.takhir.foodrecipes.requests.responses.RecipeResponse;
import com.takhir.foodrecipes.requests.responses.RecipeSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeApi {

    @GET("api/search")
    Call<RecipeSearchResponse> searchResponse(
        @Query("key") String key,
        @Query("q") String query,
        @Query("page") String page
    );

    @GET("api/get")
    Call<RecipeResponse> getRecipe(
        @Query("key") String key,
        @Query("rId") String recipe_id
    );
}
