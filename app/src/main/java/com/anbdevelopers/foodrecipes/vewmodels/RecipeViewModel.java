package com.anbdevelopers.foodrecipes.vewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.anbdevelopers.foodrecipes.models.Recipe;
import com.anbdevelopers.foodrecipes.repositories.RecipeRepository;

public class RecipeViewModel extends ViewModel
{
    private RecipeRepository mRecipeRepository;
    private String mRecipeId;

    public Boolean getDidRetrieveRecipe() {
        return mDidRetrieveRecipe;
    }

    public void setDidRetrieveRecipe(Boolean mDidRetrieveRecipe) {
        this.mDidRetrieveRecipe = mDidRetrieveRecipe;
    }

    private Boolean mDidRetrieveRecipe;

    public RecipeViewModel() {
        mRecipeRepository = RecipeRepository.getInstance();
        mDidRetrieveRecipe = false;
    }
    public LiveData<Recipe> getRecipe()
    {
       return mRecipeRepository.getRecipe();
    }
    public LiveData<Boolean> isRecipeRequestTimedOut()
    {
        return mRecipeRepository.isRecipeRequestTimedOut();
    }
    public void searchRecipeById(String recipeId)
    {
        mRecipeId = recipeId;
        mRecipeRepository.searchRecipeById(recipeId);
    }

    public String getRecipeId() {
        return mRecipeId;
    }
}
