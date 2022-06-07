package com.anbdevelopers.foodrecipes.requests;

import com.anbdevelopers.foodrecipes.requests.responses.RecipeResponse;
import com.anbdevelopers.foodrecipes.requests.responses.RecipeSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeApi
{
    //Search
    @GET("api/search")
    Call<RecipeSearchResponse> searchRecipe(
            @Query("key") String key, //will add key appended to url with "?" sign
            @Query("q") String query, //& append q=
            @Query("page") String page
    );

    // Get specific recipe request

    @GET("api/get")
    Call<RecipeResponse> getRecipe(
            @Query("key") String key,
            @Query("rId") String recipe_id
    );
}
