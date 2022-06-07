package com.anbdevelopers.foodrecipes.requests.responses;

import com.anbdevelopers.foodrecipes.models.Recipe;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecipeResponse
{
    @SerializedName("recipe")
    @Expose()
    private Recipe recipe;

    public Recipe getRecipe()
    {
        return recipe;
    }

    @Override
    public String toString() {
        return "RecipeResponse{" +
                "recipe=" + recipe +
                '}';
    }
}
